package com.plugin.mining.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.border.Border;

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
}
