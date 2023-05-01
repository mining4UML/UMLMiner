package plugin.mining.listeners.property;

import com.vp.plugin.model.IDataType;
import com.vp.plugin.model.IOperation;

import plugin.mining.logging.LogActivity;
import plugin.mining.logging.Logger;

/**
 * 
 * @author pasqualeardimento
 *
 */

class OperationPropertyChangeListener extends AbstractPropertyChangeListener<IOperation> {

	public void propertyChange(IOperation operation, String propertyName, Object oldValue, Object newValue) {
		String propertyValue = newValue instanceof IDataType ? ((IDataType) newValue).getName()
				: newValue instanceof Boolean ? ((Boolean) newValue).toString() : (String) newValue;

		Logger.createEvent(LogActivity.UPDATE_OPERATION, operation, propertyName, propertyValue);
	}

}
