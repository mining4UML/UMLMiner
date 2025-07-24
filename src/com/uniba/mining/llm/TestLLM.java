package com.uniba.mining.llm;

import java.io.IOException;

public class TestLLM {
	public static void main(String[] args) {
		// Creazione di un oggetto ApiRequest con i dati aggiornati
		ApiRequest request = new ApiRequest(
				"s1",                             // sessionId
				"p1",                             // projectId
				"d1",                             // diagramId
				"q1",                             // queryId
				"Un diagramma descritto come testo.", // diagramAsText
				null,                             // diagramAsXML (può essere null per il test)
				"requisiti",                      // requirements
				"processo di modellazione",       // process
				"metriche del diagramma",         // metrics
				"username",                       // user
				"Qual è il significato di questo diagramma?" // query
		);

		// Creazione del client REST
		RestClient client = new RestClient();

		try {
			// Invio della richiesta e stampa della risposta
			ApiResponse response = client.sendRequest(request);
			System.out.println("Answer: " + response.getAnswer());
			System.out.println("Project Id: " + response.getProjectId());
			System.out.println("Timestamp: " + response.getTimestamp());
			System.out.println("ResponseId: " + response.getResponseId());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
