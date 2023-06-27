package com.uniba.mining.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XesXmlSerializer;
import org.processmining.plugins.DataConformance.framework.ActivityMatchCost;

import com.uniba.mining.actions.ConformanceCheckingActionController;
import com.uniba.mining.logging.LogStreamer;
import com.uniba.mining.logging.Logger;
import com.uniba.mining.plugin.Config;
import com.uniba.mining.tasks.ConformanceAnalyzerTask;
import com.uniba.mining.tasks.ConformanceReplayerTask;
import com.uniba.mining.tasks.ConformanceTask;
import com.uniba.mining.utils.Application;
import com.uniba.mining.utils.GUI;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

import controller.conformance.ConformanceMethod;
import task.conformance.ConformanceStatisticType;
import task.conformance.ConformanceTaskResult;
import task.conformance.ConformanceTaskResultGroup;
import util.LogUtils;
import util.ModelExporter;
import util.ModelUtils;

public class ConformanceCheckingDialogHandler implements IDialogHandler {
    private String[] checkingMethodItems = { ConformanceMethod.ANALYZER.getDisplayText(),
            ConformanceMethod.REPLAYER.getDisplayText() };
    private String[] optionsGroupItems = { "Traces", "Constraints" };
    private JPanel rootPanel;
    private JButton selectModelButton;
    private JButton selectLogButton;
    private JComboBox<String> checkingMethodComboBox;
    private JComboBox<String> optionsGroupComboBox;
    private JButton checkButton;

    private File selectedModelFile;
    private File selectedLogFile;
    private ConformanceTaskResult conformanceTaskResult;

    private Component getHeaderPanel() {
        final String selectButtonText = "Select";
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        Box selectBox = new Box(BoxLayout.PAGE_AXIS);
        JLabel selectModelLabel = GUI.createLabel("MP-Declare");
        Box selectModelBox = new Box(BoxLayout.PAGE_AXIS);
        Box selectModelInputBox = new Box(BoxLayout.LINE_AXIS);
        JTextField selectModelTextField = new JTextField("No model selected", 20);
        selectModelButton = new JButton(selectButtonText);
        JLabel selectLogLabel = GUI.createLabel("Log");
        Box selectLogBox = new Box(BoxLayout.PAGE_AXIS);
        Box selectLogInputBox = new Box(BoxLayout.LINE_AXIS);
        JTextField selectLogTextField = new JTextField("No log selected", 20);
        selectLogButton = new JButton(selectButtonText);
        String checkingImagePath = String.join("/", Config.ICONS_PATH, "checklist.png");
        ImageIcon checkImage = GUI.loadImage(checkingImagePath, "Conformance checking icon", 0.5f);
        JLabel checkLabel = new JLabel(checkImage);
        Dimension textFieldDimension = new Dimension(140, selectLogTextField.getMinimumSize().height);

        selectModelLabel.setLabelFor(selectModelInputBox);
        selectModelTextField.setPreferredSize(textFieldDimension);
        selectModelTextField.setMaximumSize(textFieldDimension);
        selectModelTextField.setEnabled(false);
        selectModelButton.addActionListener(e -> {
            JFileChooser fileChooser = GUI.createSelectFileChooser(
                    ConformanceCheckingActionController.ACTION_NAME, LogStreamer.getModelFileFilter(),
                    true);
            fileChooser.setCurrentDirectory(LogStreamer.getModelsDirectory().toFile());

            if (fileChooser.showOpenDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
                selectedModelFile = fileChooser.getSelectedFile();
                selectModelTextField.setText(selectedModelFile.getName());
                if (selectedModelFile != null && selectedLogFile != null)
                    checkButton.setEnabled(true);
            }
        });
        selectLogLabel.setLabelFor(selectLogBox);
        selectLogTextField.setPreferredSize(textFieldDimension);
        selectLogTextField.setMaximumSize(textFieldDimension);
        selectLogTextField.setEnabled(false);
        selectLogButton.addActionListener(e -> {
            JFileChooser fileChooser = GUI.createSelectFileChooser(
                    ConformanceCheckingActionController.ACTION_NAME, LogStreamer.getLogFileFilter(),
                    true);
            fileChooser.setCurrentDirectory(LogStreamer.getLogsDirectory().toFile());
            if (fileChooser.showOpenDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
                selectedLogFile = fileChooser.getSelectedFile();
                selectLogTextField.setText(selectedLogFile.getName());
                if (selectedModelFile != null && selectedLogFile != null)
                    checkButton.setEnabled(true);
            }
        });
        selectModelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectModelInputBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectLogLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectLogInputBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        GUI.addAll(selectModelInputBox, GUI.DEFAULT_PADDING, selectModelTextField, selectModelButton);
        GUI.addAll(selectLogInputBox, GUI.DEFAULT_PADDING, selectLogTextField, selectLogButton);
        GUI.addAll(selectModelBox, GUI.DEFAULT_PADDING, selectModelLabel, selectModelInputBox);
        GUI.addAll(selectLogBox, GUI.DEFAULT_PADDING, selectLogLabel, selectLogInputBox);
        GUI.addAll(selectBox, GUI.HIGH_PADDING, selectModelBox, selectLogBox);
        GUI.addAll(headerPanel, selectBox, checkLabel);
        return headerPanel;
    }

    private Component getCheckingMethodPanel() {
        JPanel checkingMethodPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JLabel checkingMethodLabel = GUI.createLabel("Checking Method");
        checkingMethodComboBox = new JComboBox<>(checkingMethodItems);

        checkingMethodLabel.setLabelFor(checkingMethodComboBox);
        GUI.addAll(checkingMethodPanel, checkingMethodLabel, checkingMethodComboBox);
        return checkingMethodPanel;
    }

    private Component getOptionsPanel() {
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JLabel optionsGroupLabel = new JLabel("Group Results By");
        optionsGroupComboBox = new JComboBox<>(optionsGroupItems);

        GUI.addAll(optionsPanel, optionsGroupLabel, optionsGroupComboBox);
        optionsPanel.setBorder(GUI.getDefaultTitledBorder("Options"));
        return optionsPanel;
    }

    private Component getContentPanel() {
        JPanel contentPanel = new JPanel();

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        GUI.addAll(contentPanel, GUI.DEFAULT_PADDING, new JSeparator(), getCheckingMethodPanel(), getOptionsPanel());
        return contentPanel;
    }

    private File exportCsvData() {
        List<ConformanceTaskResultGroup> results = optionsGroupComboBox.getSelectedItem().equals("Traces")
                ? conformanceTaskResult.getResultsGroupedByTrace()
                : conformanceTaskResult.getResultsGroupedByConstraint();
        List<Map<ConformanceStatisticType, String>> statistics = results.stream()
                .map(ConformanceTaskResultGroup::getGroupStatistics)
                .collect(Collectors.toList());

        Path directoryPath = LogStreamer.getReportsDirectory();
        Path filePath = directoryPath.resolve(Paths.get("report" + LogStreamer.CSV_EXTENSION));
        try {
            Files.writeString(filePath, ModelExporter.getConformanceDataAsCsv(statistics));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath.toFile();
    }

    private File exportFulfilledLog() {
        List<String> fulfilledTraceNames = new LinkedList<>();
        ConformanceMethod method = ConformanceMethod.values()[checkingMethodComboBox.getSelectedIndex()];

        List<ConformanceTaskResultGroup> results = conformanceTaskResult.getResultsGroupedByTrace();
        for (ConformanceTaskResultGroup res : results) {
            String traceName = res.getXtrace().getAttributes().get(XConceptExtension.KEY_NAME).toString();

            switch (method) {
                case ANALYZER:
                    String violationNum = res.getGroupStatistics().get(ConformanceStatisticType.VIOLATIONS);
                    String vacuousViolationNum = res.getGroupStatistics()
                            .get(ConformanceStatisticType.VACUOUS_VIOLATIONS);

                    if (Integer.parseInt(violationNum) <= 0 && Integer.parseInt(vacuousViolationNum) <= 0)
                        fulfilledTraceNames.add(traceName);

                    break;

                case REPLAYER:
                case DATA_REPLAYER:
                    String fitness = res.getGroupStatistics().get(ConformanceStatisticType.FITNESS);

                    if (Double.parseDouble(fitness) == 1.0)
                        fulfilledTraceNames.add(traceName);

                    break;

                default:
                    break;
            }
        }

        if (!fulfilledTraceNames.isEmpty()) {
            XLog originalLog = LogUtils.convertToXlog(selectedLogFile);
            String newName;
            if (XConceptExtension.instance().extractName(originalLog) != null)
                newName = XConceptExtension.instance().extractName(originalLog) + " - Fulfilled traces";
            else
                newName = "Fulfilled traces extracted from: " + selectedLogFile.getName();

            List<XTrace> fulfilledTraces = originalLog.stream()
                    .filter(trace -> fulfilledTraceNames
                            .contains(trace.getAttributes().get(XConceptExtension.KEY_NAME).toString()))
                    .collect(Collectors.toList());

            XLog fulfilledLog = Logger.xFactory.createLog();
            fulfilledLog.addAll(fulfilledTraces);
            XConceptExtension.instance().assignName(fulfilledLog, newName);
            fulfilledLog.getExtensions().addAll(originalLog.getExtensions());
            fulfilledLog.getClassifiers().addAll(originalLog.getClassifiers());
            fulfilledLog.getGlobalEventAttributes().addAll(originalLog.getGlobalEventAttributes());
            fulfilledLog.getGlobalTraceAttributes().addAll(originalLog.getGlobalTraceAttributes());

            Path directoryPath = LogStreamer.getReportsDirectory();
            Path filePath = directoryPath.resolve(Paths.get("fulfilledLog" + LogStreamer.LOG_EXTENSION));
            File file = filePath.toFile();
            try (FileOutputStream outStream = new FileOutputStream(file)) {
                new XesXmlSerializer().serialize(fulfilledLog, outStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;
        }
        return null;
    }

    private File exportViolatedLog() {
        List<String> violatedTraceNames = new LinkedList<>();
        ConformanceMethod method = ConformanceMethod.values()[checkingMethodComboBox.getSelectedIndex()];

        List<ConformanceTaskResultGroup> results = conformanceTaskResult.getResultsGroupedByTrace();
        for (ConformanceTaskResultGroup res : results) {
            String traceName = res.getXtrace().getAttributes().get(XConceptExtension.KEY_NAME).toString();

            switch (method) {
                case ANALYZER:
                    String violationNum = res.getGroupStatistics().get(ConformanceStatisticType.VIOLATIONS);
                    String vacuousViolationNum = res.getGroupStatistics()
                            .get(ConformanceStatisticType.VACUOUS_VIOLATIONS);

                    if (Integer.parseInt(violationNum) > 0 || Integer.parseInt(vacuousViolationNum) > 0)
                        violatedTraceNames.add(traceName);

                    break;

                case REPLAYER:
                case DATA_REPLAYER:
                    String fitness = res.getGroupStatistics().get(ConformanceStatisticType.FITNESS);

                    if (Double.parseDouble(fitness) < 1.0)
                        violatedTraceNames.add(traceName);

                    break;

                default:
                    break;
            }
        }

        if (!violatedTraceNames.isEmpty()) {
            XLog originalLog = LogUtils.convertToXlog(selectedLogFile);
            String newName;
            if (XConceptExtension.instance().extractName(originalLog) != null)
                newName = XConceptExtension.instance().extractName(originalLog) + " - Violated traces";
            else
                newName = "Violated traces extracted from: " + selectedLogFile.getName();

            List<XTrace> violatedTraces = originalLog.stream()
                    .filter(trace -> violatedTraceNames
                            .contains(trace.getAttributes().get(XConceptExtension.KEY_NAME).toString()))
                    .collect(Collectors.toList());

            XLog violatedLog = Logger.xFactory.createLog();
            violatedLog.addAll(violatedTraces);
            XConceptExtension.instance().assignName(violatedLog, newName);
            violatedLog.getExtensions().addAll(originalLog.getExtensions());
            violatedLog.getClassifiers().addAll(originalLog.getClassifiers());
            violatedLog.getGlobalEventAttributes().addAll(originalLog.getGlobalEventAttributes());
            violatedLog.getGlobalTraceAttributes().addAll(originalLog.getGlobalTraceAttributes());

            Path directoryPath = LogStreamer.getReportsDirectory();
            Path filePath = directoryPath.resolve(Paths.get("violatedLog" + LogStreamer.LOG_EXTENSION));
            File file = filePath.toFile();
            FileOutputStream outStream;
            try {
                outStream = new FileOutputStream(file);
                new XesXmlSerializer().serialize(violatedLog, outStream);
                outStream.flush();
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;
        }
        return null;
    }

    private List<ActivityMatchCost> getActivityMatchCosts() {
        List<ActivityMatchCost> activityMatchCosts = new ArrayList<>();

        // Insertion default (may be overridden below)
        ActivityMatchCost defaultInsertionMatchCost = new ActivityMatchCost();
        defaultInsertionMatchCost.setAllEvents(false);
        defaultInsertionMatchCost.setAllProcessActivities(true);
        defaultInsertionMatchCost.setCost(10f);
        defaultInsertionMatchCost.setEventClass(null);
        defaultInsertionMatchCost.setProcessActivity(null);

        // Deletion default (may be overridden below)
        ActivityMatchCost defaultDeletionMatchCost = new ActivityMatchCost();
        defaultDeletionMatchCost.setAllEvents(true);
        defaultDeletionMatchCost.setAllProcessActivities(false);
        defaultDeletionMatchCost.setCost(10f);
        defaultDeletionMatchCost.setEventClass(null);
        defaultDeletionMatchCost.setProcessActivity(null);

        // Default cost objects must be added last, otherwise Declare Replayer and
        // DataAware Declare Replayer ignore specific costs
        activityMatchCosts.add(defaultInsertionMatchCost);
        activityMatchCosts.add(defaultDeletionMatchCost);

        return activityMatchCosts;
    }

    private void checkConformance(Runnable callback) {
        ConformanceMethod conformanceMethod = ConformanceMethod.values()[checkingMethodComboBox
                .getSelectedIndex()];
        ConformanceTask conformanceTask = conformanceMethod == ConformanceMethod.ANALYZER
                ? new ConformanceAnalyzerTask()
                : new ConformanceReplayerTask();
        if (conformanceMethod == ConformanceMethod.REPLAYER)
            ((ConformanceReplayerTask) conformanceTask).setActivityMatchCosts(getActivityMatchCosts());
        Application.run(() -> {
            conformanceTask.setLogFile(selectedLogFile);
            try {
                File xmlFile = new File(URI.create(ModelUtils.createTmpXmlModel(selectedModelFile).getAbsolutePath()));
                conformanceTask.setXmlModel(xmlFile);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            conformanceTaskResult = conformanceTask.call();
            if (conformanceTaskResult == null)
                return;
            callback.run();
        });
    }

    private Component getActionsPanel() {
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        checkButton = new JButton("Check");
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setString("Checking conformance...");
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);

        checkButton.setEnabled(false);

        checkButton.addActionListener(e -> {
            if (checkButton.getText().equals("Cancel")) {
                checkButton.setText("Check");
                selectModelButton.setEnabled(true);
                selectLogButton.setEnabled(true);
                progressBar.setVisible(false);
                Application.cancelTasks();
                conformanceTaskResult = null;
                return;
            }

            JFileChooser fileChooser = GUI.createExportFileChooser(ConformanceCheckingActionController.ACTION_NAME);
            if (fileChooser.showOpenDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
                checkButton.setText("Cancel");
                selectModelButton.setEnabled(false);
                selectLogButton.setEnabled(false);
                progressBar.setVisible(true);

                checkConformance(() -> {
                    checkButton.setText("Check");
                    selectModelButton.setEnabled(true);
                    selectLogButton.setEnabled(true);
                    progressBar.setVisible(false);
                    Path directoryPath = fileChooser.getSelectedFile().toPath();
                    Path filePath = directoryPath
                            .resolve(Paths.get(Application.getStringTimestamp()) + LogStreamer.ZIP_EXTENSION);
                    LogStreamer.exportZip(filePath, exportCsvData(), exportFulfilledLog(), exportViolatedLog());
                    GUI.showInformationMessageDialog(rootPanel, ConformanceCheckingActionController.ACTION_NAME,
                            "Report successfully created and exported.");
                });
            }
        });

        GUI.addAll(actionsPanel, checkButton, progressBar);

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
