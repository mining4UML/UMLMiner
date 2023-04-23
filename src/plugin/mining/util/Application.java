package plugin.mining.util;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.VPProductInfo;
import com.vp.plugin.ViewManager;
import com.vp.plugin.model.IProject;

public class Application {
    public static final String PLUGIN_ID = "mining.plugin";
    private static final ApplicationManager manager = ApplicationManager.instance();

    private Application() {
        // Empty
    }

    public static VPProductInfo getProductInfo() {
        return manager.getProductInfo();
    }

    public static ProjectManager getProjectManager() {
        return manager.getProjectManager();
    }

    public static ViewManager getViewManager() {
        return manager.getViewManager();
    }

    public static IProject getProject() {
        return getProjectManager().getProject();
    }

    public static void reloadPlugin() {
        manager.reloadPluginClasses(PLUGIN_ID);
    }
}
