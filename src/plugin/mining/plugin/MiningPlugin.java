package plugin.mining.plugin;

import com.vp.plugin.VPPlugin;
import com.vp.plugin.VPPluginInfo;
import com.vp.plugin.model.IProject;

import plugin.mining.listener.ProjectListener;
import plugin.mining.utils.Application;
import plugin.mining.utils.Logger;

/**
 * 
 * @author pasqualeardimento
 *
 */
public class MiningPlugin implements VPPlugin {
	private final Logger logger = new Logger(MiningPlugin.class);
	private static final ProjectListener projectListener = new ProjectListener();

	public void loaded(VPPluginInfo info) {
		Logger.createLogFile();
		logger.info("plugin loaded");
		IProject project = Application.getProject();
		project.addProjectListener(projectListener);
	}

	public void unloaded() {
		logger.info("plugin unloaded");
		IProject project = Application.getProject();
		project.removeProjectListener(projectListener);
	}

}