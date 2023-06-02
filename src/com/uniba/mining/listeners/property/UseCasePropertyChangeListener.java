package com.uniba.mining.listeners.property;

import com.uniba.mining.logging.LogActivity;
import com.uniba.mining.logging.LogExtractor;
import com.uniba.mining.logging.Logger;
import com.vp.plugin.model.IUseCase;

/**
 * 
 * @author pasqualeardimento
 *
 */

class UseCasePropertyChangeListener extends AbstractPropertyChangeListener<IUseCase> {

    @Override
    public void propertyChange(IUseCase useCase, String propertyName, Object oldValue, Object newValue) {
        String propertyValue = LogExtractor.extractStringValue(newValue);

        Logger.createEvent(LogActivity.UPDATE_USE_CASE, useCase, propertyName, propertyValue);
    }

}
