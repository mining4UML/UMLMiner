package plugin.mining.listener;

import java.beans.PropertyChangeEvent;

import plugin.mining.utils.Logger;

public class PropertyChangeListener implements java.beans.PropertyChangeListener {
	private final Logger logger = new Logger(PropertyChangeListener.class);

	public PropertyChangeListener() {
		// Empty
	}

	@Override
	public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
		// Object changeSource = propertyChangeEvent.getSource();
		// String propertyName = propertyChangeEvent.getPropertyName();

		// if (changeSource instanceof IModelElement) {

		// IModelElement model = (IModelElement) changeSource;
		// IModelProperty[] properties = model.toModelPropertyArray();

		// if (propertyChangeEvent.getPropertyName().equals("modelViewAdded"))

		// logger.info("Model Element:" + model.getModelType() +
		// " evt.getPropertyName: " + propertyName + " " + model.PROP_NAME
		// + " : " + model.getName() + " modified.");
		// else if (propertyChangeEvent.getPropertyName().equals("childAdded"))
		// logger.info("Model Element:" + model.getModelType() +
		// " evt.getPropertyName: " + propertyName + " " + model.PROP_NAME
		// + " : " + model.getName() + " modified.");
		// else
		// logger.info("altro: " + propertyChangeEvent.getPropertyName());

		// } else if (changeSource instanceof IAttribute) {

		// IAttribute model = (IAttribute) changeSource;

		// logger.info("Attribute Element:" + model.getModelType() +
		// " evt.getPropertyName: " + propertyName + " " + model.PROP_NAME
		// + " : " + model.getName() + " modified.");
		// }

		// else
		// logger.info("Model Element: " + " IGNOTO" + propertyName);
	}

}