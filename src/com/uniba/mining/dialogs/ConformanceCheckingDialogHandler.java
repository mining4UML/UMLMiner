package com.uniba.mining.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.deckfour.xes.out.XesXmlSerializer;
import org.processmining.plugins.DataConformance.framework.ActivityMatchCost;
import org.processmining.plugins.DeclareConformance.ReplayableActivityDefinition;

import com.uniba.mining.actions.ConformanceCheckingActionController;
import com.uniba.mining.logging.LogStreamer;
import com.uniba.mining.logging.Logger;
import com.uniba.mining.plugin.Config;
import com.uniba.mining.tasks.ActivityMappingReplayerTask;
import com.uniba.mining.tasks.ConformanceAnalyzerTask;
import com.uniba.mining.tasks.ConformanceReplayerTask;
import com.uniba.mining.tasks.ConformanceTask;
import com.uniba.mining.utils.Application;
import com.uniba.mining.utils.GUI;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

import controller.conformance.ConformanceMethod;
import task.conformance.ActivityConformanceType;
import task.conformance.ConformanceStatisticType;
import task.conformance.ConformanceTaskResult;
import task.conformance.ConformanceTaskResultDetail;
import task.conformance.ConformanceTaskResultGroup;
import util.LogUtils;
import util.ModelExporter;
import util.ModelUtils;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;


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
	//private File selectedLogFile;
	private List<File> selectedLogFiles = new ArrayList<>();
	private ConformanceTaskResult conformanceTaskResult;

	private XConceptExtension xce = XConceptExtension.instance();	



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
					ConformanceCheckingActionController.ACTION_NAME, true, LogStreamer.getModelFileFilter());
			fileChooser.setCurrentDirectory(LogStreamer.getModelsDirectory().toFile());

			if (fileChooser.showOpenDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
				selectedModelFile = fileChooser.getSelectedFile();
				selectModelTextField.setText(selectedModelFile.getName());
				if (selectedModelFile != null && !selectedLogFiles.isEmpty())
					checkButton.setEnabled(true);
			}
		});
		selectLogLabel.setLabelFor(selectLogBox);
		selectLogTextField.setPreferredSize(textFieldDimension);
		selectLogTextField.setMaximumSize(textFieldDimension);
		selectLogTextField.setEnabled(false);
		selectLogButton.addActionListener(e -> {
			JFileChooser fileChooser = GUI.createSelectFileChooser(
					ConformanceCheckingActionController.ACTION_NAME, true, LogStreamer.getLogFileFilter());
			fileChooser.setMultiSelectionEnabled(true); // Abilita selezione multipla
			fileChooser.setCurrentDirectory(LogStreamer.getLogsDirectory().toFile());

			if (fileChooser.showOpenDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
				selectedLogFiles.clear();
				File[] files = fileChooser.getSelectedFiles();
				for (File file : files) {
					selectedLogFiles.add(file);
				}
				selectLogTextField.setText(selectedLogFiles.size() + " logs selected");
				if (selectedModelFile != null && !selectedLogFiles.isEmpty()) {
					checkButton.setEnabled(true);
				}
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

	private File exportCsvData(File logFile) {
		List<ConformanceTaskResultGroup> results = conformanceTaskResult.getResultsGroupedByTrace();
		List<Map<ConformanceStatisticType, String>> statistics = results.stream()
				.map(ConformanceTaskResultGroup::getGroupStatistics)
				.collect(Collectors.toList());

		String fileName = logFile.getName().replaceAll(LogStreamer.LOG_EXTENSIONS_REGEX, "") + "_report.csv";
		Path filePath = LogStreamer.getReportsDirectory().resolve(fileName);
		File file = filePath.toFile();

		try (FileWriter writer = new FileWriter(file);
				BufferedWriter bufferedWriter = new BufferedWriter(writer)) {

			// Scrive l'intestazione del CSV
			String header = String.join(",", statistics.get(0).keySet().stream()
					.map(Enum::name)
					.collect(Collectors.toList()));
			bufferedWriter.write(header);
			bufferedWriter.newLine();

			// Scrive i dati riga per riga
			for (Map<ConformanceStatisticType, String> row : statistics) {
				String line = row.values().stream()
						.map(value -> "\"" + value.replace("\"", "\"\"") + "\"") // Escape per il CSV
						.collect(Collectors.joining(","));
				bufferedWriter.write(line);
				bufferedWriter.newLine();
			}

			return file;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}



	private File exportViolatedLog(File logFile) {
		List<String> violatedTraceNames = new LinkedList<>();
		ConformanceMethod method = ConformanceMethod.values()[checkingMethodComboBox.getSelectedIndex()];

		List<ConformanceTaskResultGroup> results = conformanceTaskResult.getResultsGroupedByTrace();
		for (ConformanceTaskResultGroup res : results) {
			String traceName = res.getXtrace().getAttributes().get(XConceptExtension.KEY_NAME).toString();

			switch (method) {
			case ANALYZER:
				String violationNum = res.getGroupStatistics().get(ConformanceStatisticType.VIOLATIONS);
				String vacuousViolationNum = res.getGroupStatistics().get(ConformanceStatisticType.VACUOUS_VIOLATIONS);
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
			XLog originalLog = LogUtils.convertToXlog(logFile);
			String newName = "Violated traces extracted from: " + logFile.getName();

			XLog violatedLog = Logger.xFactory.createLog();
			violatedLog.addAll(originalLog.stream()
					.filter(trace -> violatedTraceNames.contains(trace.getAttributes().get(XConceptExtension.KEY_NAME).toString()))
					.collect(Collectors.toList()));

			XConceptExtension.instance().assignName(violatedLog, newName);
			violatedLog.getExtensions().addAll(originalLog.getExtensions());
			violatedLog.getClassifiers().addAll(originalLog.getClassifiers());
			violatedLog.getGlobalEventAttributes().addAll(originalLog.getGlobalEventAttributes());
			violatedLog.getGlobalTraceAttributes().addAll(originalLog.getGlobalTraceAttributes());

			String fileName = logFile.getName().replaceAll(LogStreamer.LOG_EXTENSIONS_REGEX, "") + "_violatedLog.xes";
			Path filePath = LogStreamer.getReportsDirectory().resolve(fileName);
			File file = filePath.toFile();

			try (FileOutputStream outStream = new FileOutputStream(file)) {
				new XesXmlSerializer().serialize(violatedLog, outStream);
				return file;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}


	private File exportAlignedLog(File logFile) {
		XLog originalLog = LogUtils.convertToXlog(logFile);
		String newName = "Aligned log extracted from: " + logFile.getName();

		XLog alignedLog = new XLogImpl(new XAttributeMapImpl());
		XConceptExtension.instance().assignName(alignedLog, newName);
		alignedLog.getExtensions().addAll(originalLog.getExtensions());
		alignedLog.getClassifiers().addAll(originalLog.getClassifiers());
		alignedLog.getGlobalEventAttributes().addAll(originalLog.getGlobalEventAttributes());
		alignedLog.getGlobalTraceAttributes().addAll(originalLog.getGlobalTraceAttributes());

		List<ConformanceTaskResultGroup> results = conformanceTaskResult.getResultsGroupedByTrace();
		for (ConformanceTaskResultGroup res : results) {
			XTrace alignedTrace = new XTraceImpl(new XAttributeMapImpl());
			XTrace resTrace = res.getXtrace();

			for (Map.Entry<String, XAttribute> entry : resTrace.getAttributes().entrySet())
				alignedTrace.getAttributes().put(entry.getKey(), entry.getValue());

			List<ActivityConformanceType> conformanceTypes = res.getGroupDetails().get(0).getActivityConformanceTypes();
			for (int i = 0; i < conformanceTypes.size(); i++) {
				if (conformanceTypes.get(i).getType() != ActivityConformanceType.Type.DELETION_OTHER
						&& conformanceTypes.get(i).getType() != ActivityConformanceType.Type.DELETION) {
					alignedTrace.add(resTrace.get(i));
				}
			}

			alignedLog.add(alignedTrace);
		}

		if (!alignedLog.isEmpty()) {
			String fileName = logFile.getName().replaceAll(LogStreamer.LOG_EXTENSIONS_REGEX, "") + "_alignedLog.xes";
			Path filePath = LogStreamer.getReportsDirectory().resolve(fileName);
			File file = filePath.toFile();

			try (FileOutputStream outStream = new FileOutputStream(file)) {
				new XesXmlSerializer().serialize(alignedLog, outStream);
				return file;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}



	private List<Map<String, String>> exportReport() {
		List<ConformanceTaskResultGroup> results = optionsGroupComboBox.getSelectedItem().equals("Traces")
				? conformanceTaskResult.getResultsGroupedByTrace()
						: conformanceTaskResult.getResultsGroupedByConstraint();

		List<Map<String, String>> reportData = new ArrayList<>();

		for (ConformanceTaskResultGroup resultGroup : results) {
			for (ConformanceTaskResultDetail groupDetail : resultGroup.getGroupDetails()) {
				for (int i = 0; i < groupDetail.getActivityConformanceTypes().size(); i++) {
					ActivityConformanceType acType = groupDetail.getActivityConformanceTypes().get(i);
					if (acType.getType() == ActivityConformanceType.Type.FULFILLMENT
							|| acType.getType() == ActivityConformanceType.Type.VIOLATION
							|| acType.getType() == ActivityConformanceType.Type.INSERTION
							|| acType.getType() == ActivityConformanceType.Type.DELETION
							|| acType.getType() == ActivityConformanceType.Type.DATA_DIFFERENCE) {

						Map<String, String> reportRow = new LinkedHashMap<>();
						reportRow.put("Trace", groupDetail.getTraceName());

						String constraintAll = groupDetail.getConstraint().replace("\n", "");
						int indexOfColons = constraintAll.indexOf(":");
						String constraint = constraintAll.substring(0, indexOfColons);
						String activities = constraintAll.substring(indexOfColons + 1).replace("[]", "");

						reportRow.put("Constraint", constraint);
						reportRow.put("Activities", activities);
						reportRow.put("Result type", acType.getType().toString().toLowerCase());
						reportRow.put("Activity name", xce.extractName(groupDetail.getXtrace().get(i)));
						reportRow.put("Activity index", Integer.toString(i + 1));

						XAttributeMap map = groupDetail.getXtrace().get(i).getAttributes();
						Map<String, String> valori = populateMap(map);

						reportRow.put("DiagramName", valori.getOrDefault("DiagramName", ""));
						reportRow.put("DiagramType", valori.getOrDefault("diagramType", ""));
						reportRow.put("UMLElementType", valori.getOrDefault("UMLElementType", ""));
						reportRow.put("UMLElementName", valori.getOrDefault("UMLElementName", ""));
						reportRow.put("PropertyName", valori.getOrDefault("PropertyName", ""));
						reportRow.put("PropertyValue", valori.getOrDefault("PropertyValue", ""));
						reportRow.put("RelationshipFrom", valori.getOrDefault("RelationshipFrom", ""));
						reportRow.put("RelationshipTo", valori.getOrDefault("RelationshipTo", ""));

						reportData.add(reportRow);
					}
				}
			}
		}
		return reportData;
	}


	private File saveReportToFile(List<Map<String, String>> reportData, String fileName) {
		if (reportData.isEmpty()) {
			System.out.println("Nessun dato da esportare.");
			return null;
		}

		Path reportsDir = LogStreamer.getReportsDirectory();
		Path filePath = reportsDir.resolve(fileName + "_violations_report.csv");
		File file = filePath.toFile();

		try (FileWriter writer = new FileWriter(file);
				BufferedWriter bufferedWriter = new BufferedWriter(writer)) {

			// Scrive l'intestazione del CSV
			String header = String.join(",", reportData.get(0).keySet());
			bufferedWriter.write(header);
			bufferedWriter.newLine();

			// Scrive i dati riga per riga
			for (Map<String, String> row : reportData) {
				String line = row.values().stream()
						.map(value -> "\"" + value.replace("\"", "\"\"") + "\"") // Escape per il CSV
						.collect(Collectors.joining(","));
				bufferedWriter.write(line);
				bufferedWriter.newLine();
			}

			return file;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


	private static Map<String,String> populateMap(XAttributeMap map) {

		Set<Entry<String, XAttribute>> mappa =  map.entrySet();

		Map<String,String> valori = new HashMap<String,String>();

		for (Entry<String, XAttribute> elemento : mappa) {
			if (elemento.getKey().equals("DiagramName")) {
				valori.put("DiagramName", elemento.getValue().toString());
			}
			else if (elemento.getKey().equals("UMLElementType")) {
				valori.put("UMLElementType", elemento.getValue().toString());	
			}
			else if (elemento.getKey().equals("UMLElementName")) {
				valori.put("UMLElementName", elemento.getValue().toString());

			}
			else if (elemento.getKey().equals("DiagramType")) {
				valori.put("diagramType", elemento.getValue().toString());

			}
			else if (elemento.getKey().equals("PropertyName")) {
				valori.put("PropertyName", elemento.getValue().toString());

			}
			else if (elemento.getKey().equals("PropertyValue")) {
				valori.put("PropertyValue", elemento.getValue().toString());
			}
			else if (elemento.getKey().equals("RelationshipFrom")) {
				valori.put("RelationshipFrom", elemento.getValue().toString());
			}
			else if (elemento.getKey().equals("RelationshipTo")) {
				valori.put("RelationshipTo", elemento.getValue().toString());
			}
		}
		return valori;

	}





	private Map<ReplayableActivityDefinition, XEventClass> getActivityMapping(File xmlFile, File logFile) {
		ActivityMappingReplayerTask activityMappingReplayerTask = new ActivityMappingReplayerTask();
		activityMappingReplayerTask.setXmlModel(xmlFile);
		activityMappingReplayerTask.setLogFile(logFile); // Usa il file log passato come parametro
		return activityMappingReplayerTask.call().getActivityMapping();
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

	private Future<Void> checkConformance(File logFile, Runnable callback) {
		FutureTask<Void> futureTask = new FutureTask<>(() -> {
			try {
				File tmpXmlFile = ModelUtils.createTmpXmlModel(selectedModelFile);
				String decodedPath = URLDecoder.decode(tmpXmlFile.getPath(), Charset.defaultCharset());
				File xmlFile = new File(decodedPath);

				ConformanceMethod conformanceMethod = ConformanceMethod.values()[checkingMethodComboBox.getSelectedIndex()];
				ConformanceTask conformanceTask = conformanceMethod == ConformanceMethod.ANALYZER
						? new ConformanceAnalyzerTask()
								: new ConformanceReplayerTask();

				conformanceTask.setXmlModel(xmlFile);
				conformanceTask.setLogFile(logFile);

				if (conformanceMethod == ConformanceMethod.REPLAYER) {
					ConformanceReplayerTask conformanceReplayerTask = (ConformanceReplayerTask) conformanceTask;
					Map<ReplayableActivityDefinition, XEventClass> activityMapping = getActivityMapping(xmlFile, logFile);
					List<ActivityMatchCost> activityMatchCosts = getActivityMatchCosts();
					conformanceReplayerTask.setActivityMapping(activityMapping);
					conformanceReplayerTask.setActivityMatchCosts(activityMatchCosts);
				}

				conformanceTaskResult = conformanceTask.call();

				if (conformanceTaskResult != null) {
					callback.run();
				}
			} catch (IOException exception) {
				System.err.println("Error processing log file: " + logFile.getAbsolutePath());
				exception.printStackTrace();
			}
			return null;
		});

		// Esegue il task in un nuovo thread
		new Thread(futureTask).start();
		return futureTask;
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

				List<File> allReportFiles = new ArrayList<>();
				int totalFiles = selectedLogFiles.size();
				int[] processedFiles = {0}; // Inizializzato in un array per renderlo modificabile

				for (File logFile : selectedLogFiles) {
					System.out.println("Processing file: " + logFile.getAbsolutePath());

					// Ora `checkConformance()` è chiamato separatamente per ogni file
					Future<Void> task = checkConformance(logFile, () -> {
						try {
							ConformanceMethod conformanceMethod = ConformanceMethod.values()[checkingMethodComboBox.getSelectedIndex()];
							String logFileNameWithoutExtension = logFile.getName().replaceAll(LogStreamer.LOG_EXTENSIONS_REGEX, "");

							// Generazione report
							File csvReport = exportCsvData(logFile);
							File fulfilledLog = exportFulfilledLog(logFile);
							File violatedLog = exportViolatedLog(logFile);
							File alignedLog = (conformanceMethod == ConformanceMethod.REPLAYER) ? exportAlignedLog(logFile) : null;
							List<Map<String, String>> reportData = exportReport();
							File violationReport = saveReportToFile(reportData, logFileNameWithoutExtension);

							// Aggiunta alla lista
							if (csvReport != null && csvReport.exists()) allReportFiles.add(csvReport);
							if (fulfilledLog != null && fulfilledLog.exists()) allReportFiles.add(fulfilledLog);
							if (violatedLog != null && violatedLog.exists()) allReportFiles.add(violatedLog);
							if (alignedLog != null && alignedLog.exists()) allReportFiles.add(alignedLog);
							if (violationReport != null && violationReport.exists()) allReportFiles.add(violationReport);

							processedFiles[0]++;
							System.out.println("Processed files: " + processedFiles + "/" + totalFiles);
						} catch (Exception ex) {
							System.err.println("Error processing file: " + logFile.getAbsolutePath());
							ex.printStackTrace();
						}
					});

					// Aspettiamo che il task termini prima di passare al successivo
					try {
						task.get(); // Blocca l'esecuzione finché il task non è completato
					} catch (InterruptedException | ExecutionException ex) {
						System.err.println("Error waiting for conformance task.");
						ex.printStackTrace();
					}
				}

				// Dopo l'ultimo file, creazione dello ZIP
				System.out.println("All files processed. Creating ZIP...");

				if (!allReportFiles.isEmpty()) {
					Path directoryPath = fileChooser.getSelectedFile().toPath();
					Path zipFilePath = directoryPath.resolve("conformance_reports_" + System.currentTimeMillis() + ".zip");

					File zipFile = zipFilePath.toFile();
					if (zipFile.exists()) {
						zipFile.delete();
					}

					LogStreamer.exportZip(zipFilePath, allReportFiles.toArray(new File[0]));
					System.out.println("ZIP created: " + zipFilePath);

					GUI.showInformationMessageDialog(rootPanel, ConformanceCheckingActionController.ACTION_NAME,
							"Report successfully created and exported: " + zipFilePath);
				} else {
					System.out.println("No files were added to allReportFiles. ZIP will not be created.");
				}

				checkButton.setText("Check");
				selectModelButton.setEnabled(true);
				selectLogButton.setEnabled(true);
				progressBar.setVisible(false);
			}
		});

		GUI.addAll(actionsPanel, checkButton, progressBar);
		return actionsPanel;
	}



	public static Future<Void> submit(Callable<Void> task) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<Void> future = executor.submit(task);
		executor.shutdown();
		return future;
	}



	private File exportFulfilledLog(File logFile) {
		List<String> fulfilledTraceNames = new LinkedList<>();
		ConformanceMethod method = ConformanceMethod.values()[checkingMethodComboBox.getSelectedIndex()];

		List<ConformanceTaskResultGroup> results = conformanceTaskResult.getResultsGroupedByTrace();
		for (ConformanceTaskResultGroup res : results) {
			String traceName = res.getXtrace().getAttributes().get(XConceptExtension.KEY_NAME).toString();

			switch (method) {
			case ANALYZER:
				String violationNum = res.getGroupStatistics().get(ConformanceStatisticType.VIOLATIONS);
				String vacuousViolationNum = res.getGroupStatistics().get(ConformanceStatisticType.VACUOUS_VIOLATIONS);
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
			XLog originalLog = LogUtils.convertToXlog(logFile);
			String newName = "Fulfilled traces extracted from: " + logFile.getName(); // Usa il file corrente

			XLog fulfilledLog = Logger.xFactory.createLog();
			fulfilledLog.addAll(originalLog.stream()
					.filter(trace -> fulfilledTraceNames.contains(trace.getAttributes().get(XConceptExtension.KEY_NAME).toString()))
					.collect(Collectors.toList()));

			XConceptExtension.instance().assignName(fulfilledLog, newName);
			fulfilledLog.getExtensions().addAll(originalLog.getExtensions());
			fulfilledLog.getClassifiers().addAll(originalLog.getClassifiers());
			fulfilledLog.getGlobalEventAttributes().addAll(originalLog.getGlobalEventAttributes());
			fulfilledLog.getGlobalTraceAttributes().addAll(originalLog.getGlobalTraceAttributes());

			// Nome del file generato
			String fileName = logFile.getName().replaceAll(LogStreamer.LOG_EXTENSIONS_REGEX, "") + "_fulfilledLog.xes";
			Path filePath = LogStreamer.getReportsDirectory().resolve(fileName);
			File file = filePath.toFile();

			try (FileOutputStream outStream = new FileOutputStream(file)) {
				new XesXmlSerializer().serialize(fulfilledLog, outStream);
				return file;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
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