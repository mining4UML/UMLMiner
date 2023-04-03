package plugin.mining.listener;

import java.time.LocalDateTime;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectListener;

import plugin.mining.utils.Application;
import plugin.mining.utils.Logger;

public class ProjectListener implements IProjectListener {
	private final Logger logger = new Logger(ProjectListener.class);
	private static final ProjectDiagramListener projectDiagramListener = new ProjectDiagramListener();
	private String projectName;

	public ProjectListener() {
		// Empty
	}

	@Override
	public void projectAfterOpened(IProject project) {
		// Empty
	}

	@Override
	public void projectNewed(IProject project) {
		projectName = project.getName();
		logger.info("New project \"%s\" created", projectName);
		project.addProjectDiagramListener(projectDiagramListener);
	}

	@Override
	public void projectOpened(IProject project) {
		projectName = project.getName();
		logger.info("Project \"%s\" opened", projectName);
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
