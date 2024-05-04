package com.uniba.mining.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.HttpURLConnection;
import java.net.URL;

import java.io.IOException;

public class RestClient {
    private static final String BASE_URL = "http://31.156.66.133:28000/interact/";

    private ObjectMapper objectMapper;

    public RestClient() {
        this.objectMapper = new ObjectMapper();
    }

    public ApiResponse sendRequest(ApiRequest request) throws IOException {
        // Converto l'oggetto di richiesta in JSON
        String jsonRequest = objectMapper.writeValueAsString(request);
        
        // Stampo il JSON della richiesta
        System.out.println("Request JSON: " + jsonRequest);

        // Creo una connessione HTTP
        URL url = new URL(BASE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json"); // Imposto l'header Content-Type
        connection.setRequestProperty("Accept", "application/json"); // Imposto l'header Accept
        connection.setDoOutput(true);

        // Invio il payload JSON
        connection.getOutputStream().write(jsonRequest.getBytes());

        // Legg0 la risposta JSON
        ApiResponse response = objectMapper.readValue(connection.getInputStream(), ApiResponse.class);

        // Chiudo la connessione
        connection.disconnect();

        return response;
    }
}
