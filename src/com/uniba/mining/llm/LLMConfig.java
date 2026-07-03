package com.uniba.mining.llm;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileOutputStream;
import java.io.IOException;

import com.uniba.mining.utils.Application;

public class LLMConfig {

    private static final String ROOT_PATH =
            Application.getPluginInfo("UMLMiner")
                    .getPluginDir()
                    .getAbsolutePath();

    private static final String LLM_CONFIG_PATH =
            String.join(File.separator, ROOT_PATH, "llm.properties");

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = new FileInputStream(LLM_CONFIG_PATH)) {
            properties.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getProvider() {
        return properties.getProperty("llm.provider", "local");
    }

    public static String getBaseUrl() {
        return properties.getProperty("llm.baseUrl", "http://localhost:11434/v1");
    }

    public static String getModel() {
        return properties.getProperty("llm.model", "llama3.1");
    }

    public static String getApiKey() {
        return properties.getProperty("llm.apiKey", "");
    }

    public static double getTemperature() {
        String value = properties.getProperty("llm.temperature", "0.2");

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.2;
        }
    }
    
    public static void setProvider(String provider) {

        if (provider == null || provider.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Provider cannot be null or empty."
            );
        }

        properties.setProperty(
                "llm.provider",
                provider.trim().toLowerCase()
        );

        saveProperties();
    }
    
    private static void saveProperties() {

        try (FileOutputStream fos =
                new FileOutputStream(new File(LLM_CONFIG_PATH))) {

            properties.store(
                    fos,
                    "UML Miner LLM Configuration"
            );

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    private LLMConfig() {
        // Utility class
    }
}