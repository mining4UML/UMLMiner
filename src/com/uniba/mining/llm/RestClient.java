package com.uniba.mining.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniba.mining.utils.Application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class RestClient {
    private static final String BASE_URL;
    private static final String ROOT_PATH = Application.getPluginInfo("UMLMiner").getPluginDir().getAbsolutePath();
    private static final String CONFIG_PATH = String.join(File.separator, ROOT_PATH, "config.properties");
    private ObjectMapper objectMapper;

    static {
        Properties properties = new Properties();
        try (InputStream config = new FileInputStream(CONFIG_PATH)) {
            properties.load(config);
            BASE_URL = properties.getProperty("BASE_URL");
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load configuration", ex);
        }
    }
    

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
