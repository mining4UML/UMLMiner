package com.plugin.mining.listeners;

import com.plugin.mining.logging.Logger;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectListener;

public class ProjectListener implements IProjectListener {
	private static final Logger logger = new Logger(ProjectListener.class);
	private static final ProjectDiagramListener projectDiagramListener = new ProjectDiagramListener();
	private String projectName;

	public ProjectListener(IProject project) {
		projectName = project.getName();

		if (project.getProjectFile() == null) {
			logger.info("Temp project \"%s\" opened", projectName);
			Logger.createTrace(project);
			project.addProjectDiagramListener(projectDiagramListener);
		}
	}

	@Override
	public void projectAfterOpened(IProject project) {
		logger.info("Project \"%s\" after opened", projectName);
	}

	@Override
	public void projectNewed(IProject project) {
		projectName = project.getName();

		logger.info("New project \"%s\" created", projectName);
		Logger.createTrace(project);
		project.addProjectDiagramListener(projectDiagramListener);
	}

	@Override
	public void projectOpened(IProject project) {
		projectName = project.getName();

		logger.info("Project \"%s\" opened", projectName);
		Logger.createTrace(project);
		project.addProjectDiagramListener(projectDiagramListener);
	}

	@Override
	public void projectPreSave(IProject project) {
		logger.info("Project \"%s\" is going to save", projectName);
	}

	@Override
	public void projectRenamed(IProject project) {
		String projectNewName = project.getName();
		logger.info("Project \"%s\" is renamed to \"%s\"", projectName,
				projectNewName);
		projectName = projectNewName;
	}

	@Override
	public void projectSaved(IProject project) {
		logger.info("Project \"%s\" is saved", projectName);
	}

}
