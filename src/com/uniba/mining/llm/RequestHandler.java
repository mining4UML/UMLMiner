package com.uniba.mining.llm;

import java.awt.Frame;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import com.uniba.mining.feedback.Conversation;
import com.uniba.mining.feedback.FileUtilities;
import com.uniba.mining.logging.LogStreamer;
import com.uniba.mining.utils.Application;

public class RequestHandler {

	private final String projectId;
	private final Conversation conversation;

	public RequestHandler(String projectId, Conversation conversation) {
		this.projectId = projectId;
		this.conversation = conversation;
	}

	public ParsedResponse sendRequestAndGetResponse() throws ConnectException, IOException {
		// Create the waiting dialog
		JDialog dialog = createWaitDialog();
		AtomicReference<String> responseRef = new AtomicReference<>();
		AtomicReference<Exception> exceptionRef = new AtomicReference<>();

		// Create and start a SwingWorker to handle the server request
		SwingWorker<String, Void> worker = new SwingWorker<>() {
			@Override
			protected String doInBackground() throws Exception {
				// Perform the request to the server and get the response
				ApiRequest request = createApiRequest(conversation);
				ApiResponse response = sendApiRequest(request);

				responseRef.set(response.getAnswer());
				return response.getAnswer();
			}

			@Override
			protected void done() {
				try {
					get(); // Get the result of the request and handle exceptions
				} catch (Exception e) {
					exceptionRef.set(e); // Store the exception
				} finally {
					dialog.dispose(); // Close the waiting dialog
				}
			}
		};

		worker.execute(); // Start the background work

		// Show the waiting dialog modally
		dialog.setVisible(true);

		// Check if there was an exception and rethrow it
		if (exceptionRef.get() != null) {
			Exception e = exceptionRef.get();
			if (e instanceof ConnectException) {
				throw (ConnectException) e;
			} else if (e instanceof IOException) {
				throw (IOException) e;
			} else {
				throw new RuntimeException(e); // For other unexpected exceptions
			}
		}

		// Return the obtained response
		return ResponseParser.parseResponse(responseRef.get());
	}

//	private ApiRequest createApiRequest(Conversation conversation) throws IOException {
//		String diagramId = conversation.getDiagramId();
//		Path path = LogStreamer.getRequirementsDirectory();
//		String requirements = FileUtilities.loadFileContent(diagramId, path);
//		String user = LogStreamer.getUsername();
//		user += "-" + Application.getProductInfo();
//
//		return new ApiRequest(
//				conversation.getSessionId(),
//				projectId,
//				diagramId,
//				conversation.getQueryId(),
//				conversation.getDiagramAsText(),
//				conversation.getDiagramAsXML(),
//				requirements,
//				user,
//				conversation.getQuery()
//				);
//	}
	
	private ApiRequest createApiRequest(Conversation conversation) throws IOException {
		String diagramId = conversation.getDiagramId();
		Path path = LogStreamer.getRequirementsDirectory();
		String requirementsFromFile = FileUtilities.loadFileContent(diagramId, path);
		String user = LogStreamer.getUsername();
		user += "-" + Application.getProductInfo();

		// Usa i requirements già salvati nella Conversation se disponibili, altrimenti quelli da file
		String finalRequirements = conversation.getRequirements() != null && !conversation.getRequirements().isBlank()
				? conversation.getRequirements()
				: requirementsFromFile;

		return new ApiRequest(
				conversation.getSessionId(),
				projectId,
				diagramId,
				conversation.getQueryId(),
				conversation.getDiagramAsText(),
				conversation.getDiagramAsXML(),
				finalRequirements,
				conversation.getProcess(),
				conversation.getMetrics(),
				user,
				conversation.getQuery()
		);
	}


	private ApiResponse sendApiRequest(ApiRequest request) throws IOException {
		RestClient client = new RestClient();
		return client.sendRequest(request);
	}

	/**
	 * Creates a modal JDialog that displays a "Please wait" message with a loading
	 * animation. The dialog is centered on the screen and does not allow resizing
	 * or closing via the close button. A Timer is used to animate ellipsis in the
	 * message to indicate processing.
	 *
	 * @return A JDialog instance configured with a loading message and animation.
	 */
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

		// Creazione di un timer per aggiornare il testo del label con i puntini
		// sospensivi
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
}