package com.plugin.mining.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.border.Border;

import com.vp.plugin.view.IDialog;

public class GUI {
    private static final int DEFAULT_PADDING = 4;
    private static final int DEFAULT_BORDER_SIZE = 1;
    private static final Dimension defaultPaddingDimension = new Dimension(DEFAULT_PADDING, DEFAULT_PADDING);
    private static final Border defaultPaddingBorder = BorderFactory.createEmptyBorder(DEFAULT_PADDING, DEFAULT_PADDING,
            DEFAULT_PADDING, DEFAULT_PADDING);
    private static final Border defaultBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, DEFAULT_BORDER_SIZE, true), defaultPaddingBorder);

    private GUI() {
    }

    public static JComponent getDefaultPaddingComponent() {
        return (JComponent) Box.createRigidArea(defaultPaddingDimension);
    }

    public static Border getDefaultBorder() {
        return defaultBorder;
    }

    public static Border getDefaultTitledBorder(String title) {
        return BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(title), defaultPaddingBorder);
    }

    public static void addAll(Container container, boolean withPadding, Component... components) {
        for (Component component : components) {
            container.add(component);
            if (withPadding) {
                JComponent defaultPaddingComponent = getDefaultPaddingComponent();
                defaultPaddingComponent.setAlignmentX(component.getAlignmentX());
                defaultPaddingComponent.setAlignmentY(component.getAlignmentY());
                container.add(defaultPaddingComponent);
            }
        }
    }

    public static void addAll(Container container, Component... components) {
        addAll(container, false, components);
    }

    public static void centerDialog(IDialog dialog) {
        Point point = Application.getCenterPoint();
        point.translate(-dialog.getWidth() / 2, -dialog.getHeight() / 2);
        dialog.setLocation(point);
    }
}
