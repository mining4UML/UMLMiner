package com.plugin.mining.listeners.property;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IParameter;
import com.vp.plugin.model.IReception;
import com.vp.plugin.model.IRelationship;
import com.vp.plugin.model.IRelationshipEnd;
import com.vp.plugin.model.IUseCase;

/**
 * 
 * @author pasqualeardimento
 *
 */

public class PropertyChangeListenerFactory {
	private static final Map<String, PropertyChangeListener> propertyChangeListeners = new HashMap<>();

	public static PropertyChangeListener getPropertyChangeListener(IModelElement modelElement) {
		return propertyChangeListeners.get(modelElement.getId());
	}

	private PropertyChangeListenerFactory() {
		// Empty
	}

	public static PropertyChangeListener getInstance(IModelElement modelElement) {
		PropertyChangeListener propertyChangeListener = null;
		if (modelElement instanceof IClass)
			propertyChangeListener = new ClassPropertyChangeListener();
		else if (modelElement instanceof IAttribute)
			propertyChangeListener = new AttributePropertyChangeListener();
		else if (modelElement instanceof IOperation)
			propertyChangeListener = new OperationPropertyChangeListener();
		else if (modelElement instanceof IParameter)
			propertyChangeListener = new ParameterPropertyChangeListener();
		else if (modelElement instanceof IReception)
			propertyChangeListener = new ReceptionPropertyChangeListener();
		else if (modelElement instanceof IUseCase)
			propertyChangeListener = new UseCasePropertyChangeListener();
		else if (modelElement instanceof IPackage)
			propertyChangeListener = new PackagePropertyChangeListener();
		else if (modelElement instanceof IRelationship)
			propertyChangeListener = new RelationshipPropertyChangeListener();
		else if (modelElement instanceof IRelationshipEnd)
			propertyChangeListener = new RelationshipEndPropertyChangeListener();

		if (propertyChangeListener == null)
			throw new UnsupportedOperationException("modelElement is not supported");

		propertyChangeListeners.put(modelElement.getId(), propertyChangeListener);
		return propertyChangeListener;
	}

}
