package com.plugin.mining.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.plugin.mining.actions.AboutActionController;
import com.plugin.mining.plugin.Config;
import com.plugin.mining.utils.Application;
import com.plugin.mining.utils.GUI;
import com.vp.plugin.ViewManager;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

public class AboutDialogHandler implements IDialogHandler {
    private static final ViewManager viewManager = Application.getViewManager();

    private JPanel rootPanel;

    private Component getHeaderPanel() {
        JPanel headerPanel = new JPanel();

        return headerPanel;
    }

    private Component getContactPanel() {
        JPanel contactPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JLabel homepageLabel = new JLabel("Homepage: ");
        JButton homepageButton = GUI.createLinkButton("www.link.to.site.it");
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
        JLabel logoLabel = new JLabel("Version 1.0 (Build 20230601)", logoImageIcon, SwingConstants.CENTER);

        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        logoLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        logoLabel.setForeground(Color.BLUE);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        GUI.addAll(contentPanel, true, logoLabel, getContactPanel());
        return contentPanel;
    }

    private Component getActionsPanel() {
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JButton newsButton = GUI.createLinkButton("Check out the latest information of UML Miner");

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
        dialog.pack();
        dialog.setModal(true);
        dialog.setResizable(false);
        dialog.setTitle(AboutActionController.ACTION_NAME);
        GUI.centerDialog(dialog);
    }

    @Override
    public void shown() {
        // Empty
    }

}
