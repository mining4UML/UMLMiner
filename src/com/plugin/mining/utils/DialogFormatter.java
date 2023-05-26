package com.plugin.mining.utils;

import java.awt.Point;

import com.vp.plugin.view.IDialog;

public class DialogFormatter {
    private DialogFormatter() {
        // Empty
    }

    public static void centerDialog(IDialog dialog) {
        Point point = Application.getCenterPoint();
        point.translate(-dialog.getWidth() / 2, -dialog.getHeight() / 2);
        dialog.setLocation(point);
    }
}
