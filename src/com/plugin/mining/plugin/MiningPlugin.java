package com.plugin.mining.plugin;

import com.plugin.mining.listeners.ProjectListener;
import com.plugin.mining.logging.Logger;
import com.plugin.mining.util.Application;
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

	public void loaded(VPPluginInfo info) {
		logger.info("Plugin loaded");

		IProject project = Application.getProject();
		project.addProjectListener(new ProjectListener(project));
	}

	public void unloaded() {
		logger.info("Plugin unloaded");
	}

}
