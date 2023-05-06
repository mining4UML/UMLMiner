package com.plugin.mining.util;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.VPProductInfo;
import com.vp.plugin.ViewManager;
import com.vp.plugin.diagram.IDiagramUIModel;
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

    public static ViewManager getViewManager() {
        return manager.getViewManager();
    }

    public static IProject getProject() {
        return manager.getProjectManager().getProject();
    }

    public static IDiagramUIModel getDiagram() {
        return manager.getDiagramManager().getActiveDiagram();
    }

    public static void reloadPlugin() {
        manager.reloadPluginClasses(PLUGIN_ID);
    }
}
