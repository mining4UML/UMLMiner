package plugin.mining.listeners.property;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IHasChildrenBaseModelElement;

import plugin.mining.logging.LogActivity;
import plugin.mining.logging.Logger;
import plugin.mining.logging.LogActivity.ActionType;

/**
 * 
 * @author pasqualeardimento
 *
 */

class ClassPropertyChangeListener extends AbstractPropertyChangeListener<IClass> {

	public void propertyChange(IClass classElement, String propertyName, Object oldValue, Object newValue) {
		if (propertyName.equals("childAdded")) {
			IHasChildrenBaseModelElement childElement = (IHasChildrenBaseModelElement) newValue;
			LogActivity logActivity = LogActivity.getInstance(ActionType.ADD, childElement.getModelType());

			Logger.createEvent(logActivity, childElement);
			childElement.addPropertyChangeListener(PropertyChangeListenerFactory.getInstance(childElement));
		} else if (propertyName.equals("childRemoved")) {
			IHasChildrenBaseModelElement childElement = (IHasChildrenBaseModelElement) oldValue;
			LogActivity logActivity = LogActivity.getInstance(ActionType.REMOVE, childElement.getModelType());

			Logger.createEvent(logActivity, childElement);
		}
	}

}
