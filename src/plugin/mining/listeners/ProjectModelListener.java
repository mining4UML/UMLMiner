package plugin.mining.listeners;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectModelListener;

import plugin.mining.logging.Logger;

public class ProjectModelListener implements IProjectModelListener {
	private static final Logger logger = new Logger(ProjectModelListener.class);

	public ProjectModelListener() {
		// Empty
	}

	@Override
	public void modelAdded(IProject project, IModelElement modelElement) {
		logger.info("%s model element %s added", modelElement.getModelType(), modelElement.getName());
	}

	@Override
	public void modelRemoved(IProject project, IModelElement modelElement) {
		logger.info("%s model element %s removed", modelElement.getModelType(), modelElement.getName());
	}

}