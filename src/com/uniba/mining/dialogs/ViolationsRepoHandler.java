package com.uniba.mining.dialogs;

import javax.swing.*;
import com.uniba.mining.tasks.repoviolations.ViolationMessageGenerator;
import com.uniba.mining.utils.GUI;
import com.uniba.mining.logging.LogStreamer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViolationsRepoHandler extends JFrame {

	private static final long serialVersionUID = 1L;
	private JButton processButton;
	private JList<String> fileList;
	private DefaultListModel<String> listModel;
	private JCheckBox selectAllCheckBox;
	private List<File> csvFiles;

	public ViolationsRepoHandler() {
		setSize(500, 500);
		if (!initComponents()) {
			JOptionPane.showMessageDialog(null, "No CSV files found in the default folder.", "Warning", JOptionPane.WARNING_MESSAGE);
			dispose();
		} else {
			setVisible(true);
		}
	}

	private boolean initComponents() {
		processButton = new JButton("Process Selected Files");
		processButton.setEnabled(false);
		selectAllCheckBox = new JCheckBox("Select/Deselect All");

		listModel = new DefaultListModel<>();
		fileList = new JList<>(listModel);
		fileList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane scrollPane = new JScrollPane(fileList);

		if (!loadCSVFiles()) {
			return false;
		}

		fileList.addListSelectionListener(e -> {
			processButton.setEnabled(!fileList.isSelectionEmpty());
		});

		selectAllCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (csvFiles != null && !csvFiles.isEmpty()) {
					if (selectAllCheckBox.isSelected()) {
						fileList.setSelectionInterval(0, csvFiles.size() - 1);
					} else {
						fileList.clearSelection();
					}
				}
			}
		});

		processButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				processSelectedFiles();
			}
		});

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(scrollPane);
		getContentPane().add(selectAllCheckBox);
		getContentPane().add(processButton);

		setTitle("CSV Processor");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		return true;
	}

	private boolean loadCSVFiles() {
		listModel.clear();
		csvFiles = new ArrayList<>();

		File violationsDirectory = LogStreamer.getReportsDirectory().toFile();
		File[] files = violationsDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
		if (files != null) {
			for (File file : files) {
				csvFiles.add(file);
				listModel.addElement(file.getName());
			}
		}
		return !csvFiles.isEmpty();
	}

	private void processSelectedFiles() {
		List<String> selectedValues = fileList.getSelectedValuesList();
		if (selectedValues.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No files selected for processing.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		JFileChooser dirChooser = new JFileChooser();
		dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = dirChooser.showSaveDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) {
			File saveDirectory = dirChooser.getSelectedFile();

			for (String fileName : selectedValues) {
				File fileToProcess = new File(LogStreamer.getReportsDirectory().toFile(), fileName);
				File processedFile = ViolationMessageGenerator.processCSV(fileToProcess, saveDirectory);

				if (processedFile == null) {
					GUI.showErrorMessageDialog(this, "Processing Error", "File " + fileName + " is not compliant.");
					continue;
				}

				File outputFile = new File(saveDirectory, "FINAL-" + fileName);
				if (!processedFile.renameTo(outputFile)) {
					JOptionPane.showMessageDialog(this, "Error saving file: " + fileName, "Error", JOptionPane.ERROR_MESSAGE);
					continue;
				}
			}

			JOptionPane.showMessageDialog(this, "Selected files processed and saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
			dispose();
			processButton.setEnabled(false);
			fileList.clearSelection();
			selectAllCheckBox.setSelected(false);
		}
	}
}
