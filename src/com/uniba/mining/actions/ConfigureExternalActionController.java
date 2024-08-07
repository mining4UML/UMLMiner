package com.uniba.mining.actions;

import com.uniba.mining.dialogs.ConfigureExternalDialogHandler;
import com.uniba.mining.utils.Application;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class ConfigureExternalActionController implements VPActionController {
    public static final String ACTION_NAME = "Configure External";
    private static final ViewManager viewManager = Application.getViewManager();

    @Override
    public void performAction(VPAction vpAction) {
        viewManager.showDialog(new ConfigureExternalDialogHandler());
    }

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub		
	}


}
