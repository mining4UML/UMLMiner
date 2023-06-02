package com.plugin.mining.actions;

import com.plugin.mining.dialogs.AboutDialogHandler;
import com.plugin.mining.utils.Application;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class AboutActionController implements VPActionController {
    public static final String ACTION_NAME = "About";
    private static final ViewManager viewManager = Application.getViewManager();

    @Override
    public void performAction(VPAction vpAction) {
        viewManager.showDialog(new AboutDialogHandler());
    }

    @Override
    public void update(VPAction vpAction) {
        // Empty
    }

}
