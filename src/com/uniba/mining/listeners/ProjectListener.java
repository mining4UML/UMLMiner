package com.uniba.mining.listeners;

import com.uniba.mining.dialogs.FeedbackHandler;
import com.uniba.mining.listeners.property.PropertyChangeListenerFactory;
import com.uniba.mining.logging.LogActivity;
import com.uniba.mining.logging.Logger;
import com.uniba.mining.utils.Application;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectListener;

//public class ProjectListener implements IProjectListener {
//	private static final Logger logger = new Logger(ProjectListener.class);
//	private static final ProjectModelListener projectModelListener = new ProjectModelListener();
//	private static final ProjectDiagramListener projectDiagramListener = new ProjectDiagramListener();
//
//	public void init(IProject project) {
//		PropertyChangeListenerFactory.initPropertyChangeListeners();
//		Logger.loadLog();
//		Logger.createTrace(project);
//		project.addProjectDiagramListener(projectDiagramListener);
//		project.addProjectModelListener(projectModelListener);	}
//
//	public ProjectListener(IProject project) {
//		if (project.getProjectFile() == null) {
//			logger.info("Temp project \"%s\" opened", project.getName() + " id:" +project.getId());
//			init(project);
//		}
//	}
//
//	@Override
//	public void projectAfterOpened(IProject project) {
//		logger.info("Project \"%s\" after opened", project.getName()+ " id:" + project.getId());
//		// se apro un progetto esistente
//		//FeedbackHandler.getInstance().showFeedbackPanel(project); // Chiamata al metodo statico
//		
//	}
//
//	@Override
//	public void projectNewed(IProject project) {
//		logger.info("New project \"%s\" created", project.getName()+ " id new project:"+project.getId());
//		init(project);
//		//FeedbackHandler.getInstance().showFeedbackPanel(project); // Chiamata al metodo statico
//	}
//
//	@Override
//	public void projectOpened(IProject project) {
//		logger.info("Project \"%s\" opened", project.getName());
//		init(project);
//		Logger.createEvent(LogActivity.ADD_PROJECT, project);
//	}
//
//	@Override
//	public void projectPreSave(IProject project) {
//		logger.info("Project \"%s\" is going to save", project.getName());
//	}
//
//	@Override
//	public void projectRenamed(IProject project) {
//		logger.info("Project is renamed to \"%s\"", project.getName());
//		Logger.createEvent(LogActivity.UPDATE_PROJECT, project, "name", project.getName());
//		// se creo un nuovo progetto e gli assegno un nome
//		//FeedbackHandler.getInstance().showFeedbackPanel(project); // Chiamata al metodo statico
//	}
//
//	@Override
//	public void projectSaved(IProject project) {
//		logger.info("Project \"%s\" is saved", project.getName());
//		Logger.saveLog();
//	}
//
//}

public class ProjectListener implements IProjectListener {
	private static final Logger logger = new Logger(ProjectListener.class);
	private static final ProjectModelListener projectModelListener = new ProjectModelListener();
	private static final ProjectDiagramListener projectDiagramListener = new ProjectDiagramListener();

	public void init(IProject project) {
		PropertyChangeListenerFactory.initPropertyChangeListeners();
		Logger.loadLog();
		Logger.createTrace(project);
		project.addProjectDiagramListener(projectDiagramListener);
		project.addProjectModelListener(projectModelListener);
	}

	public ProjectListener(IProject project) {
		if (project.getProjectFile() == null) {
			logger.info("Temp project \"%s\" opened", project.getName() + " id:" + project.getId());
			init(project);
		}
	}

	@Override
	public void projectAfterOpened(IProject project) {
		logger.info("Project \"%s\" after opened", project.getName() + " id:" + project.getId());
		if (Application.getDiagram() != null) {
			System.out.println(Application.getDiagram().getId());
			// qui si dovrebbe verificare che ci sia almeno un diagramma altrimenti feedback
			// del menu disabilitato
			FeedbackHandler.getInstance().showFeedbackPanel(Application.getDiagram()); // Chiamata al metodo statico
		}
	}

	@Override
	public void projectNewed(IProject project) {
		logger.info("New project \"%s\" created", project.getName() + " id new project:" + project.getId());
		init(project);
		// showFeedbackIfDiagramOpened(project);
	}

	@Override
	public void projectOpened(IProject project) {
		logger.info("Project \"%s\" opened", project.getName());
		init(project);
		Logger.createEvent(LogActivity.ADD_PROJECT, project);
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

	private void showFeedbackIfDiagramOpened(IProject project) {
		IDiagramUIModel currentDiagram = ApplicationManager.instance().getDiagramManager().getActiveDiagram();
		if (currentDiagram != null) {
			// FeedbackHandler.getInstance().showFeedbackPanel(project);
		}
	}
}
