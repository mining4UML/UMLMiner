package com.plugin.mining.actions;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JPanel;

import com.plugin.mining.utils.Application;
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
        JPanel panel = new JPanel();
        panel.setToolTipText(ConformanceCheckingActionController.ACTION_NAME);
        return panel;
    }

    @Override
    public void prepare(IDialog dialog) {
        dialog.setModal(true);
        dialog.setResizable(false);
        dialog.setTitle(ConformanceCheckingActionController.ACTION_NAME);

        Point point = Application.getCenterPoint();
        point.translate(-(dialog.getWidth() / 2), -(dialog.getHeight() / 2));
        dialog.setLocation(point);
    }

    @Override
    public void shown() {
        // Empty
    }

}
