package plugin.mining.listeners.property;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.vp.plugin.model.IModelElement;

import plugin.mining.logging.Logger;

/**
 * 
 * @author pasqualeardimento
 *
 */

abstract class AbstractPropertyChangeListener<T extends IModelElement> implements PropertyChangeListener<T> {
	private static final Logger logger = new Logger(AbstractPropertyChangeListener.class);
	private static final Set<String> propertiesAllowed = new HashSet<>(
			Arrays.asList("name", "initial value", "type", "scope", "visibility", "type modifier"));

	@Override
	@SuppressWarnings("unchecked")
	public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
		Object source = propertyChangeEvent.getSource();
		String propertyName = propertyChangeEvent.getPropertyName();
		Object oldValue = propertyChangeEvent.getOldValue();
		Object newValue = propertyChangeEvent.getNewValue();

		logger.info("%s property \"%s\" change to \"%s\"", ((IModelElement) source).getModelType(), propertyName,
				newValue);
		if (propertiesAllowed.contains(propertyName))
			propertyChange((T) source, propertyName, oldValue, newValue);
	}

}
