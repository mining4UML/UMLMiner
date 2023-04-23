package plugin.mining.listeners.property;

import java.beans.PropertyChangeEvent;

import com.vp.plugin.model.IAttribute;

import plugin.mining.logging.LogActivity;
import plugin.mining.logging.Logger;

/**
 * 
 * @author pasqualeardimento
 *
 */

class AttributePropertyChangeListener extends AbstractPropertyChangeListener<IAttribute> {

    public void propertyChange(IAttribute attribute, String propertyName, Object oldValue, Object newValue) {
        Logger.createEvent(LogActivity.UPDATE_ATTRIBUTE, attribute, propertyName, (String) newValue);
    }

}
