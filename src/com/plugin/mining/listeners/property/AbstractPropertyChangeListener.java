package com.plugin.mining.listeners.property;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.plugin.mining.logging.LogExtractor;
import com.plugin.mining.logging.Logger;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;

/**
 * 
 * @author pasqualeardimento
 *
 */

abstract class AbstractPropertyChangeListener<T extends IModelElement> implements PropertyChangeTypedListener<T> {
	private static final Logger logger = new Logger(AbstractPropertyChangeListener.class);
	private static final Set<String> excludedProperties = new HashSet<>(
			Arrays.asList("lastModified", "pmLastModified", "reorderChild", "modelViewAdded", "modelViewRemoved",
					"masterViewId",
					"willDelete",
					"deleted", "undeleted", "willParentChange", "parentChanged", "fromRelationshipAdded",
					"toRelationshipAdded",
					"fromRelationshipRemoved", "toRelationshipRemoved",
					"fromRelationshipEndAdded",
					"referencedByAdded", "toRelationshipEndAdded", "toRelationshipEndRemoved", "referencedWillRemove",
					"referencedByRemoved",
					"fromRelationshipEndRemoved"));

	@Override
	@SuppressWarnings("unchecked")
	public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
		T modelElement = (T) propertyChangeEvent.getSource();
		String propertyName = propertyChangeEvent.getPropertyName();
		Object oldValue = propertyChangeEvent.getOldValue();
		Object newValue = propertyChangeEvent.getNewValue();

		if (!excludedProperties.contains(propertyName)) {
			IDiagramUIModel diagramUIModel = LogExtractor.getDiagramUIModel(modelElement);
			logger.info("%s property \"%s\" change from \"%s\" to \"%s\"", modelElement.getModelType(), propertyName,
					LogExtractor.extractStringValue(oldValue), LogExtractor.extractStringValue(newValue));

			if (propertyName.equals("childAdded")) {
				IModelElement childElement = (IModelElement) newValue;
				LogExtractor.addDiagramUIModel(childElement, diagramUIModel);
				LogExtractor.addParentModelElement(childElement, modelElement);
			}

			propertyChange(modelElement, propertyName, oldValue, newValue);
		}
	}

}
