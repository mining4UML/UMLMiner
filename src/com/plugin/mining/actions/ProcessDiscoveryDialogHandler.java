package com.plugin.mining.actions;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.plugin.mining.utils.Application;
import com.plugin.mining.utils.DialogFormatter;
import com.vp.plugin.ViewManager;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

public class ProcessDiscoveryDialogHandler implements IDialogHandler {
    private static final ViewManager viewManager = Application.getViewManager();

    @Override
    public boolean canClosed() {
        return true;
    }

    @Override
    public Component getComponent() {
        JPanel rootPanel = new JPanel(new BorderLayout());

        JPanel propertiesPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 10));
        JLabel discoveryMethodLabel = new JLabel("Discovery Method");
        JComboBox<String> discoveryMethodComboBox = new JComboBox<>(new String[] { "Declare Miner", "MINERful" });
        Box discoveryMethodBox = new Box(BoxLayout.Y_AXIS);
        discoveryMethodLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        discoveryMethodLabel.setLabelFor(discoveryMethodComboBox);
        discoveryMethodBox.add(discoveryMethodLabel);
        discoveryMethodBox.add(discoveryMethodComboBox);
        propertiesPanel.setBorder(BorderFactory.createTitledBorder("General Parameters"));
        propertiesPanel.add(discoveryMethodBox);
        propertiesPanel.add(new JComboBox<String>(new String[] { "Value1", "Value2" }));
        propertiesPanel.add(new JComboBox<String>(new String[] { "Value1", "Value2" }));

        JPanel actionsPanel = new JPanel();
        actionsPanel.add(new JButton("Discovery"));

        rootPanel.add(propertiesPanel, BorderLayout.PAGE_START);
        rootPanel.add(actionsPanel, BorderLayout.LINE_END);
        return rootPanel;
    }

    @Override
    public void prepare(IDialog dialog) {
        dialog.pack();
        dialog.setModal(true);
        dialog.setResizable(false);
        dialog.setTitle(ProcessDiscoveryActionController.ACTION_NAME);
        DialogFormatter.centerDialog(dialog);
    }

    @Override
    public void shown() {
        // Empty
    }

}
