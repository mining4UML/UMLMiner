package com.uniba.mining.actions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.uniba.mining.plugin.Config;
import com.uniba.mining.plugin.ExternalTool;
import com.uniba.mining.utils.Application;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class ExternalActionController implements VPActionController {
    public static final String ACTION_NAME = "External";

    @Override
    public void performAction(VPAction vpAction) {
        ExternalTool externalTool = ExternalTool.getExternalTool(vpAction.getActionId());
        String externalToolPath = Config.getExternalToolPath(externalTool);
        String[] command = ExternalTool.getExecutionCommand(externalToolPath);
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        Application.run(() -> {
            try {
                File externalToolDirectory = Path.of(externalToolPath).getParent().toFile();
                Path externalToolLogPath = externalToolDirectory.toPath().resolve("ext.log");
                File externalToolLog = Files.exists(externalToolLogPath) ? externalToolLogPath.toFile()
                        : Files.createFile(externalToolLogPath).toFile();
                processBuilder.directory(externalToolDirectory);
                processBuilder.redirectErrorStream(true);
                processBuilder.redirectOutput(externalToolLog);
                Process process = processBuilder.start();
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        });
    }

    @Override
    public void update(VPAction vpAction) {
        // Empty
    }

}
