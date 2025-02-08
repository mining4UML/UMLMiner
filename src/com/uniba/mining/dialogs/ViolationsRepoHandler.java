package com.uniba.mining.dialogs;

import javax.swing.*;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.uniba.mining.plugin.Config;
import com.uniba.mining.tasks.repoviolations.ViolationMessageGenerator;
import com.uniba.mining.utils.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.io.IOException;

//public class ViolationsRepoHandler extends JFrame {
//
//	private static final long serialVersionUID = 1L;
//	private boolean isCSVProcessed = false; // Variabile di stato per tenere traccia del file CSV processato
//	private JButton saveButton;  // Dichiarazione della variabile saveButton come variabile di istanza
//
//	public ViolationsRepoHandler() {
//		initComponents();
//		setSize(400, 400);  // Imposta la dimensione predefinita della finestra
//	}
//
//	private void initComponents() {
//		JButton browseButton = new JButton("Browse CSV");
//		JTextArea resultTextArea = new JTextArea();
//		saveButton = new JButton("Save");
//		updateSaveButtonState();
//		resultTextArea.setEditable(false);  // Imposta la JTextArea come di sola lettura
//		JScrollPane scrollPane = new JScrollPane(resultTextArea);
//
//		// listener per il pulsante browse in modo da rendere possibile solo la sezione di file CSV
//		browseButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				JFileChooser fileChooser = new JFileChooser();
//
//				// Aggiungi un filtro per i file CSV
//				FileFilter filter = new FileNameExtensionFilter("File CSV (*.csv)", "csv");
//				fileChooser.setFileFilter(filter);
//
//				int result = fileChooser.showOpenDialog(ViolationsRepoHandler.this);
//
//				if (result == JFileChooser.APPROVE_OPTION) {
//					String filePath = fileChooser.getSelectedFile().getAbsolutePath();
//					isCSVProcessed = ViolationMessageGenerator.processCSV(filePath, resultTextArea);
//					updateSaveButtonState();
//					if (!isCSVProcessed) {
//						// Visualizza la finestra di dialogo nel caso in cui il file CSV non sia conforme
//						GUI.showErrorMessageDialog(ViolationsRepoHandler.this, 
//								Config.EXPORT_VIOLATIONS_ACTION, Config.EXPORT_VIOLATIONS_INPUT_ERROR);
//						// Visualizza la finestra di dialogo nel caso in cui il file CSV non sia conforme
//						//JOptionPane.showMessageDialog(ViolationsRepoHandler.this,
//						//		"The selected CSV file is not compliant.", "Error", JOptionPane.ERROR_MESSAGE);
//					}
//				}
//			}
//		});
//
//
//		// Aggiungi un ActionListener per il pulsante Salva
//		saveButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				saveToFile(resultTextArea);
//			}
//		});
//
//		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
//		getContentPane().add(browseButton);
//		getContentPane().add(scrollPane);
//		getContentPane().add(saveButton); // Aggiungi il pulsante Salva
//		String title =Config.PLUGIN_NAME+Config.PLUGIN_WINDOWS_SEPARATOR+Config.EXPORT_VIOLATIONS_ACTION;
//
//		setTitle(title);
//
//		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosing(WindowEvent e) {
//				// Personalizza il comportamento di chiusura della finestra
//				handleWindowClosing();
//			}
//		});
//		pack();
//		setLocationRelativeTo(null);
//	}
//
//	private void handleWindowClosing() {
//		// stub method
//
//	}
//
//
//	private void saveToFile(JTextArea resultTextArea) {
//		JFileChooser fileChooser = new JFileChooser();
//		int result = fileChooser.showSaveDialog(this);
//
//		if (result == JFileChooser.APPROVE_OPTION) {
//			try {
//				String filePath = fileChooser.getSelectedFile().getAbsolutePath();
//				// Aggiungi l'estensione .txt se non è già presente
//				if (!filePath.toLowerCase().endsWith(".txt")) {
//					filePath += ".txt";
//				}
//
//				// Scrivi il contenuto della JTextArea sul file
//				FileWriter writer = new FileWriter(filePath);
//				writer.write(resultTextArea.getText());
//				writer.close();
//
//				GUI.showInformationMessageDialog(this, Config.EXPORT_INFO_ACTION, 
//						Config.EXPORT_VIOLATIONS_OK);
//
//				//JOptionPane.showMessageDialog(this, "Report successfully saved!", "Save", JOptionPane.INFORMATION_MESSAGE);
//				// Chiudi la finestra corrente dopo aver salvato
//				dispose();
//			} catch (IOException ex) {
//				ex.printStackTrace();
//				JOptionPane.showMessageDialog(this, "Error during file saving!", "Error", JOptionPane.ERROR_MESSAGE);
//			}
//		}
//	} 
//
//
//	private void updateSaveButtonState() {
//		saveButton.setEnabled(isCSVProcessed);
//	}
//
//
//
//	private String generateMessage(String violationType, String... fields) {
//		StringBuilder message = new StringBuilder("Tipo di Violazione: " + violationType + "\n");
//
//		// Aggiungi tutte le informazioni esistenti
//		for (String field : fields) {
//			message.append(field);
//			message.append("\n");
//		}
//
//		// Aggiungi messaggio specifico per il tipo di violazione
//		message.append(ViolationMessageGenerator.generateMessage(violationType, fields));
//
//		return message.toString();
//	}
//}

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViolationsRepoHandler extends JFrame {

    private static final long serialVersionUID = 1L;
    private JButton processButton;
    private JList<String> fileList;
    private DefaultListModel<String> listModel;
    private JCheckBox selectAllCheckBox;
    private File selectedDirectory;
    private List<File> csvFiles;

    public ViolationsRepoHandler() {
        initComponents();
        setSize(500, 500);
    }

    private void initComponents() {
        JButton browseButton = new JButton("Select Folder");
        processButton = new JButton("Process Selected Files");
        processButton.setEnabled(false);
        selectAllCheckBox = new JCheckBox("Select/Deselect All");

        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        fileList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(fileList);

        // Listener per selezionare una cartella
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser dirChooser = new JFileChooser();
                dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int result = dirChooser.showOpenDialog(ViolationsRepoHandler.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedDirectory = dirChooser.getSelectedFile();
                    loadCSVFiles();
                }
            }
        });
        
     // Aggiungere un listener per il cambiamento di selezione nella JList
        fileList.addListSelectionListener(e -> {
            // Abilitare il pulsante solo se almeno un file è selezionato
            processButton.setEnabled(!fileList.isSelectionEmpty());
        });

        // Checkbox per selezionare/deselezionare tutti i file
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

        // Pulsante per elaborare e salvare i file selezionati
        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processSelectedFiles();
            }
        });

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(browseButton);
        getContentPane().add(scrollPane);
        getContentPane().add(selectAllCheckBox);
        getContentPane().add(processButton);

        setTitle("CSV Processor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void loadCSVFiles() {
        listModel.clear();
        csvFiles = new ArrayList<>();

        File[] files = selectedDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (files != null) {
            for (File file : files) {
                csvFiles.add(file);
                listModel.addElement(file.getName());
            }
        }

        if (csvFiles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No CSV files found in the selected folder.", "Warning", JOptionPane.WARNING_MESSAGE);
            processButton.setEnabled(false);
            selectAllCheckBox.setEnabled(false);
        } else {
            processButton.setEnabled(true);
            selectAllCheckBox.setEnabled(true);
        }
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
                File fileToProcess = new File(selectedDirectory, fileName); // File di input
                File processedFile = ViolationMessageGenerator.processCSV(fileToProcess); // Elabora il file

                if (processedFile == null) {
                    GUI.showErrorMessageDialog(this, "Processing Error", "File " + fileName + " is not compliant.");
                    continue;
                }

                // Rinominare e salvare il file elaborato nella cartella selezionata
                File outputFile = new File(saveDirectory, "FINAL-" + fileName);
                if (!processedFile.renameTo(outputFile)) {
                    JOptionPane.showMessageDialog(this, "Error saving file: " + fileName, "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
            }

            JOptionPane.showMessageDialog(this, "Selected files processed and saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
            // Disabilitare il pulsante dopo l'elaborazione
            processButton.setEnabled(false);
            
            // Deselezionare tutti i file
            fileList.clearSelection();
            
            // Rimuovere il segno di spunta dalla checkbox
            selectAllCheckBox.setSelected(false);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViolationsRepoHandler().setVisible(true));
    }
}


