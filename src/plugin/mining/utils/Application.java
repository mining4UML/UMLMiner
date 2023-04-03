package plugin.mining.utils;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.VPPluginInfo;
import com.vp.plugin.model.IProject;

public class Application {
    public static final String PLUGIN_ID = "mining.plugin";
    private static final ApplicationManager manager = ApplicationManager.instance();

    private Application() {
    }

    public static IProject getProject() {
        return manager.getProjectManager().getProject();
    }

    public static VPPluginInfo[] getPluginInfos() {
        return manager.getPluginInfos();
    }

    public static void reloadPlugin() {
        manager.reloadPluginClasses(PLUGIN_ID);
    }
}
