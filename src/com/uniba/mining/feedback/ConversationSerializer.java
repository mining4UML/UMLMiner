package com.uniba.mining.feedback;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConversationSerializer {
	private static final String FILENAME = "conversations.ser";

	public static void serializeConversations(List<Conversation> conversations) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILENAME))) {
			oos.writeObject(conversations);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<Conversation> deserializeConversations() {
		List<Conversation> conversations = new ArrayList<>();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILENAME))) {
			conversations = (List<Conversation>) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return conversations;
	}
}
