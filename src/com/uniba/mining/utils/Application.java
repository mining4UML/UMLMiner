package com.uniba.mining.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.uniba.mining.plugin.Config;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.VPPluginInfo;
import com.vp.plugin.VPProductInfo;
import com.vp.plugin.ViewManager;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IProject;

public class Application {
    private static final long DEFAULT_DELAY = 100;
    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd_HH.mm.ss";
    private static final ApplicationManager manager = ApplicationManager.instance();
    private static final ExecutorService executorService = Executors
            .newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final ScheduledExecutorService scheduledExecutorService = Executors
            .newSingleThreadScheduledExecutor();
    private static final List<Future<?>> tasks = new ArrayList<>();

    private Application() {
        // Empty
    }

    public static VPPluginInfo getPluginInfo(String pluginId) {
        return manager.getPluginInfo(pluginId);
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
        manager.reloadPluginClasses(Config.PLUGIN_ID);
    }

    public static void run(Runnable runnable) {
        runnable.run();
    }

    public static void submit(Runnable runnable) {
        tasks.add(executorService.submit(runnable));
    }

    public static void scheduleSubmit(Runnable runnable, long delay) {
        tasks.add(scheduledExecutorService.schedule(runnable, delay, TimeUnit.MILLISECONDS));
    }

    public static void scheduleSubmit(Runnable runnable) {
        scheduleSubmit(runnable, DEFAULT_DELAY);
    }

    public static void cancelTasks() {
        for (Future<?> task : tasks) {
            task.cancel(true);
        }
        tasks.clear();
    }

    public static String getStringTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT));
    }
}
