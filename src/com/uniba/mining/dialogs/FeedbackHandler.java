package com.uniba.mining.dialogs;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.*;

//import com.google.api.client.util.ClassInfo;
import com.uniba.mining.feedback.Conversation;
import com.uniba.mining.utils.Application;
import com.uniba.mining.utils.GUI;
import com.uniba.mining.tasks.exportdiag.ClassInfo;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import com.uniba.mining.feedback.ConversationListCellRenderer;
import com.uniba.mining.feedback.ConversationsSerializer;
import com.uniba.mining.llm.ApiRequest;
import com.uniba.mining.llm.ApiResponse;
import com.uniba.mining.llm.RestClient;
import com.uniba.mining.plugin.Config;

import java.util.ArrayList;
import java.util.List;

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
	private String projectId;
	// Contatore per tenere traccia del numero totale di conversazioni
	private int conversationCounter = 0;
	// dialogs.feedback.placeholder in plugin.properties
	private static final String DIALOG_FEEDBACK_MESSAGE = "Type your question here...";

	private static FeedbackHandler instance;

	private static JPanel panel;

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public void clearPanel() {
		conversationListModel.removeAllElements();
		conversationList.clearSelection();
	}

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

		// Modifica il listener dell'inputField
		inputField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!inputField.getText().isEmpty()) {
					// Ottieni il session id dalla conversazione selezionata
					Conversation selectedConversation = conversationList.getSelectedValue();
					String sessionId = selectedConversation != null ? selectedConversation.getSessionId()
							: generateSessionId();

					// Chiama processUserInput() passando session id
					processUserInput(sessionId);
				}
			}
		});

		newChatButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createNewChat();
			}
		});

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

	private void processUserInput(String sessionId) {
		// Ottieni il testo dall'inputField
		String inputText = inputField.getText();
		String you = "You: " + inputText;

		String diagramAsText = ClassInfo.exportInformation(Application.getProject());
		String message = sendRequestAndGetResponse(inputText, diagramAsText, sessionId);
		appendToPane(you);
		appendToPane(message);
		updateConversation(inputText, message, sessionId);
		inputField.setText("");
	}

	private String sendRequestAndGetResponse(String inputText, String diagramAsText, String session_id) {
		try {
			// Creazione di un oggetto ApiRequest con i dati appropriati
			ApiRequest request = new ApiRequest(session_id, projectId, "q1", diagramAsText, inputText);
			// Creazione di un oggetto RestClient
			RestClient client = new RestClient();

			// Invio della richiesta al server e ottenimento della risposta
			ApiResponse response = client.sendRequest(request);

			// Restituisci il messaggio della risposta
			return response.getAnswer();
		} catch (IOException ex) {
			ex.printStackTrace();
			return "Error: Unable to get response";
		}
	}

	private void updateConversation(String inputText, String message, String sessionId) {
		if (!inputText.isEmpty()) {
			// Aggiungi il testo alla conversazione corrente solo se non è vuoto
			if (conversationListModel.isEmpty()) {
				Conversation newConversation = createNewConversation(sessionId);
				conversationListModel.addElement(newConversation);
				conversationList.setSelectedValue(newConversation, true);
			}

			Conversation currentConversation = conversationList.getSelectedValue();
			if (currentConversation != null) {
				currentConversation.appendMessage("You: " + inputText + "\n" + message);
				conversationListModel.set(conversationList.getSelectedIndex(), currentConversation);
				conversationTitleField.setText(currentConversation.getTitle());
			}

			serializeConversations();

			conversationList.revalidate();
			conversationList.repaint();
		}
	}

	private String generateSessionId() {
		// Incrementa il contatore delle conversazioni di 1
		conversationCounter++;
		// Restituisci l'ID della sessione corrispondente al numero totale di
		// conversazioni
		return String.valueOf(conversationCounter);
	}

	private void serializeConversations() {
		List<Conversation> conversations = new ArrayList<>();
		for (int i = 0; i < conversationListModel.size(); i++) {
			conversations.add(conversationListModel.getElementAt(i));
		}
		// Serializza l'intera lista di conversazioni
		ConversationsSerializer.serializeConversations(conversations, projectId);
	}

	private Conversation createNewConversation(String sessionId) {

		// Genera il query_id basato sul numero di conversazioni attuali
		String query_id = "q1";
		String diagramAsText = ClassInfo.exportInformation(Application.getProject());
		String query = "...";

		// Utilizzo del costruttore con i parametri
		Conversation newConversation = new Conversation(sessionId, projectId, query_id, diagramAsText, query);
		newConversation.appendMessage(outputPane.getText());
		System.out.println(newConversation.toString());
		return newConversation;
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

			// Se l'utente conferma l'eliminazione, procedi con la cancellazione
			if (choice == JOptionPane.YES_OPTION) {
				conversationListModel.remove(selectedIndex); // Rimuovi la conversazione dal modello dei dati
				serializeConversations();

				// Seleziona un'altra conversazione dopo l'eliminazione
				int conversationCount = conversationListModel.getSize();
				if (conversationCount > 0) {
					// Seleziona la prima conversazione dopo l'eliminazione
					conversationList.setSelectedIndex(0);
				}

				conversationList.revalidate();
				conversationList.repaint();
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
				serializeConversations();
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
		projectId = Application.getProject().getId();

		if (panel == null)
			createPanel();
		else {
			// after opened
			clearPanel();
			loadSerializedConversations();
		}
		Application.getViewManager().showMessagePaneComponent(id, title, panel);
	}

	private void createNewChat() {
		// Ottieni il testo dalla JTextPane
		String query = outputPane.getText();

		// Controlla se l'inputText non è vuoto o uguale al messaggio di feedback
		// predefinito
		if (!query.isEmpty() && !query.equals(Config.DIALOG_FEEDBACK_MESSAGE)) {
			// i valori sessionId, projectId, etc.
			String sessionId = generateSessionId(); // Esempio, dovrai ottenere questi dati
			// Genera il query_id basato sul numero di conversazioni attuali
			String queryId = "q1";
			String diagramAsText = ClassInfo.exportInformation(Application.getProject());
			// Crea una nuova istanza di Conversation
			Conversation newConversation = new Conversation(sessionId, projectId, queryId, diagramAsText, query);

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

		projectId = Application.getProject().getId();
		// Carica le conversazioni serializzate durante l'inizializzazione del pannello
		loadSerializedConversations();

		// cattura l'evento di selezione di una conversazione nella list
		conversationList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				Conversation selectedConversation = conversationList.getSelectedValue();

				// Imposta il titolo della conversazione nel conversationTitleField
				if (selectedConversation != null) {
					conversationTitleField.setText(selectedConversation.getTitle()); // Aggiorna il titolo
					// Ottieni il testo della conversazione selezionata
					String conversationContent = selectedConversation.getConversationContent();
					// Rimuovi il testo precedente dall'outputPane
					outputPane.setText("");
					// Dividi il testo in righe
					String[] lines = conversationContent.split("\n");
					// Applica il colore appropriato a ciascuna riga
					for (String line : lines) {
						Color textColor = line.startsWith("You:") ? Color.BLUE : Color.BLACK;
						appendToPane(line + "\n", textColor);
					}
				} else {
					conversationTitleField.setText(""); // Pulisci il titolo se non c'è una selezione
					outputPane.setText(""); // Pulisci l'outputPane se non c'è una selezione
				}
			}
		});

		return panel;
	}

	public void loadSerializedConversations() {
		if (projectId != null) {
			List<Conversation> serializedConversations = ConversationsSerializer.deserializeConversations(projectId);
			if (serializedConversations != null) {
				int maxSessionId = 0; // Inizializza il valore massimo dell'ID della sessione
				for (Conversation conversation : serializedConversations) {
					conversationListModel.addElement(conversation);
					int sessionId = Integer.parseInt(conversation.getSessionId());
					// Trova il valore massimo dell'ID della sessione
					if (sessionId > maxSessionId) {
						maxSessionId = sessionId;
					}
					System.out.println("*****-" + conversation.getSessionId() + "*****-");
				}
				// Imposta il contatore delle conversazioni sul valore massimo trovato
				conversationCounter = maxSessionId;
			}
		}
	}

	private void appendToPane(String text) {
		// Verifica se il testo inizia con "You:" e termina con "\n"
		if (text.startsWith("You:")) {
			// Se sì, imposta il colore del testo su BLUE
			appendToPane(text, Color.BLUE);
		} else {
			// Altrimenti, imposta il colore del testo su nero
			appendToPane(text, Color.BLACK);
		}
	}

	private void appendToPane(String text, Color color) {
		Style style = document.addStyle("Style", null);
		StyleConstants.setForeground(style, color);
		try {
			// Controlla se è necessario aggiungere una nuova linea prima del testo
			if (document.getLength() > 0) { // verifica che ci sia del testo nel documento
				String lastChar = document.getText(document.getLength() - 1, 1);
				if (!lastChar.equals("\n")) { // verifica che l'ultimo carattere non sia una nuova linea
					text = "\n" + text; // aggiungi una nuova linea prima del testo
				}
			}
			document.insertString(document.getLength(), text, style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}