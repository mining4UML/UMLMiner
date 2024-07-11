package com.uniba.mining.feedback;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

import com.uniba.mining.logging.LogStreamer;
import com.uniba.mining.utils.Application;
import com.uniba.mining.utils.GUI;

public class ConversationsSerializer {

	//private static final String CONVERSATIONS_FOLDER = "conversations";

	/**
	 * Serialize the list of conversations to a file.
	 * 
	 * @param conversations The list of Conversation objects to be serialized.
	 * @param projectId     The ID of the project associated with the conversations.
	 */

	public static void serializeConversations(List<Conversation> conversations, String id) {
		// Creazione della cartella delle conversazioni se non esiste
		
		Path path = LogStreamer.getConversationsDirectory();
//		File folder = new File(CONVERSATIONS_FOLDER);
//		if (!folder.exists()) {
//			if(!folder.mkdirs()); // Utilizza mkdirs() per creare tutte le directory intermedie se non esistono
//			GUI.showErrorMessageDialog(Application.getViewManager().getRootFrame(), "Feedback",
//					"Error while creating conversations folder: ");
//		}

		//String filename = CONVERSATIONS_FOLDER + "/" + id + ".ser";
		// Crea il percorso completo del file
        Path filePath = path.resolve(id + ".ser");
        
//		try (FileOutputStream fos = new FileOutputStream(filename);
//				ObjectOutputStream oos = new ObjectOutputStream(fos)) {
//			oos.writeObject(conversations);
//			System.out.println("Conversations serialized successfully to file: " + filename);
//			File savedFile = new File(filename);
//			System.out.println("Saved file path: " + savedFile.getAbsolutePath());
//		} catch (IOException e) {
//			GUI.showErrorMessageDialog(Application.getViewManager().getRootFrame(), "Feedback",
//					"Error while serializing conversations: " + e.getMessage());
//		}

        // Supponiamo che 'conversations' sia l'oggetto da serializzare
        //Object conversations = new Object(); // Sostituisci con il tuo oggetto reale

        // Serializza l'oggetto e salva nel file
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(conversations);
            System.out.println("Conversations serialized successfully to file: " + filePath);
            System.out.println("Saved file path: " + filePath.toAbsolutePath());

        } catch (IOException e) {
            GUI.showErrorMessageDialog(Application.getViewManager().getRootFrame(), "Feedback",
                    "Error while serializing conversations: " + e.getMessage());
        }
	}

//	public static List<Conversation> deserializeConversations(String id) {
//		List<Conversation> conversations = null;
//		String filename = CONVERSATIONS_FOLDER + "/" + id + ".ser";
//		try (FileInputStream fis = new FileInputStream(filename); ObjectInputStream ois = new ObjectInputStream(fis)) {
//			conversations = (List<Conversation>) ois.readObject();
//			System.out.println("Conversations deserialized successfully from diagram " + id);
//		} catch (IOException | ClassNotFoundException e) {
//			System.err.println(
//					"Error while deserializing conversations from diagram " + id + ": " + e.getMessage());
//		}
//		return conversations;
//	}
	public static List<Conversation> deserializeConversations(String id) {
        List<Conversation> conversations = null;
        
        // Ottieni la directory e crea il percorso completo del file
        Path path = LogStreamer.getConversationsDirectory();
        Path filePath = path.resolve(id + ".ser");

        try (FileInputStream fis = new FileInputStream(filePath.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            conversations = (List<Conversation>) ois.readObject();
            System.out.println("Conversations deserialized successfully from diagram " + id);
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error while deserializing conversations from diagram " + id + ": " + e.getMessage());
        }
        
        return conversations;
    }
}
