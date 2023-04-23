package plugin.mining.listeners.property;

import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IOperation;

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
		throw new UnsupportedOperationException("modelElement is not supported");
	}

}