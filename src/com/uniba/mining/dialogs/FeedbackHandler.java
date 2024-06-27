package com.uniba.mining.dialogs;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.*;

import com.uniba.mining.utils.Application;
import com.uniba.mining.utils.GUI;
import com.vp.plugin.model.IProject;
import com.uniba.mining.tasks.exportdiag.ClassInfo;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.ConnectException;

import com.uniba.mining.feedback.Conversation;
import com.uniba.mining.feedback.ConversationListCellRenderer;
import com.uniba.mining.feedback.ConversationsSerializer;
import com.uniba.mining.llm.ApiRequest;
import com.uniba.mining.llm.ApiResponse;
import com.uniba.mining.llm.RestClient;
import com.uniba.mining.plugin.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class FeedbackHandler {
	private static final String id = "feedbackPanel";
	private static final String prefixAnswer = "You: ";
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
		inputField.setBackground(Color.WHITE);
		inputField.setBorder(BorderFactory.createLineBorder(Color.BLUE));

		outputPane = new JTextPane();
		outputPane.setEditable(false);
		outputPane.setPreferredSize(new Dimension(400, 200));
		document = outputPane.getStyledDocument();

		conversationListModel = new DefaultListModel<>();
		conversationList = new JList<>(conversationListModel);

		conversationTitleField = new JTextField();
		conversationTitleField.setEditable(false);
		// Imposta il colore di sfondo della casella di testo a quello del panel
		conversationTitleField.setBackground(UIManager.getColor("Panel.background"));

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
					try {
						// If the project is empty, an exception is thrown with a message indicating the absence of diagrams
						ClassInfo.isProjectEmpty(Application.getProject());
						System.out.println(Application.getProject().getName());
						// Get the session id from the selected conversation
						Conversation selectedConversation = conversationList.getSelectedValue();
						String sessionId = selectedConversation != null ? selectedConversation.getSessionId()
								: generateSessionId();

						// Call processUserInput() passing sessionId ad input
						try {
							processUserInput(sessionId);
						} catch (Exception processInputExcepetion) {
							showDetailedErrorMessage(processInputExcepetion);
						}
					}
					catch (Exception projectInputExcepetion) {
						showDetailedErrorMessage(projectInputExcepetion);

					}
				}
			}
		});

		newChatButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					createNewChat();
				} catch (Exception e1) {
					showDetailedErrorMessage(e1);
				}
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

	private void showDetailedErrorMessage(Exception e1) {
		StringBuilder errorMessage = new StringBuilder();

		// Aggiunge il messaggio dell'eccezione, se presente
		if (e1.getMessage() != null) {
			errorMessage.append(e1.getMessage()).append("\n");
		}

		// Aggiunge informazioni dettagliate sullo stack trace
		StackTraceElement[] stackTrace = e1.getStackTrace();
		if (stackTrace.length > 0) {
			StackTraceElement element = stackTrace[0];
			errorMessage.append("\nClass: ").append(element.getClassName()).append("\n");
			errorMessage.append("Method: ").append(element.getMethodName()).append("\n");
			errorMessage.append("Line: ").append(element.getLineNumber()).append("\n");
		}

		// Mostra il messaggio di errore
		GUI.showErrorMessageDialog(Application.getViewManager().getRootFrame(), "Feedback", errorMessage.toString());
	}



	private void processUserInput(String sessionId) throws ConnectException, IOException, Exception {
		// Acquisisco il testo dall'inputField
		String inputText = inputField.getText();

		updateConversation(inputText, sessionId);
		inputField.setText("");
	}

	// Metodo per inviare la richiesta al server e ottenere la risposta
	// Assicurati che il metodo chiamante sia in grado di gestire ConnectException e IOException
    private String sendRequestAndGetResponse(Conversation conversation) throws ConnectException, IOException {
        // Creazione del dialogo di attesa
        JDialog dialog = createWaitDialog();
        AtomicReference<String> responseRef = new AtomicReference<>();
        AtomicReference<Exception> exceptionRef = new AtomicReference<>();

        // Creazione e avvio di un SwingWorker per gestire la richiesta al server
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                // Effettua la richiesta al server e ottiene la risposta
                ApiRequest request = new ApiRequest(conversation.getSessionId(), projectId, conversation.getQueryId(),
                        conversation.getDiagramAsText(), conversation.getQuery());
                RestClient client = new RestClient();
                ApiResponse response = client.sendRequest(request);
                responseRef.set(response.getAnswer());
                return response.getAnswer();
            }

            @Override
            protected void done() {
                try {
                    get(); // Ottieni il risultato della richiesta e gestisci le eccezioni
                } catch (Exception e) {
                    exceptionRef.set(e); // Memorizza l'eccezione
                } finally {
                    dialog.dispose(); // Chiude il dialogo di attesa
                }
            }
        };

        worker.execute(); // Avvia il lavoro in background

        // Mostra il dialogo di attesa in modo modale
        dialog.setVisible(true);

        // Controlla se c'è stata un'eccezione e rilanciala
        if (exceptionRef.get() != null) {
            Exception e = exceptionRef.get();
            if (e instanceof ConnectException) {
                throw (ConnectException) e;
            } else if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new RuntimeException(e); // Per altre eccezioni non previste
            }
        }

        // Restituisci la risposta ottenuta
        return responseRef.get();
    }
	

	// Metodo per creare il dialogo di attesa con puntini sospensivi
	private JDialog createWaitDialog() {
		JDialog dialog = new JDialog((Frame) null, "Please wait", true);
		JLabel label = new JLabel("Processing, please wait");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		dialog.getContentPane().add(label);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.setSize(300, 100);
		dialog.setLocationRelativeTo(null); // Centra il dialogo sullo schermo
		dialog.setResizable(false); // Disabilita l'icona di ingrandimento

		// Rimuove il pulsante di chiusura dalla finestra di dialogo
		dialog.setUndecorated(true);
		dialog.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);

		// Creazione di un timer per aggiornare il testo del label con i puntini sospensivi
		Timer timer = new Timer(500, e -> {
			String text = label.getText();
			if (text.endsWith("...")) {
				label.setText("Processing, please wait");
			} else {
				label.setText(text + ".");
			}
		});
		timer.start();

		// Interrompi il timer quando il dialogo viene chiuso
		dialog.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosed(java.awt.event.WindowEvent windowEvent) {
				timer.stop();
			}
		});

		return dialog;
	}


	/*
	 * private void updateInputFieldText() { StringBuilder text = new
	 * StringBuilder(inputField.getText()); for (int i = 0; i < dotsCount; i++) {
	 * text.append("."); } inputField.setText(text.toString()); }
	 */

	private void updateConversation(String inputText, String sessionId)
			throws ConnectException, IOException, Exception {

		String diagramAsText = ClassInfo.exportInformation(Application.getProject(), "it");

		if (!inputText.isEmpty()) {
			// Aggiungi il testo alla conversazione corrente solo se non è vuoto
			if (conversationListModel.isEmpty()) {
				Conversation newConversation = createNewConversation(sessionId, inputText, diagramAsText);
				conversationListModel.addElement(newConversation);
				conversationList.setSelectedValue(newConversation, true);
			}

			Conversation currentConversation = conversationList.getSelectedValue();

			if (currentConversation != null) {
				// currentConversation.appendMessage(answer + "\n" + response);

				// update text description of diagrams
				currentConversation.setDiagramAsText(diagramAsText);
				// update query
				currentConversation.setQuery(inputText);

				String answer = prefixAnswer + inputText;
				currentConversation.appendMessage(answer);

				String response = sendRequestAndGetResponse(currentConversation);
				appendToPane(answer);
				if(response!=null)
					appendToPane(response);
				else
					throw new Exception("no response from the server");
				// currentConversation.appendMessage(answer);
				currentConversation.appendMessage(response);
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

	private Conversation createNewConversation(String sessionId, String query, String diagramAsText) {

		// Utilizzo del costruttore con i parametri
		Conversation newConversation = new Conversation(sessionId, projectId, diagramAsText, query, prefixAnswer);
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

	public void showFeedbackPanel(IProject project) {
		setProjectId(Application.getProject().getId());
		if (panel == null)
			createPanel();
		else {
			// after opened
			clearPanel();
			loadSerializedConversations();
		}
		Application.getViewManager().showMessagePaneComponent(id, title, panel);
	}

	private void createNewChat() throws Exception {
		// Ottieni il testo dalla JTextPane
		String query = outputPane.getText();

		// Controlla se l'inputText non è vuoto o uguale al messaggio di feedback
		// predefinito
		if (!query.isEmpty() && !query.equals(Config.DIALOG_FEEDBACK_MESSAGE)) {
			// i valori sessionId, projectId, etc.
			String sessionId = generateSessionId();
			String diagramAsText = ClassInfo.exportInformation(Application.getProject(), "it");
			// Crea una nuova istanza di Conversation
			Conversation newConversation = new Conversation(sessionId, projectId, diagramAsText, query, prefixAnswer);

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
				try {
					createNewChat();
				} catch (Exception e1) {
					GUI.showErrorMessageDialog(Application.getViewManager().getRootFrame(), "Feedback",
							e1.getMessage());
				}
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
			// serve reimpostare il valore di conversationCounter? controllare
			conversationCounter = 0;
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

				// Assign focus to the first element in the conversation list model
				if (conversationListModel.size() > 0) {
					conversationList.setSelectedIndex(0);
				}
			}
		}
	}

	private void appendToPane(String text) {
		// Verifica se il testo inizia con "You:" e termina con "\n"
		if (text.startsWith(prefixAnswer)) {
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