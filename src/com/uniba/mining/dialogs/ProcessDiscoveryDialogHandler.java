package com.uniba.mining.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;

import com.uniba.mining.actions.ProcessDiscoveryActionController;
import com.uniba.mining.logging.LogStreamer;
import com.uniba.mining.plugin.Config;
import com.uniba.mining.tasks.DeclareDiscoveryTask;
import com.uniba.mining.tasks.DiscoveryTask;
import com.uniba.mining.tasks.MinerfulDiscoveryTask;
import com.uniba.mining.utils.Application;
import com.uniba.mining.utils.GUI;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

import controller.discovery.DataConditionType;
import controller.discovery.DeclarePruningType;
import controller.discovery.DiscoveryMethod;
import minerful.postprocessing.params.PostProcessingCmdParameters.PostProcessingAnalysisType;
import task.discovery.DiscoveryTaskResult;
import task.discovery.mp_enhancer.MpEnhancer;
import util.ConstraintTemplate;
import util.ModelExporter;

public class ProcessDiscoveryDialogHandler implements IDialogHandler {

	private static final List<ConstraintTemplate> minerfulNotSupportedTemplates = Arrays.asList(
			ConstraintTemplate.Exactly1,
			ConstraintTemplate.Exactly2,
			ConstraintTemplate.Choice, ConstraintTemplate.Exclusive_Choice);
	private static final List<ConstraintTemplate> unaryTemplates = Arrays.asList(ConstraintTemplate.Absence,
			ConstraintTemplate.Absence2,
			ConstraintTemplate.Absence3, ConstraintTemplate.Exactly1, ConstraintTemplate.Exactly2,
			ConstraintTemplate.Existence, ConstraintTemplate.Existence2, ConstraintTemplate.Existence3,
			ConstraintTemplate.Init);
	private static final List<ConstraintTemplate> binaryPositiveTemplates = Arrays.asList(
			ConstraintTemplate.Alternate_Precedence,
			ConstraintTemplate.Alternate_Response, ConstraintTemplate.Alternate_Succession,
			ConstraintTemplate.Chain_Precedence,
			ConstraintTemplate.Chain_Response, ConstraintTemplate.Chain_Succession,
			ConstraintTemplate.CoExistence,
			ConstraintTemplate.Precedence, ConstraintTemplate.Responded_Existence,
			ConstraintTemplate.Response,
			ConstraintTemplate.Succession);
	private static final List<ConstraintTemplate> binaryNegativeTemplates = Arrays.asList(
			ConstraintTemplate.Not_Chain_Succession,
			ConstraintTemplate.Not_CoExistence, ConstraintTemplate.Not_Succession);
	private static final List<ConstraintTemplate> choiceTemplates = Arrays.asList(ConstraintTemplate.Choice,
			ConstraintTemplate.Exclusive_Choice);
	private static final List<ConstraintTemplate> discoverDataNotSupportedTemplates = Arrays.asList(
			ConstraintTemplate.Alternate_Succession,
			ConstraintTemplate.CoExistence,
			ConstraintTemplate.Succession);
	private static final String[] discoveryMethodItems = new String[] { DiscoveryMethod.DECLARE.getDisplayText(),
			DiscoveryMethod.MINERFUL.getDisplayText() };
	private static final String[] pruningTypeDeclareMinerItems = new String[] {
			DeclarePruningType.ALL_REDUCTIONS.getDisplayText(),
			DeclarePruningType.HIERARCHY_BASED.getDisplayText(),
			DeclarePruningType.TRANSITIVE_CLOSURE.getDisplayText(),
			DeclarePruningType.NONE.getDisplayText() };
	private static final String[] pruningTypeMinerfulItems = new String[] { "None", "Hierarchy", "Conflicts",
			"Redundancy", "Double redundancy" };
	private static final String[] discoverDataConditions = new String[] {
			DataConditionType.ACTIVATIONS.getDisplayText(), DataConditionType.CORRELATIONS.getDisplayText(),
			DataConditionType.NONE.getDisplayText() };

	boolean isDeclareMiner = true;
	boolean withDiscoverDataCondition = false;
	private File[] selectedLogFiles;
	private Map<File, DiscoveryTaskResult> discoveryTaskResults = new HashMap<>();

	private JPanel rootPanel;
	private JButton selectLogsButton;
	private JComboBox<String> discoveryMethodComboBox;
	private final List<ConstraintTemplate> selectedTemplates = new ArrayList<>(Arrays.asList(
			ConstraintTemplate.Absence,
			ConstraintTemplate.Absence2,
			ConstraintTemplate.Absence3, ConstraintTemplate.Exactly1, ConstraintTemplate.Exactly2,
			ConstraintTemplate.Existence, ConstraintTemplate.Existence2, ConstraintTemplate.Existence3,
			ConstraintTemplate.Init,
			ConstraintTemplate.Alternate_Precedence,
			ConstraintTemplate.Alternate_Response, ConstraintTemplate.Alternate_Succession,
			ConstraintTemplate.Chain_Precedence,
			ConstraintTemplate.Chain_Response, ConstraintTemplate.Chain_Succession,
			ConstraintTemplate.CoExistence,
			ConstraintTemplate.Precedence, ConstraintTemplate.Responded_Existence,
			ConstraintTemplate.Response,
			ConstraintTemplate.Succession,
			ConstraintTemplate.Not_Chain_Succession,
			ConstraintTemplate.Not_CoExistence, ConstraintTemplate.Not_Succession,
			ConstraintTemplate.Choice,
			ConstraintTemplate.Exclusive_Choice));

	private JCheckBox unaryCheckBox;
	private JCheckBox binaryPositiveCheckBox;
	private JCheckBox binaryNegativeCheckBox;
	private JCheckBox choiceCheckBox;
	private JSlider constraintSupportSlider;
	private JLabel pruningTypeLabel;
	private JComboBox<String> pruningTypeComboBox;
	private JToggleButton vacuousAsViolatedButton;
	private JToggleButton considerLifecycleButton;
	private JToggleButton discoverTimeConditionsButton;
	private JComboBox<String> discoverDataConditionsComboBox;
	private JButton discoveryButton;
	private JProgressBar progressBar;

	private Component getHeaderPanel() {
		JPanel headerPanel = new JPanel();
		JLabel selectFileLabel = new JLabel("Logs");
		Box selectFileBox = new Box(BoxLayout.PAGE_AXIS);
		Box selectFileInputBox = new Box(BoxLayout.LINE_AXIS);
		JTextArea selectFileTextArea = new JTextArea("No logs selected", 1, 20);
		selectLogsButton = new JButton("Select Logs");
		String discoverImagePath = String.join("/", Config.ICONS_PATH, "spaceman.png");
		ImageIcon discoverImage = GUI.loadImage(discoverImagePath, "Process discovery icon", 0.5f);
		JLabel discoverLabel = new JLabel(discoverImage);

		selectFileLabel.setLabelFor(selectFileInputBox);
		selectFileTextArea.setEnabled(false);
		selectLogsButton.addActionListener(e -> {
			JFileChooser fileChooser = GUI.createSelectFileChooser(
					ProcessDiscoveryActionController.ACTION_NAME, LogStreamer.getLogFileFilter(),
					true);
			fileChooser.setCurrentDirectory(LogStreamer.getLogsDirectory().toFile());

			if (fileChooser.showOpenDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
				selectedLogFiles = fileChooser.getSelectedFiles();
				selectFileTextArea.setRows(selectedLogFiles.length);
				selectFileTextArea.setText(
						Arrays.stream(selectedLogFiles).map(File::getName).reduce("",
								(t, u) -> t.isEmpty() ? "\u2022 " + u : String.join("\n", t, "\u2022 " + u)).trim());
				discoveryTaskResults.clear();
				discoveryButton.setEnabled(true);
			}
		});
		selectFileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		selectFileInputBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		GUI.addAll(selectFileInputBox, GUI.DEFAULT_PADDING, selectFileTextArea, selectLogsButton);
		GUI.addAll(selectFileBox, GUI.DEFAULT_PADDING, selectFileLabel, selectFileInputBox);
		GUI.addAll(headerPanel, selectFileBox, discoverLabel);
		return headerPanel;
	}

	private Component getDiscoveryMethodPanel() {
		JPanel discoveryMethodPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JLabel discoveryMethodLabel = new JLabel("Discovery Method");
		discoveryMethodComboBox = new JComboBox<>(discoveryMethodItems);
		discoveryMethodComboBox.setSelectedItem(discoveryMethodLabel);
		discoveryMethodComboBox.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				isDeclareMiner = e.getItem().equals("Declare Miner");
				choiceCheckBox.setVisible(isDeclareMiner);
				pruningTypeComboBox.removeAllItems();
				for (String pruningTypeItem : isDeclareMiner ? pruningTypeDeclareMinerItems
						: pruningTypeMinerfulItems) {
					pruningTypeComboBox.addItem(pruningTypeItem);
				}
				if (isDeclareMiner) {
					if (!withDiscoverDataCondition) {
						setSelectedTemplates(unaryCheckBox,
								Arrays.asList(ConstraintTemplate.Exactly1,
										ConstraintTemplate.Exactly2));
						setSelectedTemplates(choiceCheckBox,
								Arrays.asList(ConstraintTemplate.Choice,
										ConstraintTemplate.Exclusive_Choice));
					}
				} else {
					selectedTemplates.removeAll(minerfulNotSupportedTemplates);
					pruningTypeComboBox.setSelectedIndex(1);
					vacuousAsViolatedButton.setSelected(true);
					vacuousAsViolatedButton.doClick();
					considerLifecycleButton.setSelected(false);
					considerLifecycleButton.doClick();
					discoverTimeConditionsButton.setSelected(false);
					discoverTimeConditionsButton.doClick();
					discoverDataConditionsComboBox.setSelectedIndex(2);
				}
				pruningTypeLabel.setText(String.format("Pruning Type (%s)",
						isDeclareMiner ? "Declare" : "MINERful"));
				pruningTypeComboBox.setMaximumSize(pruningTypeComboBox.getPreferredSize());
				vacuousAsViolatedButton.setEnabled(isDeclareMiner);
				considerLifecycleButton.setEnabled(isDeclareMiner);
			}
		});

		discoveryMethodLabel.setLabelFor(discoveryMethodComboBox);
		GUI.addAll(discoveryMethodPanel, discoveryMethodLabel, discoveryMethodComboBox);
		return discoveryMethodPanel;
	}

	private void setSelectedTemplates(JCheckBox checkBox, List<ConstraintTemplate> templates) {
		if (checkBox.isSelected())
			selectedTemplates.addAll(templates);
		else
			selectedTemplates.removeAll(templates);
		if (!isDeclareMiner)
			selectedTemplates.removeAll(minerfulNotSupportedTemplates);
	}

	private Component getTemplatesPanel() {
		JPanel templatesPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JLabel templatesLabel = new JLabel("Templates");
		unaryCheckBox = new JCheckBox("Unary", true);
		binaryPositiveCheckBox = new JCheckBox("Binary Positive", true);
		binaryNegativeCheckBox = new JCheckBox("Binary Negative", true);
		choiceCheckBox = new JCheckBox("Choice", true);

		unaryCheckBox.addActionListener(e -> setSelectedTemplates(unaryCheckBox, unaryTemplates));

		binaryPositiveCheckBox
				.addActionListener(e -> setSelectedTemplates(binaryPositiveCheckBox,
						binaryPositiveTemplates));

		binaryNegativeCheckBox
				.addActionListener(e -> setSelectedTemplates(binaryNegativeCheckBox,
						binaryNegativeTemplates));

		choiceCheckBox.addActionListener(e -> setSelectedTemplates(choiceCheckBox, choiceTemplates));

		GUI.addAll(templatesPanel, templatesLabel, unaryCheckBox, binaryPositiveCheckBox,
				binaryNegativeCheckBox,
				choiceCheckBox);
		return templatesPanel;
	}

	private static void setToggleButtonText(JToggleButton toggleButton) {
		toggleButton.setText(toggleButton.isSelected() ? "Enabled" : "Disabled");
	}

	private Component getGeneralParametersPanel() {
		JPanel generalParametersPanel = new JPanel();
		JLabel constraintSupportLabel = new JLabel("Constraint Support");
		pruningTypeLabel = new JLabel("Pruning Type");
		JLabel vacuousAsViolatedLabel = new JLabel("Vacuous as Violated");
		JLabel considerLifecycleLabel = new JLabel("Consider Lifecycle");
		JLabel discoverTimeConditionsLabel = new JLabel("Discover Time Conditions");
		JLabel discoverDataConditionsLabel = new JLabel("Discover Data Conditions");
		constraintSupportSlider = new JSlider(0, 100, 90);
		pruningTypeComboBox = new JComboBox<>(pruningTypeDeclareMinerItems);
		vacuousAsViolatedButton = new JToggleButton("Enabled");
		vacuousAsViolatedButton.setSelected(true);
		considerLifecycleButton = new JToggleButton("Disabled");
		discoverTimeConditionsButton = new JToggleButton("Disabled");
		discoverDataConditionsComboBox = new JComboBox<>(discoverDataConditions);
		discoverDataConditionsComboBox.setSelectedItem(DataConditionType.NONE.getDisplayText());
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
		discoverDataConditionsComboBox.addActionListener(e -> {
			withDiscoverDataCondition = !discoverDataConditionsComboBox.getSelectedItem()
					.equals(DataConditionType.NONE.getDisplayText());
			if (withDiscoverDataCondition) {
				unaryCheckBox.setVisible(false);
				binaryPositiveCheckBox.setVisible(false);
				choiceCheckBox.setVisible(false);
				selectedTemplates.removeAll(unaryTemplates);
				selectedTemplates.removeAll(discoverDataNotSupportedTemplates);
				selectedTemplates.removeAll(binaryNegativeTemplates);
				selectedTemplates.removeAll(choiceTemplates);
			} else {
				unaryCheckBox.setVisible(true);
				binaryPositiveCheckBox.setVisible(true);
				if (isDeclareMiner)
					choiceCheckBox.setVisible(true);
				setSelectedTemplates(unaryCheckBox, unaryTemplates);
				setSelectedTemplates(binaryPositiveCheckBox, binaryPositiveTemplates);
				setSelectedTemplates(choiceCheckBox, choiceTemplates);
			}
		});
		discoverDataConditionsComboBox.setMaximumSize(discoverDataConditionsComboBox.getPreferredSize());
		GUI.addAll(constraintSupportBox,
				GUI.DEFAULT_PADDING, constraintSupportLabel,
				constraintSupportSlider);
		GUI.addAll(pruningTypeBox, GUI.DEFAULT_PADDING, pruningTypeLabel, pruningTypeComboBox);
		GUI.addAll(vacuousAsViolatedBox,
				GUI.DEFAULT_PADDING, vacuousAsViolatedLabel,
				vacuousAsViolatedButton);
		GUI.addAll(considerLifecycleBox,
				GUI.DEFAULT_PADDING, considerLifecycleLabel,
				considerLifecycleButton);
		GUI.addAll(discoverTimeConditionsBox,
				GUI.DEFAULT_PADDING, discoverTimeConditionsLabel,
				discoverTimeConditionsButton);
		GUI.addAll(discoverDataConditionsBox,
				GUI.DEFAULT_PADDING, discoverDataConditionsLabel,
				discoverDataConditionsComboBox);

		generalParametersPanel.setLayout(new BoxLayout(generalParametersPanel, BoxLayout.PAGE_AXIS));
		generalParametersPanel.setBorder(GUI.getDefaultTitledBorder("General Parameters"));
		GUI.addAll(generalParametersPanel,
				GUI.DEFAULT_PADDING, constraintSupportBox, pruningTypeBox, vacuousAsViolatedBox,
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
		GUI.addAll(contentPanel,
				GUI.DEFAULT_PADDING, new JSeparator(), getDiscoveryMethodPanel(),
				getTemplatesPanel(), getGeneralParametersPanel());

		return contentPanel;
	}

	private void discoverModel(Runnable callback) {
		int constraintSupport = constraintSupportSlider.getValue();
		DataConditionType dataConditionType = DataConditionType.values()[discoverDataConditionsComboBox
				.getSelectedIndex()];
		MpEnhancer mpEnhancer = new MpEnhancer();

		if (dataConditionType != DataConditionType.NONE) {
			mpEnhancer.setMinSupport(constraintSupport / 100d);
			mpEnhancer.setConditionType(dataConditionType);
		}

		System.out.println("Start discovering model(s) with the parameters:");
		System.out.println("- selectedTemplates = " + selectedTemplates);
		System.out.println("- constraintSupport = " + constraintSupport);
		System.out.println("- timeConditions = " + discoverTimeConditionsButton.isSelected());
		System.out.println("- dataConditions = " + dataConditionType.toString());

		DiscoveryTask discoveryTask;
		if (discoveryMethodComboBox.getSelectedIndex() == DiscoveryMethod.DECLARE.ordinal()) {
			DeclarePruningType declarePruningType = DeclarePruningType.values()[pruningTypeComboBox
					.getSelectedIndex()];
			DeclareDiscoveryTask discoveryTaskDeclare = new DeclareDiscoveryTask();

			discoveryTaskDeclare.setSelectedTemplates(selectedTemplates);
			discoveryTaskDeclare.setMinSupport(constraintSupport);
			discoveryTaskDeclare.setPruningType(declarePruningType);
			discoveryTaskDeclare.setVacuityAsViolation(vacuousAsViolatedButton.isSelected());
			discoveryTaskDeclare.setConsiderLifecycle(considerLifecycleButton.isSelected());
			discoveryTaskDeclare.setComuputeTimeDistances(discoverTimeConditionsButton.isSelected());

			if (dataConditionType != DataConditionType.NONE) {
				discoveryTaskDeclare.setMinSupport(0);
				discoveryTaskDeclare.setMpEnhancer(mpEnhancer);
			} else
				discoveryTaskDeclare.setMinSupport(constraintSupport);

			discoveryTask = discoveryTaskDeclare;
		} else {
			PostProcessingAnalysisType pruningType = PostProcessingAnalysisType.values()[pruningTypeComboBox
					.getSelectedIndex()];
			MinerfulDiscoveryTask discoveryTaskMinerful = new MinerfulDiscoveryTask();
			discoveryTaskMinerful.setSelectedTemplates(selectedTemplates);
			discoveryTaskMinerful.setMinSupport(constraintSupport);
			discoveryTaskMinerful.setPruningType(pruningType);
			discoveryTaskMinerful.setComuputeTimeDistances(discoverTimeConditionsButton.isSelected());

			if (dataConditionType != DataConditionType.NONE) {
				discoveryTaskMinerful.setMinSupport(0d);
				discoveryTaskMinerful.setMpEnhancer(mpEnhancer);
			} else
				discoveryTaskMinerful.setMinSupport(constraintSupport / 100d);

			discoveryTask = discoveryTaskMinerful;
		}
		Application.run(() -> {
			for (File selectedLogFile : selectedLogFiles) {
				System.out.println("Discover model for file: " + selectedLogFile.getName());
				discoveryTask.setLogFile(selectedLogFile);

				DiscoveryTaskResult discoveryTaskResult = discoveryTask.call();
				if (discoveryTaskResult == null)
					return;
				discoveryTaskResults.put(selectedLogFile, discoveryTaskResult);
				System.out.println(String.format("Discovery model completed for file: %s (%d/%d)",
						selectedLogFile.getName(), discoveryTaskResults.size(), selectedLogFiles.length));

			}
			if (selectedLogFiles.length == discoveryTaskResults.size())
				callback.run();
		});
	}

	private void exportModel(Path directoryPath) {
		Path filePath = directoryPath
				.resolve(Paths.get(Application.getTimestampString()
						+ LogStreamer.ZIP_EXTENSION));
		List<File> modelFiles = new ArrayList<>();
		for (Entry<File, DiscoveryTaskResult> discoveryEntry : discoveryTaskResults
				.entrySet()) {
			File selectedLogFile = discoveryEntry.getKey();
			DiscoveryTaskResult discoveryTaskResult = discoveryEntry.getValue();
			String selectedLogFileNameWithoutExtension = selectedLogFile.getName()
					.replaceAll(LogStreamer.LOG_EXTENSIONS_REGEX, "");
			Path modelDeclPath = LogStreamer.getModelsDirectory()
					.resolve(selectedLogFileNameWithoutExtension + ".decl");
			Path modelTextPath = LogStreamer.getModelsDirectory()
					.resolve(selectedLogFileNameWithoutExtension + ".txt");
			try {
				File modelDeclFile = Files
						.writeString(modelDeclPath, ModelExporter.getDeclString(
								selectedLogFile,
								discoveryTaskResult.getActivities(),
								discoveryTaskResult.getConstraints()))
						.toFile();
				File modelTextFile = Files.writeString(modelTextPath,
						ModelExporter.getTextString(
								discoveryTaskResult.getActivities(),
								discoveryTaskResult.getConstraints()))
						.toFile();
				modelFiles.addAll(Arrays.asList(modelDeclFile, modelTextFile));
			} catch (IOException exception) {
				exception.printStackTrace();
			}

		}
		LogStreamer.exportZip(filePath, modelFiles.toArray(File[]::new));
	}

	private Component getActionsPanel() {
		JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		discoveryButton = new JButton("Discover");
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
		progressBar.setString("Discovering model...");
		progressBar.setVisible(false);

		discoveryButton.addActionListener(e -> {
			if (discoveryButton.getText().equals("Cancel")) {
				discoveryButton.setText("Discover");
				selectLogsButton.setEnabled(true);
				progressBar.setVisible(false);
				Application.cancelTasks();
				discoveryTaskResults.clear();
				return;
			}

			JFileChooser fileChooser = GUI
					.createExportFileChooser(ProcessDiscoveryActionController.ACTION_NAME);
			if (fileChooser.showOpenDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
				discoveryButton.setText("Cancel");
				selectLogsButton.setEnabled(false);
				progressBar.setVisible(true);
				discoverModel(() -> {
					discoveryButton.setText("Discover");
					selectLogsButton.setEnabled(true);
					progressBar.setVisible(false);
					exportModel(fileChooser.getSelectedFile().toPath());
					discoveryTaskResults.clear();
					GUI.showInformationMessageDialog(rootPanel,
							ProcessDiscoveryActionController.ACTION_NAME,
							"Process model(s) successfully discovered and exported");
				});
			}

		});

		GUI.addAll(actionsPanel, discoveryButton, progressBar);

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
		GUI.prepareDialog(dialog, ProcessDiscoveryActionController.ACTION_NAME);
	}

	@Override
	public void shown() {
		// Empty
	}

}
