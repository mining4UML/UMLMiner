package plugin.mining.listeners.property;

import com.vp.plugin.model.IUseCase;

import plugin.mining.logging.LogActivity;
import plugin.mining.logging.Logger;

/**
 * 
 * @author pasqualeardimento
 *
 */

class UseCasePropertyChangeListener extends AbstractPropertyChangeListener<IUseCase> {

    public void propertyChange(IUseCase useCase, String propertyName, Object oldValue, Object newValue) {
        Logger.createEvent(LogActivity.UPDATE_USE_CASE, useCase, propertyName, (String) newValue);
    }

}
