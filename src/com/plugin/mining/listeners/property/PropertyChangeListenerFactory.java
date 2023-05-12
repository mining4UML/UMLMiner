package com.plugin.mining.listeners.property;

import java.util.Arrays;

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

	private PropertyChangeListenerFactory() {
		// Empty
	}

	public static String extractStringValue(Object value) {
		if (value instanceof IModelElement)
			return ((IModelElement) value).getId();
		if (value instanceof IModelElement[])
			return Arrays.toString(Arrays.stream(((IModelElement[]) value)).map(IModelElement::getName)
					.toArray(String[]::new));
		if (value instanceof Number)
			return ((Number) value).toString();
		if (value instanceof Boolean)
			return ((Boolean) value).toString();
		if (value instanceof Object[])
			return Arrays.toString((Object[]) value);
		return (String) value;
	}

	public static java.beans.PropertyChangeListener getInstance(IModelElement modelElement) {
		if (modelElement instanceof IClass)
			return new ClassPropertyChangeListener();
		if (modelElement instanceof IAttribute)
			return new AttributePropertyChangeListener();
		if (modelElement instanceof IOperation)
			return new OperationPropertyChangeListener();
		if (modelElement instanceof IParameter)
			return new ParameterPropertyChangeListener();
		if (modelElement instanceof IReception)
			return new ReceptionPropertyChangeListener();
		if (modelElement instanceof IUseCase)
			return new UseCasePropertyChangeListener();
		if (modelElement instanceof IPackage)
			return new PackagePropertyChangeListener();
		if (modelElement instanceof IRelationship)
			return new RelationshipPropertyChangeListener();
		if (modelElement instanceof IRelationshipEnd)
			return new RelationshipEndPropertyChangeListener();
		throw new UnsupportedOperationException("modelElement is not supported");
	}

}
