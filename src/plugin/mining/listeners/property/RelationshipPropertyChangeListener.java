package plugin.mining.listeners.property;

import com.vp.plugin.model.IDataType;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IRelationship;

import plugin.mining.logging.LogActivity;
import plugin.mining.logging.Logger;

/**
 * 
 * @author pasqualeardimento
 *
 */

class RelationshipPropertyChangeListener extends AbstractPropertyChangeListener<IRelationship> {

    public void propertyChange(IRelationship relationship, String propertyName, Object oldValue, Object newValue) {
        String propertyValue = newValue instanceof IDataType ? ((IDataType) newValue).getName()
                : newValue instanceof IModelElement ? ((IModelElement) newValue).getId()
                        : newValue instanceof Boolean ? ((Boolean) newValue).toString() : (String) newValue;

        Logger.createEvent(LogActivity.UPDATE_RELATIONSHIP, relationship, propertyName, propertyValue);
    }

}
