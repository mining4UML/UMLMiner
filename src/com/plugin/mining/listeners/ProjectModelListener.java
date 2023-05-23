package com.plugin.mining.listeners;

import com.plugin.mining.logging.LogActivity;
import com.plugin.mining.logging.LogActivity.ActionType;
import com.plugin.mining.logging.LogActivity.ModelType;
import com.plugin.mining.logging.Logger;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectModelListener;

public class ProjectModelListener implements IProjectModelListener {
	private static final Logger logger = new Logger(ProjectModelListener.class);

	public ProjectModelListener() {
		// Empty
	}

	@Override
	public void modelAdded(IProject project, IModelElement modelElement) {
		logger.info("%s model element added", modelElement.getModelType());

		LogActivity logActivity = LogActivity.getInstance(ActionType.ADD, modelElement);

		Logger.createEvent(logActivity, modelElement, ModelType.PROJECT);
	}

	@Override
	public void modelRemoved(IProject project, IModelElement modelElement) {
		logger.info("%s model element removed", modelElement.getModelType());
		LogActivity logActivity = LogActivity.getInstance(ActionType.REMOVE, modelElement);

		Logger.createEvent(logActivity, modelElement, ModelType.PROJECT);
	}

}
