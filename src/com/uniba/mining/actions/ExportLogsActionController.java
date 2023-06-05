package com.uniba.mining.actions;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

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
            viewManager.showMessageDialog(viewManager.getRootFrame(), "No logs found.", ACTION_NAME,
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = GUI.createExportFileChooser(ACTION_NAME);
        if (fileChooser.showOpenDialog(viewManager.getRootFrame()) == JFileChooser.APPROVE_OPTION) {
            LogStreamer.exportLogs(fileChooser.getSelectedFile().toPath());
            viewManager.showMessageDialog(viewManager.getRootFrame(), "Logs successfully exported.", ACTION_NAME,
                    JOptionPane.INFORMATION_MESSAGE);
        }

    }

    @Override
    public void update(VPAction vpAction) {
        // Empty
    }

}
