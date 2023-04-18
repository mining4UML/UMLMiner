package plugin.mining.listeners.property;

import java.beans.PropertyChangeEvent;

import com.vp.plugin.model.IModelElement;

/**
 * 
 * @author pasqualeardimento
 *
 */

abstract class AbstractPropertyChangeListener<T extends IModelElement> implements PropertyChangeListener<T> {

	@Override
	@SuppressWarnings("unchecked")
	public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
		Object source = propertyChangeEvent.getSource();
		String propertyName = propertyChangeEvent.getPropertyName();
		Object oldValue = propertyChangeEvent.getOldValue();
		Object newValue = propertyChangeEvent.getNewValue();

		if (!propertyName.equals("pmLastModified"))
			propertyChange((T) source, propertyName, oldValue, newValue);
	}

}