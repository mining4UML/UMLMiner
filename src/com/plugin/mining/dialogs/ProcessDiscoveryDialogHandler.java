package com.plugin.mining.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import com.plugin.mining.actions.ProcessDiscoveryActionController;
import com.plugin.mining.logging.LogStreamer;
import com.plugin.mining.utils.Application;
import com.plugin.mining.utils.GUI;
import com.vp.plugin.ViewManager;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

public class ProcessDiscoveryDialogHandler implements IDialogHandler {
    private static final ViewManager viewManager = Application.getViewManager();
    private static final String[] discoveryMethodItems = new String[] { "Declare Miner", "MINERful" };
    private static final String[] pruningTypeDeclareMinerItems = new String[] { "All reductions", "Hierarchy-based",
            "Transitive Closure", "None" };
    private static final String[] pruningTypeMinerfulItems = new String[] { "None", "Hierarchy", "Conflicts",
            "Redundancy", "Double redundancy" };

    private File selectedLogFile;
    private JPanel rootPanel;
    private JComboBox<String> discoveryMethodComboBox;
    private JCheckBox choiceCheckBox;
    private JSlider constraintSupportSlider;
    private JComboBox<String> pruningTypeComboBox;
    private JToggleButton vacuousAsViolatedButton;
    private JToggleButton considerLifecycleButton;
    private JToggleButton discoverTimeConditionsButton;
    private JComboBox<String> discoverDataConditionsComboBox;
    private JButton actionsDiscoveryButton;

    private Component getHeaderPanel() {
        JPanel headerPanel = new JPanel();
        JLabel selectFileLabel = new JLabel("Log File");
        Box selectFileBox = new Box(BoxLayout.PAGE_AXIS);
        Box selectFileInputBox = new Box(BoxLayout.LINE_AXIS);
        JTextField selectFileTextField = new JTextField("No file selected", 20);
        JButton selectFileButton = new JButton("Select File");

        selectFileLabel.setLabelFor(selectFileInputBox);
        selectFileTextField.setEnabled(false);
        selectFileButton.addActionListener(e -> {
            JFileChooser fileChooser = viewManager.createJFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(LogStreamer.getLogFileFilter());
            fileChooser.setCurrentDirectory(LogStreamer.getLogDirectory().toFile());
            fileChooser.setDialogTitle("Select log file");
            fileChooser.setApproveButtonText("Select");

            if (fileChooser.showOpenDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
                selectedLogFile = fileChooser.getSelectedFile();
                selectFileTextField.setText(selectedLogFile.getName());
                actionsDiscoveryButton.setEnabled(true);
            }
        });
        GUI.addAll(selectFileInputBox, true, selectFileTextField, selectFileButton);
        GUI.addAll(selectFileBox, true, selectFileLabel, selectFileInputBox);
        selectFileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectFileInputBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        GUI.addAll(headerPanel, selectFileBox);
        return headerPanel;
    }

    private Component getDiscoveryMethodPanel() {
        JPanel discoveryMethodPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JLabel discoveryMethodLabel = new JLabel("Discovery Method");
        discoveryMethodComboBox = new JComboBox<>(discoveryMethodItems);
        discoveryMethodComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                boolean isDeclareMiner = e.getItem().equals("Declare Miner");
                choiceCheckBox.setVisible(isDeclareMiner);
                pruningTypeComboBox.removeAllItems();
                for (String pruningTypeItem : isDeclareMiner ? pruningTypeDeclareMinerItems : pruningTypeMinerfulItems)
                    pruningTypeComboBox.addItem(pruningTypeItem);
                pruningTypeComboBox.setMaximumSize(pruningTypeComboBox.getPreferredSize());
                vacuousAsViolatedButton.setEnabled(isDeclareMiner);
                considerLifecycleButton.setEnabled(isDeclareMiner);
            }
        });

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
        choiceCheckBox = new JCheckBox("Choice", true);
        choiceCheckBox.setVisible(false);

        GUI.addAll(templatesPanel, templatesLabel, unaryCheckBox, binaryPositiveCheckBox, binaryNegativeCheckBox,
                choiceCheckBox);
        return templatesPanel;
    }

    private static void setToggleButtonText(JToggleButton toggleButton) {
        toggleButton.setText(toggleButton.isSelected() ? "Enabled" : "Disabled");
    };

    private Component getGeneralParametersPanel() {
        JPanel generalParametersPanel = new JPanel();
        JLabel constraintSupportLabel = new JLabel("Constraint Support");
        JLabel pruningTypeLabel = new JLabel("Pruning Type");
        JLabel vacuousAsViolatedLabel = new JLabel("Vacuous as Violated");
        JLabel considerLifecycleLabel = new JLabel("Consider Lifecycle");
        JLabel discoverTimeConditionsLabel = new JLabel("Discover Time Conditions");
        JLabel discoverDataConditionsLabel = new JLabel("Discover Data Conditions");
        constraintSupportSlider = new JSlider(0, 100);
        pruningTypeComboBox = new JComboBox<>(pruningTypeDeclareMinerItems);
        vacuousAsViolatedButton = new JToggleButton("Disabled");
        considerLifecycleButton = new JToggleButton("Disabled");
        discoverTimeConditionsButton = new JToggleButton("Disabled");
        discoverDataConditionsComboBox = new JComboBox<>(
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
        pruningTypeComboBox.setMaximumSize(pruningTypeComboBox.getPreferredSize());
        vacuousAsViolatedButton.addActionListener(e -> setToggleButtonText(vacuousAsViolatedButton));
        considerLifecycleButton.addActionListener(e -> setToggleButtonText(considerLifecycleButton));
        discoverTimeConditionsButton.addActionListener(e -> setToggleButtonText(discoverTimeConditionsButton));
        discoverDataConditionsComboBox.setMaximumSize(discoverDataConditionsComboBox.getPreferredSize());
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
                considerLifecycleBox, discoverTimeConditionsBox, discoverDataConditionsBox);
        constraintSupportBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        pruningTypeBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        vacuousAsViolatedBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        considerLifecycleBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        discoverTimeConditionsBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        discoverDataConditionsBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        return generalParametersPanel;
    }

    private Component getContentPanel() {
        JPanel contentPanel = new JPanel();

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        GUI.addAll(contentPanel, true, new JSeparator(), getDiscoveryMethodPanel(),
                getTemplatesPanel(), getGeneralParametersPanel());

        return contentPanel;
    }

    private Component getActionsPanel() {
        JPanel actionsPanel = new JPanel();
        actionsDiscoveryButton = new JButton("Discovery");
        actionsDiscoveryButton.setEnabled(false);
        actionsPanel.add(actionsDiscoveryButton);
        actionsDiscoveryButton.addActionListener(e -> {

        });
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
        dialog.setTitle(ProcessDiscoveryActionController.ACTION_NAME);
        GUI.centerDialog(dialog);
    }

    @Override
    public void shown() {
        // Empty
    }

}