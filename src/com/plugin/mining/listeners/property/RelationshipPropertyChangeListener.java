package com.plugin.mining.listeners.property;

import com.plugin.mining.logging.LogActivity;
import com.plugin.mining.logging.LogExtractor;
import com.plugin.mining.logging.Logger;
import com.vp.plugin.model.IRelationship;

/**
 * 
 * @author pasqualeardimento
 *
 */

class RelationshipPropertyChangeListener extends AbstractPropertyChangeListener<IRelationship> {

    @Override
    public void propertyChange(IRelationship relationship, String propertyName, Object oldValue, Object newValue) {
        String propertyValue = LogExtractor.extractStringValue(newValue);

        Logger.createEvent(LogActivity.UPDATE_RELATIONSHIP, relationship, propertyName, propertyValue);
    }

}
