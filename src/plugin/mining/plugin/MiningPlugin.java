package plugin.mining.plugin;

import com.vp.plugin.VPPlugin;
import com.vp.plugin.VPPluginInfo;
import com.vp.plugin.model.IProject;

import plugin.mining.listeners.ProjectListener;
import plugin.mining.logging.Logger;
import plugin.mining.util.Application;

/**
 * 
 * @author pasqualeardimento
 *
 */
public class MiningPlugin implements VPPlugin {
	private final Logger logger = new Logger(MiningPlugin.class);

	public void loaded(VPPluginInfo info) {
		Logger.createLog();
		logger.info("Plugin loaded");

		IProject project = Application.getProject();
		project.addProjectListener(new ProjectListener(project));
	}

	public void unloaded() {
		Logger.saveLog();
		logger.info("Plugin unloaded");
	}

}
