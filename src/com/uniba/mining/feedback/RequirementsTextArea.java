package com.uniba.mining.feedback;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.uniba.mining.logging.LogStreamer;
import com.vp.plugin.diagram.IDiagramUIModel;

public class RequirementsTextArea {
	
	private JTextArea requirementsTextArea;
	private JLabel previewRequirements;
	
	public RequirementsTextArea(IDiagramUIModel diagram) {
		requirementsTextArea = new JTextArea();
		requirementsTextArea.setBackground(new Color(225, 235, 245)); // Azzurrino chiaro

		requirementsTextArea.setEditable(false); // Make the text area non-editable
		requirementsTextArea.setLineWrap(true); // Enable line wrapping
		requirementsTextArea.setWrapStyleWord(true); // Wrap at word boundaries
		requirementsTextArea.setPreferredSize(new Dimension(0, requirementsTextArea.getPreferredSize().height)); // Set default width
		// Set default text for when no requirements are found
		requirementsTextArea.setText("Requirements NOT Found");

		previewRequirements = new JLabel();
		previewRequirements.setText("Requirements Preview");

		previewRequirements.setHorizontalAlignment(SwingConstants.CENTER); // Centra il testo dell'etichetta
		//previewRequirements.setForeground(new Color(34, 139, 34)); // Verde scuro
		previewRequirements.setFont(previewRequirements.getFont().deriveFont(Font.BOLD)); // Imposta l'etichetta
		// Add a right margin to previewRequirements
		previewRequirements.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 25));// margin right
		// Imposta la dimensione preferita a 24 pixel di altezza
		previewRequirements.setPreferredSize(new Dimension(previewRequirements.getPreferredSize().width, 24));


		printReqFound(diagram); // Call method to update the text area content
	}
	
	public void printReqFound(IDiagramUIModel diagram) {
		if (diagram != null) {
			String diagramId= diagram.getId();
			Path path = LogStreamer.getRequirementsDirectory();
			if (FileUtilities.doesFileExist(diagramId, path))
				try {
					requirementsTextArea.setText(FileUtilities.loadFileContent(diagramId, path));
	                // Forza il ricalcolo del layout e il ridisegno del componente
	                requirementsTextArea.revalidate();
	                requirementsTextArea.repaint();
				} catch (IOException e) {
					ErrorUtils.showDetailedErrorMessage(e);
					requirementsTextArea.setText("Requirements NOT found");
				}
			else
				requirementsTextArea.setText("Requirements NOT found");
		}
		else
			requirementsTextArea.setText("Requirements NOT found");
	}
	
	public JLabel getPreviewRequirements() {
		return previewRequirements;
	}
	
	public JTextArea getRequirementsTextArea() {
		return requirementsTextArea;
	}
	
	/**
	 * Add requirementsTextArea to the east region in a JScrollPane
	 * @param rightPanel
	 * @return 
	 */
	public JPanel addRTextArea(JPanel rightPanel) {
	    JScrollPane requirementsScrollPane = new JScrollPane(requirementsTextArea);
	    requirementsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Set vertical scroll policy
	    requirementsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Set horizontal scroll policy
	    rightPanel.add(requirementsScrollPane, BorderLayout.CENTER); // Aggiungi l'area di testo al centro del pannello
	    return rightPanel;
	}
	
	public void exportTextToFile() {
	    // Create a JFileChooser to allow the user to choose the file location
	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setDialogTitle("Export Requirements As");
	    fileChooser.setSelectedFile(new File("requirements.txt")); // Default file name

	    int userSelection = fileChooser.showSaveDialog(null);

	    // If the user selects a file
	    if (userSelection == JFileChooser.APPROVE_OPTION) {
	        File fileToSave = fileChooser.getSelectedFile();

	        try (FileWriter fileWriter = new FileWriter(fileToSave)) {
	            // Write the text from the JTextArea to the file
	            fileWriter.write(requirementsTextArea.getText());
	            JOptionPane.showMessageDialog(null, "Requirements exported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
	        } catch (IOException e) {
	            JOptionPane.showMessageDialog(null, "Error exporting file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}
}
