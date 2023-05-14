package com.plugin.mining.listeners.property;

import com.plugin.mining.logging.LogActivity;
import com.plugin.mining.logging.LogExtractor;
import com.plugin.mining.logging.Logger;
import com.vp.plugin.model.IEndRelationship;
import com.vp.plugin.model.IRelationshipEnd;

/**
 * 
 * @author pasqualeardimento
 *
 */

class RelationshipEndPropertyChangeListener extends AbstractPropertyChangeListener<IRelationshipEnd> {

        @Override
        public void propertyChange(IRelationshipEnd relationshipEnd, String propertyName, Object oldValue,
                        Object newValue) {
                String propertyValue = LogExtractor.extractStringValue(newValue);
                IEndRelationship relationship = relationshipEnd.getEndRelationship();
                String relationshipEndDirection = relationship.getFromEnd().equals(relationshipEnd)
                                ? "from"
                                : "to";

                Logger.createEvent(LogActivity.UPDATE_RELATIONSHIP,
                                relationship,
                                String.join(".", relationshipEndDirection, propertyName), propertyValue);
        }

}
