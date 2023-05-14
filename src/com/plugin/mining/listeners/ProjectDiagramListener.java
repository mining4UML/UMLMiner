package com.plugin.mining.listeners;

import com.plugin.mining.logging.LogActivity;
import com.plugin.mining.logging.Logger;
import com.plugin.mining.util.Application;
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
		logger.info(String.format("%s \"%s\" added", diagramUIModel.getType(),
				diagramUIModel.getName()));

		Application.runDelayed(() -> {
			Logger.createEvent(LogActivity.ADD_DIAGRAM, diagramUIModel);
			diagramUIModel.addDiagramListener(new DiagramListener(diagramUIModel));
		});
	}

	@Override
	public void diagramRemoved(IProject project, IDiagramUIModel diagramUIModel) {
		logger.info(String.format("%s \"%s\" removed", diagramUIModel.getType(),
				diagramUIModel.getName()));
		Logger.createEvent(LogActivity.REMOVE_DIAGRAM, diagramUIModel);
	}

}
