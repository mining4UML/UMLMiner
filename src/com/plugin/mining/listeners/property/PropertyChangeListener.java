package com.plugin.mining.listeners.property;

import com.vp.plugin.model.IModelElement;

/**
 * 
 * @author pasqualeardimento
 *
 */

interface PropertyChangeListener<T extends IModelElement> extends java.beans.PropertyChangeListener {
    void propertyChange(T source, String propertyName, Object oldValue, Object newValue);
}
