package com.uniba.mining.actions;

import com.uniba.mining.dialogs.FeedbackHandler;
import com.uniba.mining.plugin.Config;
import com.uniba.mining.utils.Application;
import com.uniba.mining.utils.GUI;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

import java.util.logging.Logger;

public class FeedbackActionController implements VPActionController {
    
    private static final Logger logger = Logger.getLogger(FeedbackActionController.class.getName());
    
    public FeedbackActionController() {
        logger.info("FeedbackActionController instantiated");
    }

    @Override
    public void performAction(VPAction arg0) {
        if (Application.getDiagram() != null) {
            FeedbackHandler.getInstance().showFeedbackPanel(Application.getDiagram());
        } else {
        	GUI.showInformationMessageDialog(Application.getViewManager().getRootFrame(), 
        			Config.PLUGIN_NAME, Config.FEEDBACK_NODIAGRAM_OPENED);
            logger.warning("No diagram found when performAction was called");
        }
    }

    @Override
    public void update(VPAction arg0) {
        logger.info("update called in FeedbackActionController");
        Application.getViewManager().showMessage("Hello World");
        if (Application.getDiagram() != null) {
            arg0.setEnabled(true);
            logger.info("Action enabled");
        } else {
            arg0.setEnabled(false);
            logger.info("Action disabled");
        }
    }
}
