package com.plugin.mining.actions;

import javax.swing.JFileChooser;

import com.plugin.mining.logging.LogStream;
import com.plugin.mining.logging.Logger;
import com.plugin.mining.util.Application;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class ExportLogsActionController implements VPActionController {
    private static final String ACTION_NAME = "Export Logs";
    private final Logger logger = new Logger(ExportLogsActionController.class);
    private final ViewManager viewManager = Application.getViewManager();

    private JFileChooser createFileChooser() {
        JFileChooser fileChooser = viewManager.createJFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setName(ACTION_NAME);
        fileChooser.setDialogTitle(ACTION_NAME);
        fileChooser.setToolTipText(ACTION_NAME);
        return fileChooser;
    }

    @Override
    public void performAction(VPAction vpAction) {
        JFileChooser fileChooser = createFileChooser();

        if (fileChooser.showSaveDialog(viewManager.getRootFrame()) == JFileChooser.APPROVE_OPTION) {
            LogStream.exportLogs(fileChooser.getSelectedFile().toPath());
            viewManager.showMessageDialog(viewManager.getRootFrame(), "Logs successfully exported");
        }

    }

    @Override
    public void update(VPAction vpAction) {
        // Empty
    }

}
