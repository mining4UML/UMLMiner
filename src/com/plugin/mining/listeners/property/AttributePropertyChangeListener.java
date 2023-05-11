package com.plugin.mining.listeners.property;

import com.plugin.mining.logging.LogActivity;
import com.plugin.mining.logging.Logger;
import com.vp.plugin.model.IAttribute;

/**
 * 
 * @author pasqualeardimento
 *
 */

class AttributePropertyChangeListener extends AbstractPropertyChangeListener<IAttribute> {

    @Override
    public void propertyChange(IAttribute attribute, String propertyName, Object oldValue, Object newValue) {
        String propertyValue = extractStringValue(newValue);

        Logger.createEvent(LogActivity.UPDATE_ATTRIBUTE, attribute, propertyName, propertyValue);
    }

}
