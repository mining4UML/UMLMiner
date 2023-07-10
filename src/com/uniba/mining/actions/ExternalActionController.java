package com.uniba.mining.actions;

import com.uniba.mining.dialogs.RumDialogHandler;
import com.uniba.mining.utils.Application;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class ExternalActionController implements VPActionController {
    public static final String ACTION_NAME = "External";
    private static final ViewManager viewManager = Application.getViewManager();

    @Override
    public void performAction(VPAction vpAction) {
        // ExternalTool externalTool =
        // ExternalTool.getExternalTool(vpAction.getActionId());
        // String externalToolPath = Config.getExternalToolPath(externalTool);
        // String[] command = ExternalTool.getExecutionCommand(externalToolPath);
        // ProcessBuilder processBuilder = new ProcessBuilder(command);

        // Application.run(() -> {
        // try {
        // File externalToolDirectory = Path.of(externalToolPath).getParent().toFile();
        // Path externalToolLogPath = externalToolDirectory.toPath().resolve("ext.log");
        // File externalToolLog = Files.exists(externalToolLogPath) ?
        // externalToolLogPath.toFile()
        // : Files.createFile(externalToolLogPath).toFile();
        // processBuilder.directory(externalToolDirectory);
        // processBuilder.redirectErrorStream(true);
        // processBuilder.redirectOutput(externalToolLog);
        // Process process = processBuilder.start();
        // process.waitFor();
        // } catch (IOException | InterruptedException e) {
        // e.printStackTrace();
        // Thread.currentThread().interrupt();
        // }
        // });

        viewManager.showDialog(new RumDialogHandler());
    }

    @Override
    public void update(VPAction vpAction) {
        // Empty
    }

}
