package com.plugin.mining.listeners.property;

import com.plugin.mining.logging.LogActivity;
import com.plugin.mining.logging.LogExtractor;
import com.plugin.mining.logging.Logger;
import com.plugin.mining.utils.Application;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IParameter;

/**
 * 
 * @author pasqualeardimento
 *
 */

class OperationPropertyChangeListener extends AbstractPropertyChangeListener<IOperation> {

	public void propertyChange(IOperation operation, String propertyName, Object oldValue, Object newValue) {
		if (propertyName.equals("childAdded")) {
			IParameter parameter = (IParameter) newValue;

			Application.runDelayed(() -> {
				Logger.createEvent(LogActivity.ADD_PARAMETER, operation);
				parameter.addPropertyChangeListener(PropertyChangeListenerFactory.getInstance(parameter));
			});

		} else if (propertyName.equals("childRemoved")) {
			IParameter parameter = (IParameter) oldValue;

			Logger.createEvent(LogActivity.REMOVE_PARAMETER, operation);
		} else {
			String propertyValue = LogExtractor.extractStringValue(newValue);

			Logger.createEvent(LogActivity.UPDATE_OPERATION, operation, propertyName, propertyValue);
		}
	}

}
