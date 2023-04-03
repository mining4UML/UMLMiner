package plugin.mining.actions;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

import plugin.mining.utils.Application;
import plugin.mining.utils.Logger;

public class ReloadPluginActionController implements VPActionController {
    private final Logger logger = new Logger(ReloadPluginActionController.class);

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
