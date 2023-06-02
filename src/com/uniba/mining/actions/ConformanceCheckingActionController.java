package com.uniba.mining.actions;

import com.uniba.mining.dialogs.ConformanceCheckingDialogHandler;
import com.uniba.mining.utils.Application;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class ConformanceCheckingActionController implements VPActionController {
    public static final String ACTION_NAME = "Conformance Checking";
    private static final ViewManager viewManager = Application.getViewManager();

    @Override
    public void performAction(VPAction vpAction) {
        viewManager.showDialog(new ConformanceCheckingDialogHandler());
    }

    @Override
    public void update(VPAction vpAction) {
        // Empty
    }

}
