package plugin.mining.listeners.property;

import com.vp.plugin.model.IModelElement;

/**
 * 
 * @author pasqualeardimento
 *
 */

abstract interface PropertyChangeListener<T extends IModelElement> extends java.beans.PropertyChangeListener {
    abstract void propertyChange(T source, String propertyName, Object oldValue, Object newValue);
}
