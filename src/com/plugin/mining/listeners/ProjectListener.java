package com.plugin.mining.listeners;

import com.plugin.mining.logging.LogActivity;
import com.plugin.mining.logging.Logger;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectListener;

public class ProjectListener implements IProjectListener {
	private static final Logger logger = new Logger(ProjectListener.class);
	private static final ProjectModelListener projectModelListener = new ProjectModelListener();
	private static final ProjectDiagramListener projectDiagramListener = new ProjectDiagramListener();

	public ProjectListener(IProject project) {

		if (project.getProjectFile() == null) {
			logger.info("Temp project \"%s\" opened", project.getName());
			Logger.loadLog();
			Logger.createTrace(project);
			project.addProjectDiagramListener(projectDiagramListener);
			project.addProjectModelListener(projectModelListener);
		}
	}

	@Override
	public void projectAfterOpened(IProject project) {
		logger.info("Project \"%s\" after opened", project.getName());
	}

	@Override
	public void projectNewed(IProject project) {
		logger.info("New project \"%s\" created", project.getName());
		Logger.loadLog();
		Logger.createTrace(project);
		project.addProjectDiagramListener(projectDiagramListener);
		project.addProjectModelListener(projectModelListener);
	}

	@Override
	public void projectOpened(IProject project) {
		logger.info("Project \"%s\" opened", project.getName());
		Logger.loadLog();
		Logger.createTrace(project);
		Logger.createEvent(LogActivity.ADD_PROJECT, project);
		project.addProjectDiagramListener(projectDiagramListener);
		project.addProjectModelListener(projectModelListener);
	}

	@Override
	public void projectPreSave(IProject project) {
		logger.info("Project \"%s\" is going to save", project.getName());
	}

	@Override
	public void projectRenamed(IProject project) {
		logger.info("Project is renamed to \"%s\"", project.getName());
		Logger.createEvent(LogActivity.UPDATE_PROJECT, project, "name", project.getName());
	}

	@Override
	public void projectSaved(IProject project) {
		logger.info("Project \"%s\" is saved", project.getName());
		Logger.saveLog();
	}

}
