package plugin.mining.listeners.property;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IHasChildrenBaseModelElement;

import plugin.mining.logging.LogActivity;
import plugin.mining.logging.Logger;
import plugin.mining.logging.LogActivity.Type;

/**
 * 
 * @author pasqualeardimento
 *
 */

class ClassPropertyChangeListener extends AbstractPropertyChangeListener<IClass> {

	public void propertyChange(IClass classElement, String propertyName, Object oldValue, Object newValue) {
		if (propertyName.equals("childAdded")) {
			IHasChildrenBaseModelElement childElement = (IHasChildrenBaseModelElement) newValue;

			Logger.createEvent(LogActivity.instance(Type.ADD, childElement.getClass()), childElement);
			childElement.addPropertyChangeListener(PropertyChangeListenerFactory.getInstance(childElement));
		} else if (propertyName.equals("childRemoved")) {
			IHasChildrenBaseModelElement childElement = (IHasChildrenBaseModelElement) oldValue;

			Logger.createEvent(LogActivity.instance(Type.REMOVE, childElement.getClass()), childElement);
		}
	}

}
