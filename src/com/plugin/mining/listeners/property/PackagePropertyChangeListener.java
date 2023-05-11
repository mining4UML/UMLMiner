package com.plugin.mining.listeners.property;

import com.plugin.mining.logging.LogActivity;
import com.plugin.mining.logging.Logger;
import com.vp.plugin.model.IPackage;

/**
 * 
 * @author pasqualeardimento
 *
 */

class PackagePropertyChangeListener extends AbstractPropertyChangeListener<IPackage> {

    @Override
    public void propertyChange(IPackage packageElement, String propertyName, Object oldValue, Object newValue) {
        String propertyValue = extractStringValue(newValue);

        Logger.createEvent(LogActivity.UPDATE_PACKAGE, packageElement, propertyName, propertyValue);
    }

}
