package com.uniba.mining.listeners;

import com.uniba.mining.dialogs.FeedbackHandler;
import com.uniba.mining.logging.LogActivity;
import com.uniba.mining.logging.Logger;
import com.uniba.mining.utils.Application;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectDiagramListener;

public class ProjectDiagramListener implements IProjectDiagramListener {
	private final Logger logger = new Logger(ProjectDiagramListener.class);

	public ProjectDiagramListener() {
		// Empty
	}

	@Override
	public void diagramAdded(IProject project, IDiagramUIModel diagramUIModel) {
		logger.info(String.format("%s \"%s\" added", diagramUIModel.getType(), diagramUIModel.getName()));

		Application.scheduleSubmit(() -> {
			if (!Logger.hasDiagram(diagramUIModel))
				Logger.createEvent(LogActivity.ADD_DIAGRAM, diagramUIModel);
			diagramUIModel.addDiagramListener(new DiagramListener(diagramUIModel));
		});
		// quando creo un nuovo diagramma aggiorno il pannello
		FeedbackHandler.getInstance().showFeedbackPanel(diagramUIModel); // Chiamata al metodo statico
	}

	@Override
	public void diagramRemoved(IProject project, IDiagramUIModel diagramUIModel) {
		logger.info(String.format("%s \"%s\" removed", diagramUIModel.getType(), diagramUIModel.getName()));

		Logger.createEvent(LogActivity.REMOVE_DIAGRAM, diagramUIModel);
		// quando creo un nuovo diagramma aggiorno il pannello
		FeedbackHandler.getInstance().showFeedbackPanel(Application.getDiagram()); // Chiamata al metodo statico

	}

}
