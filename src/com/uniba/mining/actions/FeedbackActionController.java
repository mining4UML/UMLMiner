package com.uniba.mining.actions;

import com.uniba.mining.dialogs.FeedbackHandler;
import com.uniba.mining.utils.Application;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IDiagramUIModel;

public class FeedbackActionController implements VPActionController {

	@Override
	public void performAction(VPAction arg0) {
		 FeedbackHandler.getInstance().showFeedbackPanel(Application.getDiagram());
	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub

	}

}