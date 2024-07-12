package com.uniba.mining.feedback;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.uniba.mining.logging.LogStreamer;
import com.uniba.mining.plugin.Config;
import com.uniba.mining.utils.Application;
import com.uniba.mining.utils.GUI;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class DiagramUI {

	private Path requirementsDirectory;
	private String diagramId;
	private JPanel mainPanel;
	private JButton loadButton;
	private JTextArea statusTextArea;

	// Constructor initializing requirementsDirectory and diagramId
	public DiagramUI() {
		this.requirementsDirectory = LogStreamer.getRequirementsDirectory();
		this.diagramId = Application.getIDCurrentDiagram();
		createRequirementsDirectoryIfNeeded();
		initializeUI();
	}

	// Private method to create requirements directory if not exists
	private void createRequirementsDirectoryIfNeeded() {
		if (!Files.exists(requirementsDirectory)) {
			try {
				Files.createDirectories(requirementsDirectory);
				System.out.println("Created directory: " + requirementsDirectory);
			} catch (IOException e) {
				ErrorUtils.showDetailedErrorMessage(e);
				System.err.println("Error creating directory: " + e.getMessage());
			}
		}
	}

	// Method to load the file into the specified path
	public void loadFile(File fileToLoad) {
		if (fileToLoad == null || !fileToLoad.exists()) {
			System.err.println("Invalid or non-existent file.");
			return;
		}

		// Construct the full path for the new file
		Path destinationPath = requirementsDirectory.resolve(diagramId + ".txt");

		try {
			// Copy the file to the specified path
			Files.copy(fileToLoad.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("File loaded successfully: " + destinationPath);

			// Show confirmation message
			GUI.showInformationMessageDialog(mainPanel, Config.FEEDBACK_LABELREQU, "File saved successfully.");
			// Display file content in statusTextArea
			//displayFileContent(destinationPath);
		} catch (UnsupportedOperationException e) {
			ErrorUtils.showDetailedErrorMessage(e);
		} catch (FileAlreadyExistsException e) {
			ErrorUtils.showDetailedErrorMessage(e);
		} catch (DirectoryNotEmptyException e) {
			ErrorUtils.showDetailedErrorMessage(e);
		} catch (IOException e) {
			ErrorUtils.showDetailedErrorMessage(e);
			e.printStackTrace();
		} catch (SecurityException e) {
			ErrorUtils.showDetailedErrorMessage(e);
		}
		closeMainWindow();
	}

	// Method to display file content in statusTextArea
	private void displayFileContent(Path filePath) {
		try {
			System.out.println("Reading file: " + filePath);
			byte[] fileBytes = Files.readAllBytes(filePath);
			System.out.println("File bytes length: " + fileBytes.length);

			// Convert byte array to string using UTF-8 encoding
			String fileContent = new String(fileBytes, StandardCharsets.UTF_8);
			System.out.println("File content: " + fileContent);

			statusTextArea.setText(fileContent);
			statusTextArea.setEditable(false); // Disable editing
		} catch (IOException e) {
			ErrorUtils.showDetailedErrorMessage(e);
			System.err.println("IOException: " + e.getMessage());
		}
	}
	
	// Method to close the main window
    private void closeMainWindow() {
        Window window = SwingUtilities.getWindowAncestor(mainPanel);
        if (window != null) {
            window.dispose(); // Close the main window
        }
    }


	// Method to return the UI component
	public Component getComponent() {
		return mainPanel;
	}

	// Method to initialize the UI components
	private void initializeUI() {
		mainPanel = new JPanel(new BorderLayout());

		// Creazione dei componenti UI
		loadButton = new JButton("Load File");
		statusTextArea = new JTextArea(10, 30);
		statusTextArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(statusTextArea);

		// Ascoltatore per il pulsante di caricamento del file
		loadButton.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();

			// Imposta il filtro per mostrare solo file .txt
			FileNameExtensionFilter filter = new FileNameExtensionFilter("TxT files", "txt");
			fileChooser = GUI.createSelectFileChooser("Select requirements file", filter);

			int returnValue = fileChooser.showOpenDialog(mainPanel);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				loadFile(selectedFile);
			}
		});

		// Layout dei componenti
		JPanel inputPanel = new JPanel();
		inputPanel.add(new JLabel("Load File:"));
		inputPanel.add(loadButton);

		mainPanel.add(inputPanel, BorderLayout.NORTH);
		mainPanel.add(scrollPane, BorderLayout.CENTER);
	}

}
