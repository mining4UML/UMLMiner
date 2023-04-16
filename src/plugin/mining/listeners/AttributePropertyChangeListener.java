package plugin.mining.listeners;

import com.vp.plugin.model.IAttribute;

import plugin.mining.utils.Logger;

public class AttributePropertyChangeListener implements PropertyChangeListener {
    private static final Logger logger = new Logger(AttributePropertyChangeListener.class);
    private IAttribute attribute;

    public AttributePropertyChangeListener(IAttribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public void propertyChange(String propertyName, Object oldValue, Object newValue) {
        if (propertyName.equals("name"))
            logger.info("%s \"%s\" %s changed from \"%s\" to \"%s\"",
                    attribute.getParent().getModelType(),
                    attribute.getParent().getName(), attribute
                            .getModelType(),
                    oldValue,
                    newValue);
    }
}
