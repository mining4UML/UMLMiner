package com.uniba.mining.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.Border;

import com.uniba.mining.plugin.Config;
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

    public static void prepareDialog(IDialog dialog, String title) {
        Point point = Application.getCenterPoint();
        dialog.pack();
        point.translate(-dialog.getWidth() / 2, -dialog.getHeight() / 2);
        dialog.setLocation(point);
        dialog.setModal(true);
        dialog.setResizable(false);
        dialog.setTitle(String.join(" - ", Config.PLUGIN_NAME, title));
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

    public static JButton createLinkButton(String text, URI uri, Icon icon) {
        JButton linkButton = icon == null ? new JButton(text) : new JButton(text, icon);
        Map<TextAttribute, Object> textAttributes = new HashMap<>(linkButton.getFont().getAttributes());
        linkButton.setOpaque(false);
        linkButton.setBackground(Color.LIGHT_GRAY);
        linkButton.setForeground(Color.BLUE);
        linkButton.setBorder(BorderFactory.createEmptyBorder());
        linkButton.setBorderPainted(false);
        linkButton.setFocusable(false);
        linkButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        linkButton.addActionListener(e -> {
            if (Desktop.isDesktopSupported())
                try {
                    Desktop.getDesktop().browse(uri);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            throw new UnsupportedOperationException("Desktop is not supported for browsing");
        });
        linkButton.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                // Empty
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // Empty
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                /// Empty
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                textAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                linkButton.setFont(linkButton.getFont().deriveFont(textAttributes));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                textAttributes.put(TextAttribute.UNDERLINE, -1);
                linkButton.setFont(linkButton.getFont().deriveFont(textAttributes));
            }

        });
        return linkButton;
    }

    public static JButton createLinkButton(String text, URI uri) {
        return createLinkButton(text, uri, null);
    }
}
