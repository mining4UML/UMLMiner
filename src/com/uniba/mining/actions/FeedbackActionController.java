package com.uniba.mining.actions;

import com.uniba.mining.dialogs.FeedbackHandler;
import com.uniba.mining.utils.Application;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class FeedbackActionController implements VPActionController {

	@Override
	public void performAction(VPAction arg0) {
		 FeedbackHandler.getInstance().showFeedbackPanel(Application.getProject());
	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub

	}

}