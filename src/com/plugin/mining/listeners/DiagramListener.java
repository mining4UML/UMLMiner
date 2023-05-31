package com.plugin.mining.listeners;

import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.plugin.mining.listeners.property.PropertyChangeListenerFactory;
import com.plugin.mining.logging.LogActivity;
import com.plugin.mining.logging.LogActivity.ActionType;
import com.plugin.mining.logging.LogActivity.ModelType;
import com.plugin.mining.utils.Application;
import com.plugin.mining.logging.LogExtractor;
import com.plugin.mining.logging.Logger;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramListener;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;

public class DiagramListener implements IDiagramListener {
	private static final Logger logger = new Logger(DiagramListener.class);
	private final Map<String, IModelElement> modelElements = new HashMap<>();
	private IDiagramUIModel diagramUIModel;

	public DiagramListener(IDiagramUIModel diagramUIModel) {
		this.diagramUIModel = diagramUIModel;
		IDiagramElement[] diagramElements = diagramUIModel.toDiagramElementArray();
		for (IDiagramElement diagramElement : diagramElements) {
			String diagramElementId = diagramElement.getId();
			IModelElement modelElement = diagramElement.getModelElement();
			modelElements.put(diagramElementId, modelElement);
			LogExtractor.addDiagramUIModel(modelElement, diagramUIModel);
			LogExtractor.addParentModelElementRecursive(modelElement);
			diagramElement.addDiagramElementListener(new DiagramElementListener(diagramElement));
		}
	}

	public IDiagramUIModel getDiagramUIModel() {
		return diagramUIModel;
	}

	@Override
	public void diagramElementAdded(IDiagramUIModel diagramUIModel, IDiagramElement diagramElement) {
		String diagramElementId = diagramElement.getId();
		IModelElement modelElement = diagramElement.getModelElement();

		modelElements.put(diagramElementId, modelElement);
		LogExtractor.addDiagramUIModel(modelElement, diagramUIModel);

		logger.info("%s element added to the diagram", modelElement.getModelType());
		LogActivity logActivity = LogExtractor.extractLogActivity(ActionType.ADD, modelElement);
		Application.runDelayed(() -> {
			Logger.createEvent(logActivity, modelElement);
			diagramElement.addDiagramElementListener(new DiagramElementListener(diagramElement));
		});

	}

	@Override
	public void diagramElementRemoved(IDiagramUIModel diagramUIModel, IDiagramElement diagramElement) {
		String diagramElementId = diagramElement.getId();
		IModelElement modelElementRemoved = modelElements.get(diagramElementId);

		if (modelElementRemoved == null)
			return;

		logger.info("%s element removed from the diagram", modelElementRemoved.getModelType());
		LogActivity logActivity = LogExtractor.extractLogActivity(ActionType.REMOVE, modelElementRemoved);
		if (logActivity.getModelType() != ModelType.VIEW) {
			PropertyChangeListener propertyChangeListener = PropertyChangeListenerFactory
					.getPropertyChangeListener(modelElementRemoved);
			modelElementRemoved.removePropertyChangeListener(propertyChangeListener);
		}
		Logger.createEvent(logActivity, modelElementRemoved);
		modelElements.remove(diagramElementId);
	}

	@Override
	public void diagramUIModelLoaded(IDiagramUIModel diagramUIModel) {
		logger.info("%s \"%s\" loaded", diagramUIModel.getType(), diagramUIModel.getName());
	}

	@Override
	public void diagramUIModelRenamed(IDiagramUIModel diagramUIModel) {
		// Empty
	}

	@Override
	public void diagramUIModelPropertyChanged(IDiagramUIModel diagramUIModel, String propertyName,
			Object oldValue, Object newValue) {
		if (!(propertyName.equals("name")))
			return;

		String propertyValue = LogExtractor.extractStringValue(newValue);
		logger.info("%s \"%s\" %s property changed to \"%s\"", diagramUIModel.getType(), diagramUIModel.getName(),
				propertyName, propertyValue);
		Logger.createEvent(LogActivity.UPDATE_DIAGRAM, diagramUIModel, propertyName, propertyValue);
	}

}
