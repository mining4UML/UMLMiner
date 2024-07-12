package com.uniba.mining.actions;

import com.uniba.mining.dialogs.FeedbackRequHandler;
import com.uniba.mining.utils.Application;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class FeedbackRequActionController implements VPActionController {
	
    //public static final String ACTION_NAME = "Configure External";
    private static final ViewManager viewManager = Application.getViewManager();

	@Override
	public void performAction(VPAction arg0) {
		viewManager.showDialog(new FeedbackRequHandler());

	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub

	}

}
