package com.plugin.mining.listeners.property;

import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IReception;
import com.vp.plugin.model.IRelationship;
import com.vp.plugin.model.IUseCase;

/**
 * 
 * @author pasqualeardimento
 *
 */

public interface PropertyChangeListenerFactory {

	static java.beans.PropertyChangeListener getInstance(IModelElement modelElement) {
		if (modelElement instanceof IClass)
			return new ClassPropertyChangeListener();
		if (modelElement instanceof IAttribute)
			return new AttributePropertyChangeListener();
		if (modelElement instanceof IOperation)
			return new OperationPropertyChangeListener();
		if (modelElement instanceof IReception)
			return new OperationPropertyChangeListener();
		if (modelElement instanceof IUseCase)
			return new UseCasePropertyChangeListener();
		if (modelElement instanceof IRelationship)
			return new RelationshipPropertyChangeListener();
		throw new UnsupportedOperationException("modelElement is not supported");
	}

}
