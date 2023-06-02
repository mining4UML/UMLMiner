package com.uniba.mining.listeners.property;

import com.uniba.mining.logging.LogActivity;
import com.uniba.mining.logging.LogExtractor;
import com.uniba.mining.logging.Logger;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IPackage;

/**
 * 
 * @author pasqualeardimento
 *
 */

class PackagePropertyChangeListener extends AbstractPropertyChangeListener<IPackage> {

    @Override
    public void propertyChange(IPackage packageElement, String propertyName, Object oldValue, Object newValue) {

        if (propertyName.equals("childAdded")) {
            String propertyValue = LogExtractor.extractModelType((IModelElement) newValue);
            Logger.createEvent(LogActivity.ADD_PACKAGE_CHILD, packageElement, propertyName, propertyValue);

        } else if (propertyName.equals("childRemoved")) {
            String propertyValue = LogExtractor.extractModelType((IModelElement) oldValue);
            Logger.createEvent(LogActivity.REMOVE_PACKAGE_CHILD, packageElement, propertyName, propertyValue);

        } else {
            String propertyValue = LogExtractor.extractStringValue(newValue);
            Logger.createEvent(LogActivity.UPDATE_PACKAGE, packageElement, propertyName, propertyValue);
        }
    }

}
