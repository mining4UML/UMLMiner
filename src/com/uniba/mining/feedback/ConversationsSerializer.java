package com.uniba.mining.feedback;

import java.io.*;
import java.util.List;

public class ConversationsSerializer {

	private static final String CONVERSATIONS_FOLDER = "conversations";

	/**
	 * Serialize the list of conversations to a file.
	 * 
	 * @param conversations The list of Conversation objects to be serialized.
	 * @param projectId     The ID of the project associated with the conversations.
	 */

	public static void serializeConversations(List<Conversation> conversations, String id) {
		// Creazione della cartella delle conversazioni se non esiste
		File folder = new File(CONVERSATIONS_FOLDER);
		if (!folder.exists()) {
			folder.mkdirs(); // Utilizza mkdirs() per creare tutte le directory intermedie se non esistono
		}

		String filename = CONVERSATIONS_FOLDER + "/" + id + ".ser";
		try (FileOutputStream fos = new FileOutputStream(filename);
				ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(conversations);
			System.out.println("Conversations serialized successfully to file: " + filename);
			File savedFile = new File(filename);
			System.out.println("Saved file path: " + savedFile.getAbsolutePath());
		} catch (IOException e) {
			System.err.println("Error while serializing conversations: " + e.getMessage());
		}
	}

	public static List<Conversation> deserializeConversations(String id) {
		List<Conversation> conversations = null;
		String filename = CONVERSATIONS_FOLDER + "/" + id + ".ser";
		try (FileInputStream fis = new FileInputStream(filename); ObjectInputStream ois = new ObjectInputStream(fis)) {
			conversations = (List<Conversation>) ois.readObject();
			System.out.println("Conversations deserialized successfully from diagram " + id);
		} catch (IOException | ClassNotFoundException e) {
			System.err.println(
					"Error while deserializing conversations from diagram " + id + ": " + e.getMessage());
		}
		return conversations;
	}
}
