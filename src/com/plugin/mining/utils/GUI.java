package com.plugin.mining.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
        Component lastComponent = components[components.length - 1];
        for (Component component : components) {
            container.add(component);
            if (withPadding && component != lastComponent) {
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

    public static ImageIcon loadImage(String filePath, String description) {
        URL imageUrl = GUI.class.getResource(filePath);
        if (imageUrl != null) {
            return new ImageIcon(imageUrl, description);
        }
        throw new UnsupportedOperationException("The image is not found in the path: " + filePath);
    }

    public static ImageIcon loadImage(String filePath, String description, float scale) {
        ImageIcon imageIcon = loadImage(filePath, description);
        int scaledWidth = Math.round(imageIcon.getIconWidth() * scale);
        int scaledHeight = Math.round(imageIcon.getIconHeight() * scale);
        Image scaledImage = imageIcon.getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage, description);
    }

    public static JButton createLinkButton(String text, Icon icon) {
        JButton linkButton = icon == null ? new JButton(text) : new JButton(text, icon);
        linkButton.setOpaque(false);
        linkButton.setBackground(Color.LIGHT_GRAY);
        linkButton.setForeground(Color.BLUE);
        linkButton.setBorder(BorderFactory.createEmptyBorder());
        linkButton.setBorderPainted(false);
        linkButton.setFocusable(false);
        return linkButton;
    }

    public static JButton createLinkButton(String text) {
        return createLinkButton(text, null);
    }
}
