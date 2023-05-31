package com.plugin.mining.listeners;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.plugin.mining.logging.LogActivity;
import com.plugin.mining.logging.LogActivity.ActionType;
import com.plugin.mining.logging.LogActivity.ModelType;
import com.plugin.mining.logging.Logger;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectModelListener;
import com.vp.plugin.model.IValueSpecification;

public class ProjectModelListener implements IProjectModelListener {
	private static final Logger logger = new Logger(ProjectModelListener.class);
	private static final Set<String> modelTypesExcluded = new HashSet<>(
			Arrays.asList("ModelRelationshipContainer", "Stereotype"));

	public ProjectModelListener() {
		// Empty
	}

	@Override
	public void modelAdded(IProject project, IModelElement modelElement) {
		logger.info("%s model element added", modelElement.getModelType());
		if (modelTypesExcluded.contains(modelElement.getModelType()))
			return;

		if (modelElement instanceof IValueSpecification) {
			Logger.createEvent(LogActivity.UPDATE_PROJECT, project, "author",
					project.getProjectProperties().getAuthor());
			return;
		}

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
