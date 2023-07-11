package com.uniba.mining.actions;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessHandle.Info;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JFrame;

import com.uniba.mining.plugin.Config;
import com.uniba.mining.plugin.ExternalTool;
import com.uniba.mining.utils.Application;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class ExternalActionController implements VPActionController {
    public static final String ACTION_NAME = "External";
    private static final ViewManager viewManager = Application.getViewManager();

    @Override
    public void performAction(VPAction vpAction) {
        JFrame frame = (JFrame) viewManager.getRootFrame();
        ExternalTool externalTool = ExternalTool.getExternalTool(vpAction.getActionId());
        String externalToolPath = Config.getExternalToolPath(externalTool);
        String[] command = ExternalTool.getExecutionCommand(externalToolPath);
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        frame.setVisible(false);

        try {
            File externalToolDirectory = Path.of(externalToolPath).getParent().toFile();
            Path externalToolLogPath = externalToolDirectory.toPath().resolve("ext.log");
            File externalToolLog = Files.exists(externalToolLogPath) ? externalToolLogPath.toFile()
                    : Files.createFile(externalToolLogPath).toFile();
            String bundledJavaHome = ExternalTool.getBundledJavaHome(externalToolDirectory.toString());
            String javaHome = Files.isDirectory(Path.of(bundledJavaHome)) ? bundledJavaHome
                    : System.getenv("JAVA_HOME");
            String javaPath = javaHome + File.separator + "bin";

            processBuilder.directory(externalToolDirectory);
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectOutput(externalToolLog);
            processBuilder.environment().put("Path", javaPath);
            processBuilder.environment().put("PATH", javaPath);

            Process process = processBuilder.start();
            Info processInfo = process.toHandle().info();

            System.out.println("Command: " + processInfo.command());
            System.out.println("Arguments: " + processInfo.arguments());
            System.out.println("Command Line: " + processInfo.commandLine());

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        frame.setVisible(true);
    }

    @Override
    public void update(VPAction vpAction) {
        // Empty
    }

}
