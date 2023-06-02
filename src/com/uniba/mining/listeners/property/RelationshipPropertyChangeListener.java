package com.uniba.mining.listeners.property;

import com.uniba.mining.logging.LogActivity;
import com.uniba.mining.logging.LogExtractor;
import com.uniba.mining.logging.Logger;
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
