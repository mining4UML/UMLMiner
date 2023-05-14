package com.plugin.mining.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.VPProductInfo;
import com.vp.plugin.ViewManager;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IProject;

public class Application {
    public static final String PLUGIN_ID = "mining.plugin";
    private static final ApplicationManager manager = ApplicationManager.instance();
    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private static final long DEFAULT_DELAY = 100;

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

    public static void runDelayed(Runnable runnable, long delay) {
        executor.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    public static void runDelayed(Runnable runnable) {
        executor.schedule(runnable, DEFAULT_DELAY, TimeUnit.MILLISECONDS);
    }
}
