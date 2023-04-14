package plugin.mining.listeners;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IHasChildrenBaseModelElement;

import plugin.mining.utils.Logger;

public class PropertyChangeListener implements java.beans.PropertyChangeListener {
	private static final Logger logger = new Logger(PropertyChangeListener.class);
	private static final PropertyChangeListener propertyChangeListener = new PropertyChangeListener();
	private static final Set<String> propertiesAllowed = new HashSet<>(
			Arrays.asList("name", "childAdded", "childRemoved"));

	public PropertyChangeListener() {
		// Empty
	}

	@Override
	public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
		Object source = propertyChangeEvent.getSource();
		String propertyName = propertyChangeEvent.getPropertyName();
		Object oldValue = propertyChangeEvent.getOldValue();
		Object newValue = propertyChangeEvent.getNewValue();

		logger.info("\"%s\" \"%s\" property changed from \"%s\" to \"%s\"", source,
				propertyName, oldValue, newValue);

		// if (!propertiesAllowed.contains(propertyName))
		// return;

		if (source instanceof IClass) {
			IClass classElement = (IClass) source;

			if (propertyName.equals("childAdded")) {
				IHasChildrenBaseModelElement childElement = (IHasChildrenBaseModelElement) newValue;
				childElement.addPropertyChangeListener(propertyChangeListener);
				logger.info("%s \"%s\" \"%s\" %s added", classElement.getModelType(),
						classElement.getName(), childElement.getName(),
						childElement.getModelType());
			} else if (propertyName.equals("childRemoved")) {
				IHasChildrenBaseModelElement childElement = (IHasChildrenBaseModelElement) oldValue;
				logger.info("%s \"%s\" \"%s\" %s removed", classElement.getModelType(),
						classElement.getName(), childElement.getName(),
						childElement.getModelType());
			}
			return;
		}

		if (source instanceof IHasChildrenBaseModelElement) {
			IHasChildrenBaseModelElement childElement = (IHasChildrenBaseModelElement) source;

			if (propertyName.equals("name"))
				logger.info("%s \"%s\" %s changed from \"%s\" to \"%s\"",
						childElement.getParent().getModelType(),
						childElement.getParent().getName(), childElement
								.getModelType(),
						oldValue,
						newValue);
		}

	}

}