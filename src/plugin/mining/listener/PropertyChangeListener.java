package plugin.mining.listener;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IHasChildrenBaseModelElement;

import plugin.mining.utils.Logger;

public class PropertyChangeListener implements java.beans.PropertyChangeListener {
	private static final Logger logger = new Logger(PropertyChangeListener.class);
	private static final Set<String> propertiesAllowed = new HashSet<>(
			Arrays.asList("name", "childAdded", "childRemoved"));

	public PropertyChangeListener() {
		// Empty
	}

	@Override
	public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
		String propertyName = propertyChangeEvent.getPropertyName();
		if (!propertiesAllowed.contains(propertyName))
			return;

		Object source = propertyChangeEvent.getSource();
		if (source instanceof IClass) {
			IClass classElement = (IClass) source;

			if (propertyName.equals("childAdded")) {
				IHasChildrenBaseModelElement childElement = (IHasChildrenBaseModelElement) propertyChangeEvent
						.getNewValue();
				logger.info("%s \"%s\" \"%s\" %s added", classElement.getModelType(),
						classElement.getName(), childElement.getName(),
						childElement.getModelType());
			} else if (propertyName.equals("childRemoved")) {
				IHasChildrenBaseModelElement childElement = (IHasChildrenBaseModelElement) propertyChangeEvent
						.getOldValue();
				logger.info("%s \"%s\" \"%s\" %s removed", classElement.getModelType(),
						classElement.getName(), childElement.getName(),
						childElement.getModelType());
			}
			return;
		}

		if (source instanceof IHasChildrenBaseModelElement) {
			IHasChildrenBaseModelElement childElement = (IHasChildrenBaseModelElement) source;
			logger.info("%s \"%s\" %s changed from \"%s\" to \"%s\"",
					childElement.getParent().getModelType(),
					childElement.getParent().getName(), childElement
							.getModelType(),
					propertyChangeEvent.getOldValue(),
					propertyChangeEvent.getNewValue());
		}

	}

}