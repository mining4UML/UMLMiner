package com.plugin.mining.listeners.property;

import com.plugin.mining.logging.LogActivity;
import com.plugin.mining.logging.Logger;
import com.vp.plugin.model.IDataType;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IOperation;

/**
 * 
 * @author pasqualeardimento
 *
 */

class OperationPropertyChangeListener extends AbstractPropertyChangeListener<IOperation> {

	public void propertyChange(IOperation operation, String propertyName, Object oldValue, Object newValue) {
		String propertyValue = extractStringValue(newValue);

		Logger.createEvent(LogActivity.UPDATE_OPERATION, operation, propertyName, propertyValue);
	}

}
