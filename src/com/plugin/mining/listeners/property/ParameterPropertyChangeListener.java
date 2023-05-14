package com.plugin.mining.listeners.property;

import com.plugin.mining.logging.LogActivity;
import com.plugin.mining.logging.LogExtractor;
import com.plugin.mining.logging.Logger;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IParameter;

/**
 * 
 * @author pasqualeardimento
 *
 */

class ParameterPropertyChangeListener extends AbstractPropertyChangeListener<IParameter> {

	public void propertyChange(IParameter parameter, String propertyName, Object oldValue, Object newValue) {
		String propertyValue = LogExtractor.extractStringValue(newValue);
		IOperation operation = parameter.getOperation();

		Logger.createEvent(LogActivity.UPDATE_PARAMETER, operation, propertyName, propertyValue);
	}

}
