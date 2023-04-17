package plugin.mining.listeners;

import java.beans.PropertyChangeEvent;

import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IOperation;

import plugin.mining.utils.Logger;

public class PropertyChangeListenerFactory implements java.beans.PropertyChangeListener {
	private static final Logger logger = new Logger(PropertyChangeListenerFactory.class);
	private PropertyChangeListener propertyChangeListener;

	public PropertyChangeListenerFactory(IModelElement modelElement) {
		if (modelElement instanceof IClass) {
			propertyChangeListener = new ClassPropertyChangeListener((IClass) modelElement);
		} else if (modelElement instanceof IAttribute) {
			propertyChangeListener = new AttributePropertyChangeListener((IAttribute) modelElement);
		} else if (modelElement instanceof IOperation) {
			propertyChangeListener = new OperationPropertyChangeListener((IOperation) modelElement);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
		Object source = propertyChangeEvent.getSource();
		String propertyName = propertyChangeEvent.getPropertyName();
		Object oldValue = propertyChangeEvent.getOldValue();
		Object newValue = propertyChangeEvent.getNewValue();

		if (!propertyName.equals("pmLastModified"))
			logger.info("\"%s\" \"%s\" property changed from \"%s\" to \"%s\"", source,
					propertyName, oldValue, newValue);

		propertyChangeListener.propertyChange(propertyName, oldValue, newValue);
	}

}