package plugin.mining.listeners.property;

import java.beans.PropertyChangeEvent;

import com.vp.plugin.model.IAttribute;
import plugin.mining.utils.Logger;

/**
 * 
 * @author pasqualeardimento
 *
 */

class AttributePropertyChangeListener extends AbstractPropertyChangeListener<IAttribute> {
    private static final Logger logger = new Logger(AttributePropertyChangeListener.class);

    public void propertyChange(IAttribute attribute, String propertyName, Object oldValue, Object newValue) {
        if (propertyName.equals("name"))
            logger.info("%s \"%s\" %s changed from \"%s\" to \"%s\"",
                    attribute.getParent().getModelType(),
                    attribute.getParent().getName(), attribute
                            .getModelType(),
                    oldValue,
                    newValue);
    }

}
