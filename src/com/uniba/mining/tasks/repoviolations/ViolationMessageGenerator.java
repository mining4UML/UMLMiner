package com.uniba.mining.tasks.repoviolations;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.BiFunction;
import javax.swing.JTextArea;

public class ViolationMessageGenerator {

	public static boolean processCSV(String filePath, JTextArea resultTextArea) {
		boolean atLeastOneRow= false;
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] fields = line.split(";");
				if (fields.length >= 8 && "violation".equals(fields[3].trim())) {
					String activities = getField(fields, 2);
					String activityName = getField(fields,4);
					String diagramName = getField(fields, 6);
					String umlElementType = getField(fields, 8);
					String umlElementName = getField(fields, 9);
					String propertyName = getField(fields, 10);
					String propertyValue = getField(fields, 11);

					String constraint = getField(fields, 1);

					String message = generateMessage(constraint, activities, 
							activityName, diagramName, umlElementType, umlElementName, propertyName, propertyValue);
					System.out.println(message);
					resultTextArea.append(message + "\n");
					atLeastOneRow= true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return atLeastOneRow;
	}

	public static String generateMessage(String constraint, String... fields) {
		BiFunction<String, String, String> messageGenerator = null;

		switch (constraint.toLowerCase()) {
		case "exclusive choice":
			messageGenerator = (activityA, activityB) -> generateExclusiveChoiceMessage(activityA, activityB, fields);
			break;
		case "not chain succession": 
			messageGenerator = (activityA, activityB)->generateNotChaineMessage(activityA, activityB, fields);
			break;
		case "response":
			messageGenerator= (activityA, activityB)->generateResponseMessage(activityA, activityB, fields);
			break;
		case "altre_tipologie_di_violazione":
			messageGenerator = ViolationMessageGenerator::generateAltroTipoViolazioneMessage;
			break;
			// Aggiungi altri casi per tipi di constraint aggiuntivi...
		default:
			messageGenerator = ViolationMessageGenerator::generateAltroTipoViolazioneMessage;
			//throw new UnsupportedOperationException("Tipo di constraint non gestito: " + constraint);
		}

		String activities = getField(fields, 0);
		String[] activityParts = extractActivityParts(activities);

		return messageGenerator.apply(activityParts[0].trim(), activityParts.length > 1 ? activityParts[1].trim() : null);
	}


	private static String generateExclusiveChoiceMessage(String activityA, String activityB, String[] fields) {
		return generateCommonMessage("Exclusive Choice", fields) +
				"Violation description: "+
				activityA + " and " + (activityB != null ? activityB : "") +
				" must occur at least once and they exclude each other\n" ;
	}

	private static String generateNotChaineMessage(String activityA, String activityB, String[] fields) {
		return generateCommonMessage("Not Chain Succession", fields) +
				"Violation description: "+
				activityA + " and " + (activityB != null ? activityB : "") +
				" occur together if and only if the latter does not immediately follow the former\n" ;
	}

	private static String generateResponseMessage(String activityA, String activityB, String[] fields) {
		return generateCommonMessage("Response", fields) +
				"Violation description: "+
				"if "+ activityA + " occurs then" + (activityB != null ? activityB : "") +
				" occurs after "+ activityA + "\n" ;
	}




	private static String generateAltroTipoViolazioneMessage(String activityA, String activityB) {
		// Logica per generare il messaggio per un altro tipo di violazione utilizzando 'activityA' e 'activityB'...
		return "Violation not recognized\n";
	}

	private static String generateCommonMessage(String violationType, String... fields) {
		StringBuilder message = new StringBuilder("Violation Type: " + violationType + "\n");
		String[] labels = {"Activity Name","Diagram Name", "UML Element Type", "UML Element Name", "Property Name", "Property Value"};

		for (int i = 0; i < labels.length; i++) {
			String fieldValue = getField(fields, i + 1);
			if (!fieldValue.isEmpty()) {
				message.append(labels[i]).append(": ").append(fieldValue).append("\n");
			}
		}

		return message.toString();
	}

	private static String getField(String[] fields, int index) {
		return index < fields.length && !fields[index].trim().isEmpty() ? fields[index].trim() : "";
	}

	private static String[] extractActivityParts(String activities) {
		return activities.replaceAll("\\[|\\]", "").split(",");
	}
}
