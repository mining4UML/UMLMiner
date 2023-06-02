package com.uniba.mining.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

import com.uniba.mining.actions.ConformanceCheckingActionController;
import com.uniba.mining.utils.Application;
import com.uniba.mining.utils.GUI;
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
        GUI.prepareDialog(dialog, ConformanceCheckingActionController.ACTION_NAME);
    }

    @Override
    public void shown() {
        // Empty
    }

}
