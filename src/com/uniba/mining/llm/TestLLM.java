package com.uniba.mining.llm;

import java.io.IOException;

public class TestLLM {
	public static void main(String[] args) {
		// Creazione di un oggetto ApiRequest con i dati appropriati
		ApiRequest request = new ApiRequest("s1", "p1", "d1", "q1", "Un diagramma descritto come testo.", 
				null, "requisiti", "username", "Qual è il significato di questo diagramma?");

		// Creazione di un oggetto RestClient
		RestClient client = new RestClient();

		try {
			// Invio della richiesta al server e ottenimento della risposta
			ApiResponse response = client.sendRequest(request);

			// Stampare la risposta ottenuta
			System.out.println("Answer: " + response.getAnswer());
			System.out.println("Project Id: " + response.getProjectId());
			System.out.println("Timestamp: " + response.getTimestamp());
			System.out.println("ResponseId: " + response.getResponseId());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
