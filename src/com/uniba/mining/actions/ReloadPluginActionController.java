package com.uniba.mining.actions;

import com.uniba.mining.dialogs.FeedbackHandler;
import com.uniba.mining.logging.Logger;
import com.uniba.mining.utils.Application;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class ReloadPluginActionController implements VPActionController {
    public static final Logger logger = new Logger(ReloadPluginActionController.class);
    //private final FeedbackHandler feed = FeedbackHandler.getInstance();
    
    @Override
    public void performAction(VPAction vpAction) {
        Application.reloadPlugin();
        logger.info("Plugin reloaded");
        //feed.showFeedbackPanel();
    }

    @Override
    public void update(VPAction vpAction) {
        // Empty
    }

}
