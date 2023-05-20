package com.plugin.mining.listeners.property;

import java.beans.PropertyChangeListener;

import com.vp.plugin.model.IModelElement;

/**
 * 
 * @author pasqualeardimento
 *
 */

interface PropertyChangeTypedListener<T extends IModelElement> extends PropertyChangeListener {
    void propertyChange(T source, String propertyName, Object oldValue, Object newValue);
}
