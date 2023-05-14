package com.plugin.mining.listeners.property;

import com.plugin.mining.logging.LogActivity;
import com.plugin.mining.logging.LogExtractor;
import com.plugin.mining.logging.Logger;
import com.vp.plugin.model.IReception;

public class ReceptionPropertyChangeListener extends AbstractPropertyChangeListener<IReception> {
    @Override
    public void propertyChange(IReception reception, String propertyName, Object oldValue, Object newValue) {
        String propertyValue = LogExtractor.extractStringValue(newValue);

        Logger.createEvent(LogActivity.UPDATE_RECEPTION, reception, propertyName, propertyValue);
    }
}
