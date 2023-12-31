package com.uniba.mining.dialogs;

import javax.swing.*;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.uniba.mining.tasks.repoviolations.ViolationMessageGenerator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.io.IOException;

public class ViolationsRepoHandler extends JFrame {

	private static final long serialVersionUID = 1L;
	private boolean isCSVProcessed = false; // Variabile di stato per tenere traccia del file CSV processato
	private JButton saveButton;  // Dichiarazione della variabile saveButton come variabile di istanza

	public ViolationsRepoHandler() {
		initComponents();
		setSize(400, 400);  // Imposta la dimensione predefinita della finestra
	}

	private void initComponents() {
		JButton browseButton = new JButton("Browse CSV");
		JTextArea resultTextArea = new JTextArea();
		saveButton = new JButton("Save");
		updateSaveButtonState();
		resultTextArea.setEditable(false);  // Imposta la JTextArea come di sola lettura
		JScrollPane scrollPane = new JScrollPane(resultTextArea);

		// listener per il pulsante browse in modo da rendere possibile solo la sezione di file CSV
		browseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();

				// Aggiungi un filtro per i file CSV
				FileFilter filter = new FileNameExtensionFilter("File CSV (*.csv)", "csv");
				fileChooser.setFileFilter(filter);

				int result = fileChooser.showOpenDialog(ViolationsRepoHandler.this);

				if (result == JFileChooser.APPROVE_OPTION) {
					String filePath = fileChooser.getSelectedFile().getAbsolutePath();
					isCSVProcessed = ViolationMessageGenerator.processCSV(filePath, resultTextArea);
					updateSaveButtonState();
					if (!isCSVProcessed) {
						// Visualizza la finestra di dialogo nel caso in cui il file CSV non sia conforme
						JOptionPane.showMessageDialog(ViolationsRepoHandler.this,
								"The selected CSV file is not compliant.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});


//		browseButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				JFileChooser fileChooser = new JFileChooser();
//				int result = fileChooser.showOpenDialog(ViolationsRepoHandler.this);
//
//				if (result == JFileChooser.APPROVE_OPTION) {
//					String filePath = fileChooser.getSelectedFile().getAbsolutePath();
//					isCSVProcessed= ViolationMessageGenerator.processCSV(filePath, resultTextArea);
//					updateSaveButtonState();
//				}
//			}
//		});


		// Aggiungi un ActionListener per il pulsante Salva
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveToFile(resultTextArea);
			}
		});

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(browseButton);
		getContentPane().add(scrollPane);
		getContentPane().add(saveButton); // Aggiungi il pulsante Salva

		setTitle("Violations Report");

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// Personalizza il comportamento di chiusura della finestra
				handleWindowClosing();
			}
		});
		pack();
		setLocationRelativeTo(null);
	}

	private void handleWindowClosing() {
		// stub method

	}


	private void saveToFile(JTextArea resultTextArea) {
		JFileChooser fileChooser = new JFileChooser();
		int result = fileChooser.showSaveDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) {
			try {
				String filePath = fileChooser.getSelectedFile().getAbsolutePath();
				// Aggiungi l'estensione .txt se non è già presente
				if (!filePath.toLowerCase().endsWith(".txt")) {
					filePath += ".txt";
				}

				// Scrivi il contenuto della JTextArea sul file
				FileWriter writer = new FileWriter(filePath);
				writer.write(resultTextArea.getText());
				writer.close();

				JOptionPane.showMessageDialog(this, "Report successfully saved!", "Save", JOptionPane.INFORMATION_MESSAGE);
				// Chiudi la finestra corrente dopo aver salvato
				dispose();
			} catch (IOException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Error during file saving!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	} 


	private void updateSaveButtonState() {
		saveButton.setEnabled(isCSVProcessed);
	}



	private String generateMessage(String violationType, String... fields) {
		StringBuilder message = new StringBuilder("Tipo di Violazione: " + violationType + "\n");

		// Aggiungi tutte le informazioni esistenti
		for (String field : fields) {
			message.append(field);
			message.append("\n");
		}

		// Aggiungi messaggio specifico per il tipo di violazione
		message.append(ViolationMessageGenerator.generateMessage(violationType, fields));

		return message.toString();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ViolationsRepoHandler().setVisible(true);
			}
		});
	}
}
