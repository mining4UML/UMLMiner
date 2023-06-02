package com.uniba.mining.listeners.property;

import com.uniba.mining.logging.LogActivity;
import com.uniba.mining.logging.LogExtractor;
import com.uniba.mining.logging.Logger;
import com.vp.plugin.model.IAttribute;

/**
 * 
 * @author pasqualeardimento
 *
 */

class AttributePropertyChangeListener extends AbstractPropertyChangeListener<IAttribute> {

    @Override
    public void propertyChange(IAttribute attribute, String propertyName, Object oldValue, Object newValue) {
        String propertyValue = LogExtractor.extractStringValue(newValue);

        Logger.createEvent(LogActivity.UPDATE_ATTRIBUTE, attribute, propertyName, propertyValue);
    }

}
