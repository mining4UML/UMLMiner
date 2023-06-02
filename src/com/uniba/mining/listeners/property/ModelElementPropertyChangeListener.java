package com.uniba.mining.listeners.property;

import com.uniba.mining.logging.LogActivity;
import com.uniba.mining.logging.LogExtractor;
import com.uniba.mining.logging.Logger;
import com.vp.plugin.model.IModelElement;

public class ModelElementPropertyChangeListener extends AbstractPropertyChangeListener<IModelElement> {
    @Override
    public void propertyChange(IModelElement modelElement, String propertyName, Object oldValue, Object newValue) {
        String propertyValue = LogExtractor.extractStringValue(newValue);

        Logger.createEvent(LogActivity.UPDATE_MODEL, modelElement, propertyName, propertyValue);
    }
}
