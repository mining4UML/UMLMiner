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
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import com.uniba.mining.plugin.Config;
import com.vp.plugin.ViewManager;
import com.vp.plugin.view.IDialog;

public class GUI {
    public static final int LOW_PADDING = 2;
    public static final int DEFAULT_PADDING = 4;
    public static final int HIGH_PADDING = 8;
    public static final int DEFAULT_BORDER_SIZE = 1;
    private static final Dimension defaultPaddingDimension = new Dimension(DEFAULT_PADDING, DEFAULT_PADDING);
    private static final Border defaultPaddingBorder = BorderFactory.createEmptyBorder(DEFAULT_PADDING, DEFAULT_PADDING,
            DEFAULT_PADDING, DEFAULT_PADDING);
    private static final Border defaultBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, DEFAULT_BORDER_SIZE, true), defaultPaddingBorder);
    private static final ViewManager viewManager = Application.getViewManager();

    private GUI() {
    }

    public static JComponent getPaddingComponent(Dimension dimension) {
        return (JComponent) Box.createRigidArea(dimension);
    }

    public static JComponent getDefaultPaddingComponent() {
        return getPaddingComponent(defaultPaddingDimension);
    }

    public static Border getDefaultBorder() {
        return defaultBorder;
    }

    public static Border getDefaultTitledBorder(String title) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder(title);
        titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(Font.BOLD));
        return BorderFactory.createCompoundBorder(titledBorder, defaultPaddingBorder);
    }

    public static Point getCenterPoint() {
        Component rootComponent = viewManager.getRootFrame();
        Point rootPoint = rootComponent.getLocation();
        Dimension rootDimension = rootComponent.getSize();
        int xCoordinate = (int) (rootPoint.getX() + rootDimension.getWidth() / 2);
        int yCoordinate = (int) (rootPoint.getY() + rootDimension.getHeight() / 2);

        return new Point(xCoordinate, yCoordinate);
    }

    public static void addAll(Container container, int padding, Component... components) {
        Component lastComponent = components[components.length - 1];
        for (Component component : components) {
            container.add(component);
            if (padding > 0 && component != lastComponent) {
                JComponent defaultPaddingComponent = getPaddingComponent(new Dimension(padding, padding));
                container.add(defaultPaddingComponent);
            }
        }
    }

    public static void addAll(Container container, Component... components) {
        addAll(container, 0, components);
    }

    public static void prepareDialog(IDialog dialog, String title) {
        Point point = getCenterPoint();
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

    public static ImageIcon loadImage(String filePath, String description, int width, int height) {
        ImageIcon imageIcon = loadImage(filePath, description);
        Image scaledImage = imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
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

    public static JLabel createLabel(String text, Icon icon, int horizontalAlignment) {
        JLabel label = new JLabel(text + ":", icon, horizontalAlignment);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        return label;
    }

    public static JLabel createLabel(String text, Icon icon) {
        return createLabel(text, icon, SwingConstants.LEADING);
    }

    public static JLabel createLabel(String text) {
        return createLabel(text, null);
    }

    private static void disableTextFields(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JTextField) {
                ((JTextField) component).setEditable(false);
            } else if (component instanceof Container) {
                disableTextFields((Container) component);
            }
        }
    }

    public static JFileChooser createSelectFileChooser(String title, boolean multiple, FileFilter... fileFilters) {
        String fullTitle = String.join(" - ", Config.PLUGIN_NAME, title);
        JFileChooser fileChooser = viewManager.createJFileChooser();

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        for (FileFilter fileFilter : fileFilters)
            fileChooser.addChoosableFileFilter(fileFilter);
        fileChooser.setFileFilter(fileFilters[0]);
        fileChooser.setMultiSelectionEnabled(multiple);
        fileChooser.setDialogTitle(fullTitle);
        fileChooser.setToolTipText(fullTitle);
        fileChooser.setApproveButtonText("Select");
        fileChooser.setApproveButtonToolTipText(fullTitle);
        return fileChooser;
    }
    
    public static File[] showFileSelectionDialog(Window parent, File logsDir, String title) {
    	String fullTitle = String.join(" - ", Config.PLUGIN_NAME, title);
        
    	JFileChooser fileChooser = viewManager.createJFileChooser();
    	fileChooser.setCurrentDirectory(logsDir);
    	fileChooser.setLocale(Locale.ENGLISH);
    	fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setDialogTitle(fullTitle);
        fileChooser.setApproveButtonToolTipText(fullTitle);
        fileChooser.setApproveButtonText("Select");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = (parent != null)
            ? fileChooser.showOpenDialog(parent)
            : fileChooser.showOpenDialog(null);

        return (result == JFileChooser.APPROVE_OPTION) ? fileChooser.getSelectedFiles() : new File[0];
    }
    
    

    public static JFileChooser createSelectFileChooser(String title, FileFilter... fileFilters) {
        return createSelectFileChooser(title, false, fileFilters);
    }

    public static JFileChooser createExportFileChooser(String title) {
        String fullTitle = String.join(" - ", Config.PLUGIN_NAME, title);
        JFileChooser fileChooser = viewManager.createJFileChooser();

        fileChooser.setLocale(Locale.ENGLISH);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setName(fullTitle);
        fileChooser.setDialogTitle(fullTitle);
        fileChooser.setToolTipText(fullTitle);
        fileChooser.setApproveButtonText("Export");
        fileChooser.setApproveButtonToolTipText(title);

        disableTextFields(fileChooser);

        return fileChooser;
    }
    
    public static JFileChooser createExportDirectoryChooser(String title) {
        String fullTitle = String.join(" - ", Config.PLUGIN_NAME, title);
        JFileChooser fileChooser = viewManager.createJFileChooser();

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false); // Opzionale: disabilita il filtro "tutti i file"
		
        
        fileChooser.setLocale(Locale.ENGLISH);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setName(fullTitle);
        fileChooser.setDialogTitle(fullTitle);
        fileChooser.setToolTipText(fullTitle);
        fileChooser.setApproveButtonText("Export");
        fileChooser.setApproveButtonToolTipText(title);

        disableTextFields(fileChooser);

        return fileChooser;
    }
    

    public static void showInformationMessageDialog(Component component, String title, String msg) {
        viewManager.showMessageDialog(component, msg, String.join(" - ", Config.PLUGIN_NAME, title),
                JOptionPane.INFORMATION_MESSAGE, getImageIcon());
    }

    public static void showWarningMessageDialog(Component component, String title, String msg) {
        viewManager.showMessageDialog(component, msg, String.join(" - ", Config.PLUGIN_NAME, title),
                JOptionPane.WARNING_MESSAGE, getImageIcon());
    }
    
    public static void showErrorMessageDialog(Component component, String title, String msg) {
        viewManager.showMessageDialog(component, msg, String.join(" - ", Config.PLUGIN_NAME, title),
                JOptionPane.ERROR_MESSAGE, getImageIcon());   
    }
    
    /**
     * 
     * @return The default image icon
     */
    public static ImageIcon getImageIcon() {
        String discoverImagePath = String.join("/", Config.ICONS_PATH, "spaceman.png");
 		ImageIcon discoverImage = GUI.loadImage(discoverImagePath, "Process discovery icon", 0.5f); 
    	return discoverImage;
    }
    
    /**
     * 
     * @return An image icon
     */
    public static ImageIcon getImageIcon(String iconName) {
        String discoverImagePath = String.join("/", Config.ICONS_PATH, iconName);
 		ImageIcon discoverImage = GUI.loadImage(discoverImagePath, "Icon", 1f); 
    	return discoverImage;
    }
    
}
