package com.uniba.mining.dialogs;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.*;

import com.uniba.mining.feedback.Conversation;
import com.uniba.mining.utils.Application;
import com.uniba.mining.utils.GUI;

import java.awt.*;
import java.awt.event.*;

import com.uniba.mining.feedback.ConversationListCellRenderer;
import com.uniba.mining.plugin.Config;

public class FeedbackHandler {
	private static final String id = "feedbackPanel";
	private static final String title = "UML Miner - Feedback";
	private JTextField inputField;
	private JTextPane outputPane;
	private StyledDocument document;
	private DefaultListModel<Conversation> conversationListModel;
	private JList<Conversation> conversationList;
	private JButton newChatButton;
	private JTextField conversationTitleField;
	// dialogs.feedback.placeholder in plugin.properties
	private static final String DIALOG_FEEDBACK_MESSAGE = "Type your question here...";

	private static FeedbackHandler instance;

	private static JPanel panel;

	private FeedbackHandler() {
		inputField = new JTextField(DIALOG_FEEDBACK_MESSAGE);

		outputPane = new JTextPane();
		outputPane.setEditable(false);
		outputPane.setPreferredSize(new Dimension(400, 200));
		document = outputPane.getStyledDocument();

		conversationListModel = new DefaultListModel<>();
		conversationList = new JList<>(conversationListModel);

		conversationTitleField = new JTextField();
		conversationTitleField.setEditable(false);
		// Imposta il colore di sfondo della casella di testo a grigio chiaro
		conversationTitleField.setBackground(Color.LIGHT_GRAY);

		newChatButton = new JButton("New Chat");

		conversationList.setCellRenderer(new ConversationListCellRenderer());

		inputField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (inputField.getText().equals(Config.DIALOG_FEEDBACK_MESSAGE)) {
					inputField.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (inputField.getText().isEmpty()) {
					inputField.setText(Config.DIALOG_FEEDBACK_MESSAGE);
				}
			}
		});

		inputField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				boolean conversationEmpty = false;
				// Ottieni il testo dall'inputField
				String inputText = inputField.getText();

				String you = "You: " + inputText + "\n";
				String message = "Message: " + inputText.toUpperCase() + "\n";

				// Aggiungi il testo alla JTextPane formattandolo come "You" e "Message"
				appendToPane(you, Color.BLUE);
				appendToPane(message, Color.BLACK);

				// Aggiungi il testo alla conversazione corrente solo se non è vuoto
				if (!inputText.isEmpty()) {
					if (conversationListModel.isEmpty()) { // Controlla se la lista delle conversazioni è vuota
						conversationEmpty = true;
						// Crea una nuova istanza di Conversation
						Conversation newConversation = new Conversation();
						newConversation.appendMessage(outputPane.getText());
						// Aggiungi la nuova istanza di Conversation alla lista delle conversazioni
						conversationListModel.addElement(newConversation);
						// Imposta la nuova istanza di Conversation come selezionata nella lista delle
						// conversazioni
						conversationList.setSelectedValue(newConversation, true);
					}

					// Seleziona la conversazione corrente dalla lista delle conversazioni
					Conversation currentConversation = conversationList.getSelectedValue();

					// Aggiungi il testo all'attuale conversazione
					if (currentConversation != null) {
						if (!conversationEmpty)
							currentConversation.appendMessage(you + message);

						// Aggiorna il modello della lista delle conversazioni per riflettere le
						// modifiche
						conversationListModel.set(conversationList.getSelectedIndex(), currentConversation);

						// aggiorna il titolo della casella del titolo
						conversationTitleField.setText(currentConversation.getTitle());

					}

					// Aggiorna la visualizzazione della GUI
					conversationList.revalidate();
					conversationList.repaint();

					// Azzera il campo di input
					inputField.setText("");
				}
			}
		});

		newChatButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createNewChat();
				/*
				 * String inputText = inputField.getText(); if
				 * (!inputText.equals(DIALOG_FEEDBACK_MESSAGE)) { Conversation newConversation =
				 * new Conversation(); newConversation.appendMessage(inputText);
				 * conversationListModel.addElement(newConversation);
				 * inputField.setText(DIALOG_FEEDBACK_MESSAGE); outputPane.setText(""); //
				 * Azzera il contenuto della JTextPane }
				 */
			}
		});

		// mouse listener per gestire il click destro sull'elemento della lista
		/*
		 * conversationList.addMouseListener(new MouseAdapter() {
		 * 
		 * @Override public void mouseReleased(MouseEvent e) { if
		 * (SwingUtilities.isRightMouseButton(e)) { int index =
		 * conversationList.locationToIndex(e.getPoint());
		 * conversationList.setSelectedIndex(index); showPopupMenu(e); } } });
		 */

		conversationList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					int index = conversationList.locationToIndex(e.getPoint());
					if (index > -1) {
						conversationList.setSelectedIndex(index);
						showPopupMenu(e);
					}
				}
			}
		});

		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem copyItem = new JMenuItem("Copy");
		// JMenuItem clearItem = new JMenuItem("Clear");
		copyItem.addActionListener(e -> outputPane.copy());
		// clearItem.addActionListener(e -> outputPane.setText(""));
		popupMenu.add(copyItem);
		// popupMenu.add(clearItem);
		outputPane.setComponentPopupMenu(popupMenu);
	}

	// Metodo per visualizzare il menu a comparsa
	private void showPopupMenu(MouseEvent e) {
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem renameItem = new JMenuItem("Rename");
		JMenuItem deleteItem = new JMenuItem("Delete");
		renameItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				renameConversation();
			}
		});
		deleteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteConversation();
			}
		});
		popupMenu.add(renameItem);
		popupMenu.add(deleteItem);
		popupMenu.show(conversationList, e.getX(), e.getY());
	}

	// Metodo per eliminare la conversazione selezionata
	private void deleteConversation() {
		int selectedIndex = conversationList.getSelectedIndex();
		if (selectedIndex != -1) { // Verifica se è stata selezionata una conversazione
			// Conversation selectedConversation = conversationList.getSelectedValue();

			int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the conversation?",
					"Delete Conversation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, GUI.getImageIcon());

			// Mostra una finestra di dialogo di conferma
			/*
			 * int choice = Application.getViewManager().showConfirmDialog(null,
			 * "Are you sure you want to delete the conversation?", "Delete Conversation",
			 * JOptionPane.YES_NO_OPTION);
			 */

			// Se l'utente conferma l'eliminazione, procedi con la cancellazione
			if (choice == JOptionPane.YES_OPTION) {
				conversationListModel.remove(selectedIndex); // Rimuovi la conversazione dal modello dei dati
			}

		}
	}

	private void renameConversation() {
		Conversation selectedConversation = conversationList.getSelectedValue();
		if (selectedConversation != null) {

			// Mostra una finestra di dialogo di input con un'icona personalizzata
			String newTitle = (String) JOptionPane.showInputDialog(null, "Enter new conversation title:",
					"Conversation title", JOptionPane.PLAIN_MESSAGE, GUI.getImageIcon(), null, null);

			if (newTitle != null && !newTitle.isEmpty()) {
				selectedConversation.setTitle(newTitle);
				// Aggiorna il testo nella casella di testo del titolo della conversazione
				conversationTitleField.setText(newTitle);
				// Aggiorna la visualizzazione della lista delle conversazioni
				conversationListModel.setElementAt(selectedConversation, conversationList.getSelectedIndex());
			}
		}
	}

	public static FeedbackHandler getInstance() {
		if (instance == null) {
			instance = new FeedbackHandler();
		}
		return instance;
	}

	public void showFeedbackPanel() {

		if (panel == null)
			createPanel();
		Application.getViewManager().showMessagePaneComponent(id, title, panel);
	}

	private void createNewChat() {
		// Ottieni il testo dalla JTextPane
		String inputText = outputPane.getText();

		// Controlla se l'inputText non è vuoto o uguale al messaggio di feedback
		// predefinito
		if (!inputText.isEmpty() && !inputText.equals(Config.DIALOG_FEEDBACK_MESSAGE)) {
			// Crea una nuova istanza di Conversation
			Conversation newConversation = new Conversation();

			// Aggiungi la nuova istanza di Conversation alla lista delle conversazioni
			conversationListModel.addElement(newConversation);

			// Aggiorna la visualizzazione della GUI
			conversationList.revalidate();
			conversationList.repaint();

			// Imposta la nuova istanza di Conversation come selezionata nella lista delle
			// conversazioni
			conversationList.setSelectedValue(newConversation, true);
			System.out.println(
					"\n\ndimensione della lista delle conversazioni:" + conversationList.getModel().getSize() + "\n\n");

			// Azzera il contenuto della JTextPane
			outputPane.setText("");

		}
	}

	private JPanel createPanel() {
		panel = new JPanel(new BorderLayout());

		// Pannello principale diviso in due parti: sinistra e destra
		JPanel mainPanel = new JPanel(new BorderLayout());

		// Pannello sinistro contiene il pulsante "New Chat" e la lista delle
		// conversazioni
		JPanel leftPanel = new JPanel(new BorderLayout());
		newChatButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createNewChat();
			}
		});
		leftPanel.add(newChatButton, BorderLayout.NORTH);
		JScrollPane listScrollPane = new JScrollPane(conversationList);
		listScrollPane.setPreferredSize(new Dimension(200, 200)); // Imposta le dimensioni desiderate
		leftPanel.add(listScrollPane, BorderLayout.CENTER);

		mainPanel.add(leftPanel, BorderLayout.WEST);

		// Pannello destro contiene il conversationTitleField, l'outputPane e
		// l'inputField
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(conversationTitleField, BorderLayout.NORTH); // Aggiungi questa linea
		rightPanel.add(new JScrollPane(outputPane), BorderLayout.CENTER);
		rightPanel.add(inputField, BorderLayout.SOUTH);

		mainPanel.add(rightPanel, BorderLayout.CENTER);

		panel.add(mainPanel, BorderLayout.CENTER);

		// Aggiungi il listener per conversationList
		conversationList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				Conversation selectedConversation = conversationList.getSelectedValue();

				// Imposta il titolo della conversazione nel conversationTitleField
				if (selectedConversation != null) {
					conversationTitleField.setText(selectedConversation.getTitle()); // Aggiorna il titolo
					System.out.println(selectedConversation.getConversationContent());
					outputPane.setText(selectedConversation.getConversationContent());
				} else {
					conversationTitleField.setText(""); // Pulisci il titolo se non c'è una selezione
					outputPane.setText("");
				}
			}
		});

		return panel;
	}

	private void appendToPane(String text, Color color) {
		Style style = document.addStyle("Style", null);
		StyleConstants.setForeground(style, color);
		try {
			document.insertString(document.getLength(), text, style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}