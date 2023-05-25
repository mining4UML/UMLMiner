package com.plugin.mining.actions;

import com.plugin.mining.logging.Logger;
import com.plugin.mining.utils.Application;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class ReloadPluginActionController implements VPActionController {
    public static final Logger logger = new Logger(ReloadPluginActionController.class);

    @Override
    public void performAction(VPAction vpAction) {
        Application.reloadPlugin();
        logger.info("Plugin reloaded");
    }

    @Override
    public void update(VPAction vpAction) {
        // Empty
    }

}
