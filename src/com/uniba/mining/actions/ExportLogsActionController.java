package com.uniba.mining.actions;

import javax.swing.JFileChooser;

import com.uniba.mining.logging.LogStreamer;
import com.uniba.mining.utils.Application;
import com.uniba.mining.utils.GUI;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class ExportLogsActionController implements VPActionController {
    public static final String ACTION_NAME = "Export Logs";
    private static final ViewManager viewManager = Application.getViewManager();

    @Override
    public void performAction(VPAction vpAction) {

        if (LogStreamer.countLogs() == 0) {
            GUI.showWarningMessageDialog(viewManager.getRootFrame(), ACTION_NAME, "No logs found.");
            return;
        }

        JFileChooser fileChooser = GUI.createExportFileChooser(ACTION_NAME);
        if (fileChooser.showOpenDialog(viewManager.getRootFrame()) == JFileChooser.APPROVE_OPTION) {
            LogStreamer.exportLogs(fileChooser.getSelectedFile().toPath());
            GUI.showInformationMessageDialog(viewManager.getRootFrame(), ACTION_NAME, "Logs successfully exported.");
        }

    }

    @Override
    public void update(VPAction vpAction) {
        // Empty
    }

}
