package com.uniba.mining.dialogs;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.*;

import com.uniba.mining.feedback.Conversation;
import com.uniba.mining.utils.Application;

import java.awt.*;
import java.awt.event.*;

import com.uniba.mining.feedback.ConversationListCellRenderer;

public class FeedbackHandler {
	private final static String DIALOG_FEEDBACK_MESSAGE = "Message here...";
	private JTextField inputField;
	private JTextPane outputPane;
	private StyledDocument document;
	private DefaultListModel<Conversation> conversationListModel;
	private JList<Conversation> conversationList;
	private JButton newChatButton; // Dichiarare il pulsante come campo della classe

	private static FeedbackHandler instance;

	private FeedbackHandler() {
		inputField = new JTextField(DIALOG_FEEDBACK_MESSAGE);
		outputPane = new JTextPane();
		outputPane.setEditable(false);
		outputPane.setPreferredSize(new Dimension(400, 200));
		document = outputPane.getStyledDocument();
		conversationListModel = new DefaultListModel<>();
		conversationList = new JList<>(conversationListModel);
		newChatButton = new JButton("New Chat"); // Inizializzare il pulsante

		conversationList.setCellRenderer(new ConversationListCellRenderer());

		inputField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (inputField.getText().equals(DIALOG_FEEDBACK_MESSAGE)) {
					inputField.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (inputField.getText().isEmpty()) {
					inputField.setText(DIALOG_FEEDBACK_MESSAGE);
				}
			}
		});

		/*
		 * inputField.addActionListener(e -> { String inputText = inputField.getText();
		 * appendToPane("You: " + inputText + "\n", Color.BLUE);
		 * appendToPane("Message: " + inputText.toUpperCase() + "\n", Color.BLACK);
		 * inputField.setText(""); });
		 */

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
		conversationList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					int index = conversationList.locationToIndex(e.getPoint());
					conversationList.setSelectedIndex(index);
					showPopupMenu(e);
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
	        Conversation selectedConversation = conversationList.getSelectedValue();
	        conversationListModel.remove(selectedIndex); // Rimuovi la conversazione dal modello dei dati
	    }
	}
	
	// Metodo per rinominare la conversazione
	private void renameConversation() {
		Conversation selectedConversation = conversationList.getSelectedValue();
		if (selectedConversation != null) {
			String newTitle = JOptionPane.showInputDialog(null, "Enter new conversation title:", "Conversation title",
					JOptionPane.PLAIN_MESSAGE);
			if (newTitle != null && !newTitle.isEmpty()) {
				selectedConversation.setTitle(newTitle);
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
		String id = "customPanel";
		String title = "UML Miner - Feedback";
		Application.getViewManager().showMessagePaneComponent(id, title, createPanel());
	}

	private void createNewChat() {
		// Ottieni il testo dalla JTextPane
		String inputText = outputPane.getText();

		// Controlla se l'inputText non è vuoto o uguale al messaggio di feedback
		// predefinito
		if (!inputText.isEmpty() && !inputText.equals(DIALOG_FEEDBACK_MESSAGE)) {
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
		JPanel panel = new JPanel(new BorderLayout());

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

		// Pannello destro contiene l'outputPane e l'inputField
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(new JScrollPane(outputPane), BorderLayout.CENTER);
		rightPanel.add(inputField, BorderLayout.SOUTH);

		mainPanel.add(rightPanel, BorderLayout.CENTER);

		panel.add(mainPanel, BorderLayout.CENTER);

		// Aggiungi il listener per conversationList
		conversationList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				Conversation selectedConversation = conversationList.getSelectedValue();

				// Visualizza il testo della Conversation nella outputPane
				if (selectedConversation != null) {
					System.out.println(selectedConversation.getConversationContent());
					outputPane.setText(selectedConversation.getConversationContent());
				} else {
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