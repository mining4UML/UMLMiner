package com.plugin.mining.listeners.property;

import com.plugin.mining.logging.LogActivity;
import com.plugin.mining.logging.Logger;
import com.vp.plugin.model.IUseCase;

/**
 * 
 * @author pasqualeardimento
 *
 */

class UseCasePropertyChangeListener extends AbstractPropertyChangeListener<IUseCase> {

    @Override
    public void propertyChange(IUseCase useCase, String propertyName, Object oldValue, Object newValue) {
        String propertyValue = extractStringValue(newValue);

        Logger.createEvent(LogActivity.UPDATE_USE_CASE, useCase, propertyName, propertyValue);
    }

}
