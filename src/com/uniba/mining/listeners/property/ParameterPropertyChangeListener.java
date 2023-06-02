package com.uniba.mining.listeners.property;

import com.uniba.mining.logging.LogActivity;
import com.uniba.mining.logging.LogExtractor;
import com.uniba.mining.logging.Logger;
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
		IOperation operation = (IOperation) parameter.getParent();

		Logger.createEvent(LogActivity.UPDATE_PARAMETER, operation, propertyName, propertyValue);
	}

}
