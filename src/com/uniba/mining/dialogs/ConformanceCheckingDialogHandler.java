package com.uniba.mining.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import com.uniba.mining.actions.ConformanceCheckingActionController;
import com.uniba.mining.logging.LogStreamer;
import com.uniba.mining.utils.Application;
import com.uniba.mining.utils.GUI;
import com.vp.plugin.ViewManager;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

import controller.conformance.ConformanceMethod;

public class ConformanceCheckingDialogHandler implements IDialogHandler {
    private static final ViewManager viewManager = Application.getViewManager();

    private JPanel rootPanel;
    private String[] checkingMethodItems = { ConformanceMethod.ANALYZER.getDisplayText(),
            ConformanceMethod.REPLAYER.getDisplayText() };
    private JComboBox<String> checkingMethodComboBox;

    private Component getHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, GUI.ULTRA_HIGH_PADDING, GUI.DEFAULT_PADDING));
        JLabel selectModelLabel = new JLabel("MP-Declare File");
        Box selectModelBox = new Box(BoxLayout.PAGE_AXIS);
        Box selectModelInputBox = new Box(BoxLayout.LINE_AXIS);
        JTextField selectModelTextField = new JTextField("No model selected", 20);
        JButton selectModelButton = new JButton("Select Log");
        JLabel selectLogLabel = new JLabel("Log File");
        Box selectLogBox = new Box(BoxLayout.PAGE_AXIS);
        Box selectLogInputBox = new Box(BoxLayout.LINE_AXIS);
        JTextField selectLogTextField = new JTextField("No log selected", 20);
        JButton selectLogButton = new JButton("Select Log");

        selectModelLabel.setLabelFor(selectModelInputBox);
        selectModelTextField.setEnabled(false);
        selectModelButton.addActionListener(e -> {
            JFileChooser fileChooser = GUI.createSelectFileChooser(
                    ConformanceCheckingActionController.ACTION_NAME, LogStreamer.getModelFileFilter(),
                    true);
            fileChooser.setCurrentDirectory(LogStreamer.getModelsDirectory().toFile());
        });
        selectLogLabel.setLabelFor(selectLogBox);
        selectLogTextField.setEnabled(false);
        selectLogButton.addActionListener(e -> {
            JFileChooser fileChooser = GUI.createSelectFileChooser(
                    ConformanceCheckingActionController.ACTION_NAME, LogStreamer.getLogFileFilter(),
                    true);
            fileChooser.setCurrentDirectory(LogStreamer.getLogsDirectory().toFile());
        });
        selectModelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectModelInputBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectLogLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectLogInputBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        GUI.addAll(selectModelInputBox, GUI.DEFAULT_PADDING, selectModelTextField, selectModelButton);
        GUI.addAll(selectLogInputBox, GUI.DEFAULT_PADDING, selectLogTextField, selectLogButton);
        GUI.addAll(selectModelBox, GUI.DEFAULT_PADDING, selectModelLabel, selectModelInputBox);
        GUI.addAll(selectLogBox, GUI.DEFAULT_PADDING, selectLogLabel, selectLogInputBox);
        GUI.addAll(headerPanel, selectModelBox, selectLogBox);
        return headerPanel;
    }

    private Component getCheckingMethodPanel() {
        JPanel checkingMethodPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JLabel checkingMethodLabel = new JLabel("Checking Method");
        checkingMethodComboBox = new JComboBox<>(checkingMethodItems);

        checkingMethodLabel.setLabelFor(checkingMethodComboBox);
        GUI.addAll(checkingMethodPanel, checkingMethodLabel, checkingMethodComboBox);
        return checkingMethodPanel;
    }

    private Component getContentPanel() {
        JPanel contentPanel = new JPanel();

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        GUI.addAll(contentPanel, GUI.DEFAULT_PADDING, new JSeparator(), getCheckingMethodPanel());
        return contentPanel;
    }

    private Component getActionsPanel() {
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        JButton actionsCheckButton = new JButton("Check");
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);

        actionsCheckButton.setEnabled(false);

        GUI.addAll(actionsPanel, actionsCheckButton, progressBar);

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
        GUI.prepareDialog(dialog, ConformanceCheckingActionController.ACTION_NAME);
    }

    @Override
    public void shown() {
        // Empty
    }

}
