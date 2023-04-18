package plugin.mining.listeners.property;

import java.beans.PropertyChangeEvent;

import com.vp.plugin.model.IOperation;
import plugin.mining.utils.Logger;

/**
 * 
 * @author pasqualeardimento
 *
 */

class OperationPropertyChangeListener extends AbstractPropertyChangeListener<IOperation> {
	private static final Logger logger = new Logger(OperationPropertyChangeListener.class);

	public void propertyChange(IOperation operation, String propertyName, Object oldValue, Object newValue) {
		if (propertyName.equals("name"))
			logger.info("%s \"%s\" %s changed from \"%s\" to \"%s\"",
					operation.getParent().getModelType(),
					operation.getParent().getName(), operation
							.getModelType(),
					oldValue,
					newValue);
	}

}
