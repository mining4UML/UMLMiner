package com.uniba.mining.tasks.repoviolations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.BiFunction;
import javax.swing.JTextArea;

public class ViolationMessageGenerator {

	public static File processCSV(File inputFile, File outputFile) {
		// Crea un file temporaneo per l'output nella stessa cartella dell'input
		outputFile = new File(outputFile, inputFile.getName());

		System.out.println("inputFile: " + inputFile.getName() + " " + inputFile.getAbsolutePath());
		System.out.println("outputFile: " + outputFile.getName() + " " + outputFile.getAbsolutePath());

		try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
				BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

			String line;
			boolean atLeastOneRow = false;
			int lineNumber = 0;

			while ((line = reader.readLine()) != null) {
				lineNumber++;

				try {
					String[] fields = line.split(",");
					//System.out.println("fields: "+fields.toString());
					
					System.out.println(fields[3]+ " " +fields[3].trim()+ " fields.length >= 8: "+fields.length);

					//if (fields.length >= 8 && "violation".equals(fields[3].trim())) {
					if (fields.length >= 8) {
						System.out.println("violation detected");
						String activities = getField(fields, 2).replaceAll("^\"|\"$", "");
						String activityName = getField(fields, 4).replaceAll("^\"|\"$", "");
						String diagramName = getField(fields, 6).replaceAll("^\"|\"$", "");
						String umlElementType = getField(fields, 8).replaceAll("^\"|\"$", "");
						String umlElementName = getField(fields, 9).replaceAll("^\"|\"$", "");
						String propertyName = getField(fields, 10).replaceAll("^\"|\"$", "");
						String propertyValue = getField(fields, 11).replaceAll("^\"|\"$", "");
						String relationshipFrom = getField(fields, 12).replaceAll("^\"|\"$", "");
						String relationshipTo = getField(fields, 13).replaceAll("^\"|\"$", "");
						String constraint = getField(fields, 1).replaceAll("^\"|\"$", "");
						
						System.out.println("constraint: "+constraint);

						String message = generateMessage(constraint, activities, 
								activityName, diagramName, umlElementType, umlElementName, 
								propertyName, propertyValue, relationshipFrom, relationshipTo);

						// Scrive il messaggio nel file di output
						writer.write(message);
						System.out.println("message: " + message);
						writer.newLine();
						atLeastOneRow = true;
						System.out.println("avvaloro atLeastOneRow");
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					System.err.println("ERROR: ArrayIndexOutOfBoundsException at line " + lineNumber + " - " + e.getMessage());
					e.printStackTrace();
				} catch (Exception e) {
					System.err.println("ERROR: Exception at line " + lineNumber + " - " + e.getMessage());
					e.printStackTrace();
				}
			}

			// Se almeno una riga è stata scritta, restituisce il file elaborato
			System.out.println("Valore di atLeastOneRow: "+atLeastOneRow);
			return atLeastOneRow ? outputFile : null;

		} catch (FileNotFoundException e) {
			System.err.println("ERROR: File not found - " + inputFile.getAbsolutePath());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("ERROR: IO Exception while processing file - " + inputFile.getAbsolutePath());
			e.printStackTrace();
		} catch (SecurityException e) {
			System.err.println("ERROR: SecurityException - Cannot access file: " + inputFile.getAbsolutePath());
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("ERROR: Unexpected exception while processing file: " + inputFile.getAbsolutePath());
			e.printStackTrace();
		}

		return null;
	}



	private static String getField(String[] fields, int index) {
		return (index < fields.length) ? fields[index].trim() : "";
	}

	public static String generateMessage(String constraint, String... fields) {
		BiFunction<String, String, String> messageGenerator = null;

		switch (constraint.toLowerCase()) {
		// choice templates
		case "choice":
			messageGenerator = (activityA, activityB) -> generateChoiceMessage(activityA, activityB, fields);
			break;
		case "exclusive choice":
			messageGenerator = (activityA, activityB) -> generateExclusiveChoiceMessage(activityA, activityB, fields);
			break;

			// binary negative templates
		case "not chain succession": 
			messageGenerator = (activityA, activityB)->generateNotChainMessage(activityA, activityB, fields);
			break;
		case "not co-existence": 
			messageGenerator = (activityA, activityB)->generateNotCoExistenceMessage(activityA, activityB, fields);
			break;
		case "not succession": 
			messageGenerator = (activityA, activityB)->generateNotSuccessionMessage(activityA, activityB, fields);
			break;

			// binary positive templates
		case "alternate precedence":
			messageGenerator= (activityA, activityB)->generateAlternatePrecedenceMessage(activityA, activityB, fields);
			break;
		case "alternate response":
			messageGenerator= (activityA, activityB)->generateAlternateResponseMessage(activityA, activityB, fields);
			break;
		case "alternate succession":
			messageGenerator= (activityA, activityB)->generateAlternateSuccessionMessage(activityA, activityB, fields);
			break;
		case "chain precedence":
			messageGenerator= (activityA, activityB)->generateChainPrecedenceMessage(activityA, activityB, fields);
			break;
		case "chain response":
			messageGenerator= (activityA, activityB)->generateChainResponseMessage(activityA, activityB, fields);
			break;
		case "chain succession":
			messageGenerator= (activityA, activityB)->generateChainSuccessionMessage(activityA, activityB, fields);
			break;
		case "co-existence":
			messageGenerator= (activityA, activityB)->generateCoExistenceMessage(activityA, activityB, fields);
			break;
		case "precedence":
			messageGenerator= (activityA, activityB)->generatePrecedenceMessage(activityA, activityB, fields);
			break;
		case "responded existence":
			messageGenerator= (activityA, activityB)->generateRespondedExistenceMessage(activityA, activityB, fields);
			break;
		case "response":
			messageGenerator= (activityA, activityB)->generateResponseMessage(activityA, activityB, fields);
			break;
		case "succession":
			messageGenerator= (activityA, activityB)->generateSuccessionMessage(activityA, activityB, fields);
			break;

			// unary templates
		case "absence":
			messageGenerator= (activityA, activityB)->generateAbsenceMessage(activityA, fields);
			break;
		case "absence2":
			messageGenerator= (activityA, activityB)->generateAbsence2Message(activityA, fields);
			break;
		case "absence3":
			messageGenerator= (activityA, activityB)->generateAbsence3Message(activityA, fields);
			break;
		case "exactly1":
			messageGenerator= (activityA, activityB)->generateExactly1Message(activityA, fields);
			break;
		case "exactly2":
			messageGenerator= (activityA, activityB)->generateExactly2Message(activityA, fields);
			break;
		case "existence":
			messageGenerator= (activityA, activityB)->generateExistenceMessage(activityA, fields);
			break;
		case "existence2":
			messageGenerator= (activityA, activityB)->generateExistence2Message(activityA, fields);
			break;
		case "existence3":
			messageGenerator= (activityA, activityB)->generateExistence3Message(activityA, fields);
			break;
		case "init":
			messageGenerator=(activityA, activityB)->generateInitMessage(activityA, fields);
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

	private static String generateAbsenceMessage(String activityA, String[] fields) {
		return generateCommonMessage("Absence", fields) +
				"Violation description: "+
				activityA + " does not occur\n";
	}

	private static String generateAbsence2Message(String activityA, String[] fields) {
		return generateCommonMessage("Absence2", fields) +
				"Violation description: "+
				activityA + " occurs at most once\n";
	}

	private static String generateAbsence3Message(String activityA, String[] fields) {
		return generateCommonMessage("Absence3", fields) +
				"Violation description: "+
				activityA + " occurs at most twice\n";
	}

	private static String generateExactly1Message(String activityA, String[] fields) {
		return generateCommonMessage("Exactly1", fields) +
				"Violation description: "+
				activityA + " occurs exactly once\n";
	}

	private static String generateExactly2Message(String activityA, String[] fields) {
		return generateCommonMessage("Exactly2", fields) +
				"Violation description: "+
				activityA + " occurs exactly twice\n";
	}

	private static String generateExistenceMessage(String activityA, String[] fields) {
		return generateCommonMessage("Existence", fields) +
				"Violation description: "+
				activityA + " occurs at least once \n";
	}

	private static String generateExistence2Message(String activityA, String[] fields) {
		return generateCommonMessage("Existence2", fields) +
				"Violation description: "+
				activityA + " occurs at least twice\n";
	}

	private static String generateExistence3Message(String activityA, String[] fields) {
		return generateCommonMessage("Existence3", fields) +
				"Violation description: "+
				activityA + " occurs at least three times\n";
	}

	private static String generateInitMessage(String activityA, String[] fields) {
		return generateCommonMessage("Init", fields) +
				"Violation description: "+
				activityA + " occurs first\n";
	}

	private static String generateExclusiveChoiceMessage(String activityA, String activityB, String[] fields) {
		return generateCommonMessage("Exclusive Choice", fields) +
				"Violation description: "+
				activityA + " and " + (activityB != null ? activityB : "") +
				" must occur at least once and they exclude each other\n" ;
	}

	private static String generateChoiceMessage(String activityA, String activityB, String[] fields) {
		return generateCommonMessage("Choice", fields) +
				"Violation description: "+
				activityA + " or " + (activityB != null ? activityB : "") +
				" must occur at least once\n" ;
	}

	private static String generateNotChainMessage(String activityA, String activityB, String[] fields) {
		return generateCommonMessage("Not Chain Succession", fields) +
				"Violation description: "+
				activityA + " and " + (activityB != null ? activityB : "") +
				" occur together if and only if the latter does not immediately follow the former\n" ;
	}

	private static String generateNotCoExistenceMessage(String activityA, String activityB, String[] fields) {
		return generateCommonMessage("Not Co-Existence", fields) +
				"Violation description: "+
				activityA + " and " + (activityB != null ? activityB : "") +
				" never occur together\n" ;
	}

	private static String generateNotSuccessionMessage(String activityA, String activityB, String[] fields) {
		return generateCommonMessage("Not Succession", fields) +
				"Violation description: "+
				activityA + " can never occur before "
				+ (activityB != null ? activityB : ""+"\n");
	}


	// inizio

	private static String generateAlternatePrecedenceMessage(String activityA, String activityB, String[] fields) {
		return generateCommonMessage("Alternate Precedence", fields) +
				"Violation description: "+
				"Each time "+ activityA + " occurs it is preceded by" + (activityB != null ? activityB : "") +
				" and no other "+ activityA + " can recur in between\n" ;
	}


	private static String generateAlternateResponseMessage(String activityA, String activityB, String[] fields) {
		return generateCommonMessage("Alternate Precedence", fields) +
				"Violation description: "+
				"Each time "+ activityA + " occurs then " + (activityB != null ? activityB : "") +
				" occurs afterwards before "+ activityA + " recurs\n" ;
	}


	private static String generateAlternateSuccessionMessage(String activityA, String activityB, String[] fields) {
		return generateCommonMessage("Alternate Succession", fields) +
				"Violation description: "+
				activityA + " and " + (activityB != null ? activityB : "") +
				" occurs together if and only if the latter follows the former, and"
				+ " they alternate each other\n" ;
	}
	private static String generateChainPrecedenceMessage(String activityA, String activityB, String[] fields) {
		return generateCommonMessage("Chain Precedence", fields) +
				"Violation description: "+
				"Each time"+ activityA + " occurs then" + (activityB != null ? activityB : "") +
				" occurs immediately beforehand\n" ;
	}
	private static String generateChainResponseMessage(String activityA, String activityB, String[] fields) {
		return generateCommonMessage("Chain Response", fields) +
				"Violation description: "+
				"Each time "+ activityA + " occurs then" + (activityB != null ? activityB : "") +
				" occurs afterwards\n" ;
	}
	private static String generateChainSuccessionMessage(String activityA, String activityB, String[] fields) {
		return generateCommonMessage("Chain Succession", fields) +
				"Violation description: "+
				activityA + " and " + (activityB != null ? activityB : "") +
				" occur together if and only if the latter immediately follows the former\n" ;
	}
	private static String generateCoExistenceMessage(String activityA, String activityB, String[] fields) {
		return generateCommonMessage("Co-Existence", fields) +
				"Violation description: "+
				activityA + " and " + (activityB != null ? activityB : "") +
				" occur together\n" ;
	}
	private static String generatePrecedenceMessage(String activityA, String activityB, String[] fields) {
		return generateCommonMessage("Precedence", fields) +
				"Violation description: "+
				activityA + " occurs if preceded by" + (activityB != null ? activityB : "") 
				+ "\n" ;
	}
	private static String generateRespondedExistenceMessage(String activityA, String activityB, String[] fields) {
		return generateCommonMessage("Responded Existence", fields) +
				"Violation description: "+
				"if "+ activityA + " occurs then" + (activityB != null ? activityB : "") +
				" occurs as well\n" ;
	}
	private static String generateResponseMessage(String activityA, String activityB, String[] fields) {
		return generateCommonMessage("Response", fields) +
				"Violation description: "+
				"if "+ activityA + " occurs then" + (activityB != null ? activityB : "") +
				" occurs after "+ activityA + "\n" ;
	}
	private static String generateSuccessionMessage(String activityA, String activityB, String[] fields) {
		return generateCommonMessage("Succession", fields) +
				"Violation description: "+
				activityA + " occurs if and only if it is followed by" + (activityB != null ? activityB : "") +
				"\n" ;
	}



	// fine



	private static String generateAltroTipoViolazioneMessage(String activityA, String activityB) {
		// Logica per generare il messaggio per un altro tipo di violazione utilizzando 'activityA' e 'activityB'...
		return "Violation not recognized\n";
	}

	private static String generateCommonMessage(String violationType, String... fields) {
		StringBuilder message = new StringBuilder("Violation Type: " + violationType + "\n");
		String[] labels = {"Activity Name","Diagram Name", "UML Element Type", "UML Element Name", 
				"Property Name", "Property Value", "RelationShip From", "RelationShip To"};

		for (int i = 0; i < labels.length; i++) {
			String fieldValue = getField(fields, i + 1);
			if (!fieldValue.isEmpty()) {
				message.append(labels[i]).append(": ").append(fieldValue).append("\n");
			}
		}

		return message.toString();
	}


	private static String[] extractActivityParts(String activities) {
		return activities.replaceAll("\\[|\\]", "").split(",");
	}
}
