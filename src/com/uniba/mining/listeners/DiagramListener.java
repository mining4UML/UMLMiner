package com.uniba.mining.listeners;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import com.uniba.mining.dialogs.FeedbackHandler;
import com.uniba.mining.listeners.property.PropertyChangeListenerFactory;
import com.uniba.mining.logging.LogActivity;
import com.uniba.mining.logging.LogActivity.ActionType;
import com.uniba.mining.logging.LogActivity.ModelType;
import com.uniba.mining.logging.LogExtractor;
import com.uniba.mining.logging.Logger;
import com.uniba.mining.utils.Application;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramListener2;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;

public class DiagramListener implements IDiagramListener2 {
	private static final Logger logger = new Logger(DiagramListener.class);
	private static String diagramId;
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
		logger.info("DiagramListener initizialization");
		diagramId = diagramUIModel.getId();
	}

	public IDiagramUIModel getDiagramUIModel() {
		logger.info("getDiagramUIModel(); ottengo il riferimento al diagramma");
		return diagramUIModel;
	}

	@Override
	public void diagramElementAdded(IDiagramUIModel diagramUIModel, IDiagramElement diagramElement) {
		String diagramElementId = diagramElement.getId();
		IModelElement modelElement = diagramElement.getModelElement();

		if(modelElement != null) {
			modelElements.put(diagramElementId, modelElement);
			LogExtractor.addDiagramUIModel(modelElement, diagramUIModel);

			logger.info("%s element added to the diagram", modelElement.getModelType());
			LogActivity logActivity = LogExtractor.extractLogActivity(ActionType.ADD, modelElement);
			Application.scheduleSubmit(() -> {
				Logger.createEvent(logActivity, modelElement);
				diagramElement.addDiagramElementListener(new DiagramElementListener(diagramElement));
			});
		}
		else {
			logger.info("%s Element was not added to the diagram!!!");
			System.out.println("modelElement null!");
		}

		// FeedbackHandler.getInstance().showFeedbackPanel(Application.getDiagram());

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


		System.out.println("Diagram loaded: " + diagramUIModel.getName());
		
		
		logger.info("Diagram loaded: %s \"%s\"", diagramUIModel.getType(), diagramUIModel.getName());
		// da correggere!!!
		Logger.createEvent(LogActivity.UPDATE_DIAGRAM, diagramUIModel, "diagramUIModelLoaded", "diagramUIModelLoaded");

		

		IDiagramUIModel active = ApplicationManager.instance().getDiagramManager().getActiveDiagram();
		if (active != null && active.equals(diagramUIModel)) {
			System.out.println("Diagram is also currently displayed to the user.");
			// codice da eseguire quando è effettivamente visibile
		}



	}

	@Override
	public void diagramUIModelRenamed(IDiagramUIModel diagramUIModel) {
		// Empty
	}

	@Override
	public void diagramUIModelPropertyChanged(IDiagramUIModel diagramUIModel, String propertyName, Object oldValue,
			Object newValue) {
		if (!(propertyName.equals("name")))
			return;

		String propertyValue = LogExtractor.extractStringValue(newValue);
		logger.info("%s \"%s\" %s property changed to \"%s\"", diagramUIModel.getType(), diagramUIModel.getName(),
				propertyName, propertyValue);
		Logger.createEvent(LogActivity.UPDATE_DIAGRAM, diagramUIModel, propertyName, propertyValue);
	}

	@Override
	public void selectionChanged(IDiagramUIModel diagramUIModel) {
		if (!diagramId.equals(diagramUIModel.getId())) {
			// Questo metodo viene chiamato quando la selezione del diagramma cambia
			logger.info("Diagram selection changed: %s \"%s\" \"%s\" ", 
					diagramUIModel.getType(), diagramUIModel.getName(), 
					diagramUIModel.getId());
			diagramId= diagramUIModel.getId();
			//FeedbackHandler.getInstance().showFeedbackPanel(diagramUIModel);
		}		

	}

}
