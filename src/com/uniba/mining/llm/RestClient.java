package com.uniba.mining.llm;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniba.mining.utils.Application;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class RestClient {
    private static final Logger LOGGER = Logger.getLogger(RestClient.class.getName());
    private static final String BASE_URL;
    private static final String ROOT_PATH = Application.getPluginInfo("UMLMiner").getPluginDir().getAbsolutePath();
    private static final String CONFIG_PATH = String.join(File.separator, ROOT_PATH, "config.properties");
    private ObjectMapper objectMapper;

    static {
        try {
            // Configurazione del FileHandler per il logger
            FileHandler fileHandler = new FileHandler("server.log", true); // true per abilitare l'append
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.ALL);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to configure logger", e);
        }

        Properties properties = new Properties();
        try (InputStream config = new FileInputStream(CONFIG_PATH)) {
            properties.load(config);
            BASE_URL = properties.getProperty("BASE_URL");
            if (BASE_URL == null) {
                throw new RuntimeException("BASE_URL not found in configuration file");
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load configuration", ex);
        }
    }

    public RestClient() {
        this.objectMapper = new ObjectMapper();
        //  aggiunta della configurazione per ignorare i campi sconosciuti durante la deserializzazione
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public ApiResponse sendRequest(ApiRequest request) throws IOException {
        String jsonRequest;
        URL url;
        HttpURLConnection connection = null;
        ApiResponse response = null;

        try {
            jsonRequest = objectMapper.writeValueAsString(request);
            LOGGER.info("Request JSON: " + jsonRequest);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to convert request to JSON", e);
            throw new IOException("Failed to convert request to JSON", e);
        }

        try {
            url = new URL(BASE_URL);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Invalid URL: " + BASE_URL, e);
            throw new IOException("Invalid URL: " + BASE_URL, e);
        }

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.getOutputStream().write(jsonRequest.getBytes());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to open HTTP connection", e);
            throw new IOException("Failed to open HTTP connection", e);
        }

        try {
            int statusCode = connection.getResponseCode();
            LOGGER.info("Response Code: " + statusCode);
            InputStream inputStream;
            if (statusCode >= 200 && statusCode < 400) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }

            // Log the response content for debugging
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                StringBuilder responseContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                LOGGER.info("Response Content: " + responseContent.toString());
                
                // Deserialize the response content
                response = objectMapper.readValue(responseContent.toString(), ApiResponse.class);
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read JSON response", e);
            throw new IOException("Failed to read JSON response", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response;
    }
}
