package com.plugin.mining.actions;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import com.plugin.mining.logging.LogStream;
import com.plugin.mining.utils.Application;
import com.plugin.mining.utils.DialogFormatter;
import com.plugin.mining.utils.GUI;
import com.vp.plugin.ViewManager;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

public class ProcessDiscoveryDialogHandler implements IDialogHandler {
    private static final ViewManager viewManager = Application.getViewManager();
    JPanel rootPanel;

    private Component getSelectFilePanel() {
        JPanel selectFilePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JLabel selectFileLabel = new JLabel("Log File");
        Box selectFileBox = new Box(BoxLayout.PAGE_AXIS);
        Box selectFileInputBox = new Box(BoxLayout.LINE_AXIS);
        JTextField selectFileTextField = new JTextField("No file selected", 20);
        JButton selectFileButton = new JButton("Select File");

        selectFileLabel.setLabelFor(selectFileInputBox);
        selectFileTextField.setEnabled(false);
        selectFileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectFileInputBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectFileButton.addActionListener(e -> {
            JFileChooser fileChooser = viewManager.createJFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(LogStream.getLogFileFilter());
            fileChooser.setCurrentDirectory(LogStream.getLogDirectory().toFile());

            if (fileChooser.showOpenDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
                selectFileTextField.setText(fileChooser.getSelectedFile().getName());
            }
        });
        GUI.addAll(selectFileInputBox, true, selectFileTextField, selectFileButton);
        GUI.addAll(selectFileBox, true, selectFileLabel, selectFileInputBox);
        GUI.addAll(selectFilePanel, selectFileBox);
        return selectFilePanel;
    }

    private Component getDiscoveryMethodPanel() {
        JPanel discoveryMethodPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JLabel discoveryMethodLabel = new JLabel("Discovery Method");
        JComboBox<String> discoveryMethodComboBox = new JComboBox<>(new String[] { "Declare Miner", "MINERful" });

        discoveryMethodLabel.setLabelFor(discoveryMethodComboBox);
        GUI.addAll(discoveryMethodPanel, discoveryMethodLabel, discoveryMethodComboBox);
        return discoveryMethodPanel;
    }

    private Component getTemplatesPanel() {
        JPanel templatesPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JLabel templatesLabel = new JLabel("Templates");
        JCheckBox unaryCheckBox = new JCheckBox("Unary", true);
        JCheckBox binaryPositiveCheckBox = new JCheckBox("Binary Positive", true);
        JCheckBox binaryNegativeCheckBox = new JCheckBox("Binary Negative", true);
        JCheckBox choiceCheckBox = new JCheckBox("Choice", true);

        GUI.addAll(templatesPanel, templatesLabel, unaryCheckBox, binaryPositiveCheckBox, binaryNegativeCheckBox,
                choiceCheckBox);
        return templatesPanel;
    }

    private Component getGeneralParametersPanel() {
        JPanel generalParametersPanel = new JPanel();
        JLabel constraintSupportLabel = new JLabel("Constraint Support");
        JLabel pruningTypeLabel = new JLabel("Pruning Type");
        JLabel vacuousAsViolatedLabel = new JLabel("Vacuous as Violated");
        JLabel considerLifecycleLabel = new JLabel("Consider Lifecycle");
        JLabel discoverTimeConditionsLabel = new JLabel("Discover Time Conditions");
        JLabel discoverDataConditionsLabel = new JLabel("Discover Data Conditions");
        JSlider constraintSupportSlider = new JSlider(0, 100);
        JComboBox<String> pruningTypeComboBox = new JComboBox<>(
                new String[] { "All reductions", "Hierarchy-based", "Transitive Closure", "None" });
        JToggleButton vacuousAsViolatedButton = new JToggleButton("Enable");
        JToggleButton considerLifecycleButton = new JToggleButton("Enable");
        JToggleButton discoverTimeConditionsButton = new JToggleButton("Enable");
        JComboBox<String> discoverDataConditionsComboBox = new JComboBox<>(
                new String[] { "Activations", "Correlations", "None" });
        Box constraintSupportBox = new Box(BoxLayout.LINE_AXIS);
        Box pruningTypeBox = new Box(BoxLayout.LINE_AXIS);
        Box vacuousAsViolatedBox = new Box(BoxLayout.LINE_AXIS);
        Box considerLifecycleBox = new Box(BoxLayout.LINE_AXIS);
        Box discoverTimeConditionsBox = new Box(BoxLayout.LINE_AXIS);
        Box discoverDataConditionsBox = new Box(BoxLayout.LINE_AXIS);

        constraintSupportSlider.setMajorTickSpacing(10);
        constraintSupportSlider.setMinorTickSpacing(1);
        constraintSupportSlider.setPaintTicks(true);
        constraintSupportSlider.setPaintLabels(true);
        GUI.addAll(constraintSupportBox, true, constraintSupportLabel,
                constraintSupportSlider);
        GUI.addAll(pruningTypeBox, true, pruningTypeLabel, pruningTypeComboBox);
        GUI.addAll(vacuousAsViolatedBox, true, vacuousAsViolatedLabel,
                vacuousAsViolatedButton);
        GUI.addAll(considerLifecycleBox, true, considerLifecycleLabel,
                considerLifecycleButton);
        GUI.addAll(discoverTimeConditionsBox, true, discoverTimeConditionsLabel,
                discoverTimeConditionsButton);
        GUI.addAll(discoverDataConditionsBox, true, discoverDataConditionsLabel,
                discoverDataConditionsComboBox);

        generalParametersPanel.setLayout(new BoxLayout(generalParametersPanel, BoxLayout.PAGE_AXIS));
        generalParametersPanel.setBorder(GUI.getDefaultTitledBorder("General Parameters"));
        GUI.addAll(generalParametersPanel, true, constraintSupportBox, pruningTypeBox, vacuousAsViolatedBox,
                considerLifecycleBox, discoverTimeConditionsBox,
                discoverDataConditionsBox);
        return generalParametersPanel;
    }

    private Component getContentPanel() {
        JPanel contentPanel = new JPanel();

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        GUI.addAll(contentPanel, true, getDiscoveryMethodPanel(), getTemplatesPanel(), getGeneralParametersPanel());

        return contentPanel;
    }

    private Component getActionsPanel() {
        JPanel actionsPanel = new JPanel();
        actionsPanel.add(new JButton("Discovery"));
        return actionsPanel;
    }

    @Override
    public boolean canClosed() {
        return true;
    }

    @Override
    public Component getComponent() {
        rootPanel = new JPanel(new BorderLayout());

        rootPanel.add(getSelectFilePanel(), BorderLayout.PAGE_START);
        rootPanel.add(getContentPanel(), BorderLayout.CENTER);
        rootPanel.add(getActionsPanel(), BorderLayout.PAGE_END);
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
