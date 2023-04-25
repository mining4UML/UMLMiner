package plugin.mining.listeners.property;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.vp.plugin.model.IModelElement;

/**
 * 
 * @author pasqualeardimento
 *
 */

abstract class AbstractPropertyChangeListener<T extends IModelElement> implements PropertyChangeListener<T> {
	private static final Set<String> propertiesAllowed = new HashSet<>(
			Arrays.asList("name", "initial value", "type", "scope", "visibility", "type modifier"));

	@Override
	@SuppressWarnings("unchecked")
	public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
		Object source = propertyChangeEvent.getSource();
		String propertyName = propertyChangeEvent.getPropertyName();
		Object oldValue = propertyChangeEvent.getOldValue();
		Object newValue = propertyChangeEvent.getNewValue();

		if (propertiesAllowed.contains(propertyName))
			propertyChange((T) source, propertyName, oldValue, newValue);
	}

}
