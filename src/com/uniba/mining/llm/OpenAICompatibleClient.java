package com.uniba.mining.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class OpenAICompatibleClient {

    private final String baseUrl;
    private final String model;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public OpenAICompatibleClient(String baseUrl, String model, String apiKey) {
        this.baseUrl = removeTrailingSlash(baseUrl);
        this.model = model;
        this.apiKey = apiKey;
        this.objectMapper = new ObjectMapper();
    }

    public String generate(String systemPrompt, String userPrompt) throws IOException {
        String endpoint = baseUrl + "/chat/completions";
        String jsonRequest = buildRequestBody(systemPrompt, userPrompt);

        HttpURLConnection connection = null;

        try {
            URL url = new URL(endpoint);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");

            if (apiKey != null && !apiKey.trim().isEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            }

            connection.setDoOutput(true);

            try (OutputStreamWriter writer =
                         new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write(jsonRequest);
                writer.flush();
            }

            int statusCode = connection.getResponseCode();

            InputStream inputStream;

            if (statusCode >= 200 && statusCode < 300) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }

            String responseBody = readResponse(inputStream);

            if (statusCode < 200 || statusCode >= 300) {
                throw new IOException("LLM server returned error " + statusCode + ": " + responseBody);
            }

            return extractAnswer(responseBody);

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String buildRequestBody(String systemPrompt, String userPrompt) throws IOException {
        String systemPromptJson = objectMapper.writeValueAsString(systemPrompt);
        String userPromptJson = objectMapper.writeValueAsString(userPrompt);
        String modelJson = objectMapper.writeValueAsString(model);

        return "{"
                + "\"model\":" + modelJson + ","
                + "\"messages\":["
                + "{"
                + "\"role\":\"system\","
                + "\"content\":" + systemPromptJson
                + "},"
                + "{"
                + "\"role\":\"user\","
                + "\"content\":" + userPromptJson
                + "}"
                + "],"
                + "\"temperature\":0.2,"
                + "\"stream\":false"
                + "}";
    }

    private String extractAnswer(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode choices = root.path("choices");

        if (!choices.isArray() || choices.size() == 0) {
            throw new IOException("Invalid LLM response: missing choices field. Response: " + responseBody);
        }

        JsonNode content = choices.get(0).path("message").path("content");

        if (content.isMissingNode() || content.isNull()) {
            throw new IOException("Invalid LLM response: missing message.content field. Response: " + responseBody);
        }

        return content.asText();
    }

    private String readResponse(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }

        StringBuilder response = new StringBuilder();

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        return response.toString();
    }

    private String removeTrailingSlash(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Base URL cannot be null or empty");
        }

        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }

        return value;
    }
}