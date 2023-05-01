package plugin.mining.listeners.property;

import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IDataType;

import plugin.mining.logging.LogActivity;
import plugin.mining.logging.Logger;

/**
 * 
 * @author pasqualeardimento
 *
 */

class AttributePropertyChangeListener extends AbstractPropertyChangeListener<IAttribute> {

    public void propertyChange(IAttribute attribute, String propertyName, Object oldValue, Object newValue) {
        String propertyValue = newValue instanceof IDataType ? ((IDataType) newValue).getName()
                : newValue instanceof Boolean ? ((Boolean) newValue).toString() : (String) newValue;

        Logger.createEvent(LogActivity.UPDATE_ATTRIBUTE, attribute, propertyName, propertyValue);
    }

}
