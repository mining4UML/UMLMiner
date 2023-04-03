package plugin.mining.listener;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectModelListener;

import plugin.mining.utils.Logger;

public class ProjectModelListener implements IProjectModelListener {
	private final Logger logger = new Logger(ProjectModelListener.class);
	private static final PropertyChangeListener propertyChangeListener = new PropertyChangeListener();

	@Override
	public void modelAdded(IProject project, IModelElement modelElement) {
		logger.info("%s model element %s added", modelElement.getModelType(), modelElement.getName());
		modelElement.addPropertyChangeListener(propertyChangeListener);
	}

	@Override
	public void modelRemoved(IProject project, IModelElement modelElement) {
		logger.info("%s model element %s removed", modelElement.getModelType(), modelElement.getName());
		modelElement.removePropertyChangeListener(propertyChangeListener);
	}

}