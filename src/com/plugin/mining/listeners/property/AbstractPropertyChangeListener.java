package com.plugin.mining.listeners.property;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.plugin.mining.logging.Logger;
import com.vp.plugin.model.IModelElement;

/**
 * 
 * @author pasqualeardimento
 *
 */

abstract class AbstractPropertyChangeListener<T extends IModelElement> implements PropertyChangeListener<T> {
	private static final Logger logger = new Logger(AbstractPropertyChangeListener.class);
	private static final Set<String> excludedProperties = new HashSet<>(
			Arrays.asList("lastModified", "pmLastModified", "reorderChild", "modelViewRemoved", "masterViewId",
					"willDelete",
					"deleted", "willParentChange", "parentChanged", "fromRelationshipAdded",
					"toRelationshipAdded",
					"fromRelationshipRemoved", "toRelationshipRemoved",
					"fromRelationshipEndAdded",
					"referencedByAdded", "toRelationshipEndAdded", "toRelationshipEndRemoved", "referencedWillRemove",
					"referencedByRemoved",
					"fromRelationshipEndRemoved"));

	protected static String extractStringValue(Object value) {
		if (value instanceof IModelElement)
			return ((IModelElement) value).getId();
		if (value instanceof IModelElement[])
			return Arrays.toString(Arrays.stream(((IModelElement[]) value)).map(IModelElement::getName)
					.toArray(String[]::new));
		if (value instanceof Number)
			return ((Number) value).toString();
		if (value instanceof Boolean)
			return ((Boolean) value).toString();
		return (String) value;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
		Object source = propertyChangeEvent.getSource();
		String propertyName = propertyChangeEvent.getPropertyName();
		Object oldValue = propertyChangeEvent.getOldValue();
		Object newValue = propertyChangeEvent.getNewValue();

		if (!excludedProperties.contains(propertyName)) {
			logger.info("%s property \"%s\" change to \"%s\"", ((T) source).getModelType(), propertyName,
					newValue);
			propertyChange((T) source, propertyName, oldValue, newValue);
		}
	}

}
