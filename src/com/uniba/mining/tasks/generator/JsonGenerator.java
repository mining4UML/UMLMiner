package com.uniba.mining.tasks.generator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class JsonGenerator {

    public static void main(String[] args) {
        // Numero desiderato di classi nel JSON
        int numClasses = 5;

        // Genera il JSON con classi casuali
        JSONObject jsonDiagram = generateRandomJsonDiagram(numClasses);

        // Salva il JSON su un file
        saveJsonToFile(jsonDiagram, "output.json");
    }

    private static JSONObject generateRandomJsonDiagram(int numClasses) {
        JSONObject jsonDiagram = new JSONObject();
        JSONArray jsonClasses = new JSONArray();

        for (int i = 0; i < numClasses; i++) {
            JSONObject jsonClass = generateRandomJsonClass();
            jsonClasses.put(jsonClass);
        }

        jsonDiagram.put("classi", jsonClasses);
        return jsonDiagram;
    }

    private static JSONObject generateRandomJsonClass() {
        JSONObject jsonClass = new JSONObject();

        // Genera dati casuali per la classe
        String className = "Class" + getRandomNumber(1, 100);
        jsonClass.put("nome", className);
        jsonClass.put("sottoclassi", new JSONArray());
        jsonClass.put("attributi", generateRandomJsonAttributes());
        jsonClass.put("generalizzazione", getRandomGeneralization());
        jsonClass.put("associazione", generateRandomJsonAssociations());
        jsonClass.put("aggregazione", getRandomAggregation());
        jsonClass.put("operazioni", generateRandomJsonOperations());

        return jsonClass;
    }

    private static JSONArray generateRandomJsonAttributes() {
        JSONArray jsonAttributes = new JSONArray();

        // Genera dati casuali per gli attributi
        for (int i = 0; i < getRandomNumber(1, 5); i++) {
            JSONObject jsonAttribute = new JSONObject();
            jsonAttribute.put("nome", "Attribute" + getRandomNumber(1, 100));
            jsonAttribute.put("tipo", getRandomDataType());
            jsonAttributes.put(jsonAttribute);
        }

        return jsonAttributes;
    }

    private static String getRandomGeneralization() {
        // Genera casualmente se deve esistere una generalizzazione
        return getRandomBoolean() ? "SuperClass" + getRandomNumber(1, 10) : null;
    }

    private static JSONArray generateRandomJsonAssociations() {
        JSONArray jsonAssociations = new JSONArray();

        // Genera casualmente se devono esistere associazioni
        if (getRandomBoolean()) {
            JSONObject jsonAssociation = new JSONObject();
            jsonAssociation.put("nome", "Association" + getRandomNumber(1, 100));
            jsonAssociation.put("destinazione", "DestinationClass" + getRandomNumber(1, 10));
            jsonAssociation.put("molteplicita", getRandomMultiplicity());
            jsonAssociations.put(jsonAssociation);
        }

        return jsonAssociations;
    }

    private static JSONObject getRandomAggregation() {
        // Genera casualmente se deve esistere un'aggregazione
        if (getRandomBoolean()) {
            JSONObject jsonAggregation = new JSONObject();
            jsonAggregation.put("nome", "Aggregation" + getRandomNumber(1, 100));
            jsonAggregation.put("destinazione", "DestinationClass" + getRandomNumber(1, 10));
            jsonAggregation.put("molteplicita", getRandomMultiplicity());
            return jsonAggregation;
        }

        return null;
    }

    private static JSONArray generateRandomJsonOperations() {
        JSONArray jsonOperations = new JSONArray();

        // Genera dati casuali per le operazioni
        for (int i = 0; i < getRandomNumber(1, 3); i++) {
            JSONObject jsonOperation = new JSONObject();
            jsonOperation.put("nome", "Operation" + getRandomNumber(1, 100));
            jsonOperation.put("tipoRitorno", getRandomDataType());
            jsonOperation.put("parametri", generateRandomJsonParameters());
            jsonOperations.put(jsonOperation);
        }

        return jsonOperations;
    }

    private static JSONArray generateRandomJsonParameters() {
        JSONArray jsonParameters = new JSONArray();

        // Genera dati casuali per i parametri
        for (int i = 0; i < getRandomNumber(1, 2); i++) {
            JSONObject jsonParameter = new JSONObject();
            jsonParameter.put("nome", "Parameter" + getRandomNumber(1, 10));
            jsonParameter.put("tipo", getRandomDataType());
            jsonParameters.put(jsonParameter);
        }

        return jsonParameters;
    }

    private static int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    private static boolean getRandomBoolean() {
        Random random = new Random();
        return random.nextBoolean();
    }

    private static String getRandomDataType() {
        String[] dataTypes = {"int", "string", "boolean"};
        return dataTypes[getRandomNumber(0, 2)];
    }

    private static String getRandomMultiplicity() {
        return getRandomBoolean() ? "0..1" : "1..*";
    }

    private static void saveJsonToFile(JSONObject jsonObject, String filePath) {
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonObject.toString(2)); // Indentazione di 2 spazi per una formattazione più leggibile
            System.out.println("File JSON creato con successo: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

