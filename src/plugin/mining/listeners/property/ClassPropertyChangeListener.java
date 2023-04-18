package plugin.mining.listeners.property;

import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IHasChildrenBaseModelElement;
import com.vp.plugin.model.IModelElement;

import plugin.mining.utils.Logger;

/**
 * 
 * @author pasqualeardimento
 *
 */

class ClassPropertyChangeListener extends AbstractPropertyChangeListener<IClass> {
	private static final Logger logger = new Logger(ClassPropertyChangeListener.class);

	public void propertyChange(IClass classElement, String propertyName, Object oldValue, Object newValue) {
		if (propertyName.equals("childAdded")) {
			IHasChildrenBaseModelElement childElement = (IHasChildrenBaseModelElement) newValue;
			childElement.addPropertyChangeListener(PropertyChangeListenerFactory.getInstance(childElement));
			logger.info("%s \"%s\" \"%s\" %s added", classElement.getModelType(),
					classElement.getName(), childElement.getName(),
					childElement.getModelType());
		} else if (propertyName.equals("childRemoved")) {
			IHasChildrenBaseModelElement childElement = (IHasChildrenBaseModelElement) oldValue;
			logger.info("%s \"%s\" \"%s\" %s removed", classElement.getModelType(),
					classElement.getName(), childElement.getName(),
					childElement.getModelType());
		}
	}

}
