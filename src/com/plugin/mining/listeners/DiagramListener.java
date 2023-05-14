package com.plugin.mining.listeners;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.plugin.mining.listeners.property.PropertyChangeListenerFactory;
import com.plugin.mining.logging.LogActivity;
import com.plugin.mining.logging.LogExtractor;
import com.plugin.mining.logging.LogActivity.ActionType;
import com.plugin.mining.logging.Logger;
import com.plugin.mining.util.Application;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramListener;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IRelationship;

public class DiagramListener implements IDiagramListener {
	private static final Logger logger = new Logger(DiagramListener.class);
	private final Set<IModelElement> modelElements = new HashSet<>();

	public DiagramListener(IDiagramUIModel diagramUIModel) {
		IDiagramElement[] diagramElements = diagramUIModel.toDiagramElementArray();
		for (IDiagramElement diagramElement : diagramElements) {
			IModelElement modelElement = diagramElement.getModelElement();
			modelElements.add(modelElement);
			DiagramElementListener diagramElementListener = new DiagramElementListener(modelElement);
			diagramElement.addDiagramElementListener(diagramElementListener);
		}
	}

	@Override
	public void diagramElementAdded(IDiagramUIModel diagramUIModel, IDiagramElement diagramElement) {
		IModelElement modelElement = diagramElement.getModelElement();
		modelElements.add(modelElement);

		logger.info("%s element added to the diagram", modelElement.getModelType());
		LogActivity logActivity = modelElement instanceof IRelationship ? LogActivity.ADD_RELATIONSHIP
				: LogActivity.getInstance(ActionType.ADD, modelElement.getModelType());
		Application.runDelayed(() -> {
			Logger.createEvent(logActivity, modelElement);
			diagramElement.addDiagramElementListener(new DiagramElementListener(modelElement));
		});

	}

	@Override
	public void diagramElementRemoved(IDiagramUIModel diagramUIModel, IDiagramElement diagramElement) {
		IDiagramElement[] diagramElements = diagramUIModel.toDiagramElementArray();
		IModelElement modelElementRemoved = modelElements.stream()
				.filter(modelElement -> Arrays.stream(diagramElements)
						.noneMatch(t -> t.getModelElement().equals(modelElement)))
				.findFirst().orElse(null);

		if (modelElementRemoved == null)
			return;

		logger.info("%s element removed from the diagram", modelElementRemoved.getModelType());
		LogActivity logActivity = modelElementRemoved instanceof IRelationship ? LogActivity.REMOVE_RELATIONSHIP
				: LogActivity.getInstance(ActionType.REMOVE, modelElementRemoved.getModelType());
		Logger.createEvent(logActivity, modelElementRemoved);
		modelElements.remove(modelElementRemoved);
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
