package com.uniba.mining.plugin;

import com.uniba.mining.dialogs.FeedbackHandler;
import com.uniba.mining.listeners.ProjectListener;
import com.uniba.mining.logging.Logger;
import com.uniba.mining.utils.Application;
import com.vp.plugin.VPPlugin;
import com.vp.plugin.VPPluginInfo;
import com.vp.plugin.model.IProject;

/**
 * 
 * @author pasqualeardimento
 *
 */
public class MiningPlugin implements VPPlugin {
	private final Logger logger = new Logger(MiningPlugin.class);
	private final FeedbackHandler feed = FeedbackHandler.getInstance();

	public void loaded(VPPluginInfo pluginInfo) {
		logger.info("Plugin loaded");

		IProject project = Application.getProject();
		project.addProjectListener(new ProjectListener(project));
		feed.showFeedbackPanel();
	}

	public void unloaded() {
		logger.info("Plugin unloaded");

		Config.storeExtProperties();
	}

}
