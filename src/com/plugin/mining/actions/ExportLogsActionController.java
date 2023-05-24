package com.plugin.mining.actions;

import java.awt.Component;
import java.awt.Container;
import java.awt.TextField;
import java.awt.TrayIcon.MessageType;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.plugin.mining.logging.LogStream;
import com.plugin.mining.util.Application;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

public class ExportLogsActionController implements VPActionController {
    private static final String ACTION_NAME = "Export Logs";
    private static final ViewManager viewManager = Application.getViewManager();

    public void disableTextFields(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JTextField) {
                ((JTextField) component).setEditable(false);
            } else if (component instanceof Container) {
                disableTextFields((Container) component);
            }
        }
    }

    private JFileChooser createFileChooser() {
        JFileChooser fileChooser = viewManager.createJFileChooser();
        fileChooser.setLocale(Locale.ENGLISH);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setName(ACTION_NAME);
        fileChooser.setDialogTitle(ACTION_NAME);
        fileChooser.setToolTipText(ACTION_NAME);

        disableTextFields(fileChooser);

        return fileChooser;
    }

    @Override
    public void performAction(VPAction vpAction) {

        if (LogStream.countLogs() == 0) {
            viewManager.showMessageDialog(viewManager.getRootFrame(), "No logs found.", ACTION_NAME,
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = createFileChooser();
        if (fileChooser.showSaveDialog(viewManager.getRootFrame()) == JFileChooser.APPROVE_OPTION) {
            LogStream.exportLogs(fileChooser.getSelectedFile().toPath());
            viewManager.showMessageDialog(viewManager.getRootFrame(), "Logs successfully exported.", ACTION_NAME,
                    JOptionPane.INFORMATION_MESSAGE);
        }

    }

    @Override
    public void update(VPAction vpAction) {
        // Empty
    }

}
