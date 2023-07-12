package com.uniba.mining.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.HyperlinkEvent;

import com.uniba.mining.actions.AboutActionController;
import com.uniba.mining.plugin.Config;
import com.uniba.mining.utils.GUI;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

public class AboutDialogHandler implements IDialogHandler {
    private JPanel rootPanel;

    private Component getHeaderPanel() {
        JPanel headerPanel = new JPanel();

        return headerPanel;
    }

    private Component getInfoPanel() {
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JEditorPane infoEditorPane = new JEditorPane("text/html; charset=UTF-8", Config.PLUGIN_DESCRIPTION);
        Dimension infoTextAreaDimension = new Dimension(640, 100);

        infoEditorPane.setPreferredSize(infoTextAreaDimension);
        infoEditorPane.setOpaque(false);
        infoEditorPane.setEditable(false);
        infoEditorPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (IOException | URISyntaxException exception) {
                    exception.printStackTrace();
                }
        });

        GUI.addAll(infoPanel, infoEditorPane);
        return infoPanel;
    }

    private Component getContactPanel() {
        JPanel contactPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JLabel homepageLabel = new JLabel("Homepage: ");
        JButton homepageButton = GUI.createLinkButton(Config.PLUGIN_HOMEPAGE, URI.create(Config.PLUGIN_HOMEPAGE));
        Box homepageBox = new Box(BoxLayout.LINE_AXIS);

        GUI.addAll(homepageBox, homepageLabel, homepageButton);
        contactPanel.setBorder(GUI.getDefaultTitledBorder("Contact Information"));
        contactPanel.add(homepageBox);
        return contactPanel;
    }

    private Component getContentPanel() {
        JPanel contentPanel = new JPanel();
        String logoImagePath = String.join("/", Config.IMAGES_PATH, "logo.jpg");
        ImageIcon logoImageIcon = GUI.loadImage(logoImagePath, "UML Miner Logo", 0.5f);
        JLabel logoLabel = new JLabel(Config.PLUGIN_VERSION, logoImageIcon, SwingConstants.CENTER);

        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        logoLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        logoLabel.setForeground(Color.BLUE);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        GUI.addAll(contentPanel, GUI.DEFAULT_PADDING, logoLabel, getInfoPanel(), getContactPanel());
        return contentPanel;
    }

    private Component getActionsPanel() {
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JButton newsButton = GUI.createLinkButton("Check out the latest information of UML Miner",
                URI.create(Config.PLUGIN_HOMEPAGE));

        actionsPanel.add(newsButton);
        return actionsPanel;
    }

    @Override
    public boolean canClosed() {
        return true;
    }

    @Override
    public Component getComponent() {
        rootPanel = new JPanel(new BorderLayout());

        rootPanel.add(getHeaderPanel(), BorderLayout.PAGE_START);
        rootPanel.add(getContentPanel(), BorderLayout.CENTER);
        rootPanel.add(getActionsPanel(), BorderLayout.PAGE_END);
        return rootPanel;
    }

    @Override
    public void prepare(IDialog dialog) {
        GUI.prepareDialog(dialog, AboutActionController.ACTION_NAME);
    }

    @Override
    public void shown() {
        // Empty
    }

}
