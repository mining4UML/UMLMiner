package com.plugin.mining.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

import com.plugin.mining.actions.ConformanceCheckingActionController;
import com.plugin.mining.utils.Application;
import com.plugin.mining.utils.GUI;
import com.vp.plugin.ViewManager;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

public class ConformanceCheckingDialogHandler implements IDialogHandler {
    private static final ViewManager viewManager = Application.getViewManager();

    @Override
    public boolean canClosed() {
        return true;
    }

    @Override
    public Component getComponent() {
        JPanel rootPanel = new JPanel(new BorderLayout(2, 0));
        return rootPanel;
    }

    @Override
    public void prepare(IDialog dialog) {
        dialog.setModal(true);
        dialog.setResizable(false);
        dialog.setTitle(ConformanceCheckingActionController.ACTION_NAME);
        GUI.centerDialog(dialog);
    }

    @Override
    public void shown() {
        // Empty
    }

}
