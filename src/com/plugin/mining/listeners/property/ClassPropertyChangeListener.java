package com.plugin.mining.listeners.property;

import com.plugin.mining.logging.LogActivity;
import com.plugin.mining.logging.LogActivity.ActionType;
import com.plugin.mining.logging.LogExtractor;
import com.plugin.mining.logging.Logger;
import com.plugin.mining.util.Application;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IHasChildrenBaseModelElement;

/**
 * 
 * @author pasqualeardimento
 *
 */

class ClassPropertyChangeListener extends AbstractPropertyChangeListener<IClass> {

	@Override
	public void propertyChange(IClass classElement, String propertyName, Object oldValue, Object newValue) {
		if (propertyName.equals("childAdded")) {
			IHasChildrenBaseModelElement childElement = (IHasChildrenBaseModelElement) newValue;
			LogActivity logActivity = LogActivity.getInstance(ActionType.ADD, childElement.getModelType());

			LogExtractor.addModelElementParent(childElement, classElement);
			Application.runDelayed(() -> {
				Logger.createEvent(logActivity, childElement);
				childElement.addPropertyChangeListener(PropertyChangeListenerFactory.getInstance(childElement));
			});

		} else if (propertyName.equals("childRemoved")) {
			IHasChildrenBaseModelElement childElement = (IHasChildrenBaseModelElement) oldValue;
			LogActivity logActivity = LogActivity.getInstance(ActionType.REMOVE, childElement.getModelType());

			LogExtractor.addModelElementParent(childElement, classElement);
			Logger.createEvent(logActivity, childElement);
		} else {
			String propertyValue = LogExtractor.extractStringValue(newValue);

			Logger.createEvent(LogActivity.UPDATE_CLASS, classElement, propertyName, propertyValue);
		}
	}

}
