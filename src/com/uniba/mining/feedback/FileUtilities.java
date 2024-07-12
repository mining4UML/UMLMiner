package com.uniba.mining.feedback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtilities {

	//    public static String loadFileContent(String diagramId, Path directory) throws IOException {
	//        // Costruisce il percorso completo del file
	//        Path filePath = directory.resolve(diagramId + ".txt");
	//
	//        // Legge tutto il contenuto del file in una stringa
	//        String fileContent = Files.readString(filePath);
	//
	//        return fileContent;
	//    }


	/**
	 * 
	 * @param diagramId
	 * @param directory
	 * @return null if does not exist
	 * @throws IOException
	 */
	public static String loadFileContent(String diagramId, Path directory) throws IOException {

		if (doesFileExist( diagramId, directory)) {

			// Costruisce il percorso completo del file
			Path filePath = directory.resolve(diagramId + ".txt");

			// Legge tutto il contenuto del file in una stringa
			String fileContent = readFileContent(filePath); 

			return fileContent;
		}
		else
			return null;
	}

	private static String readFileContent(Path filePath) {
		String fileContent = null;
		try {
			System.out.println("Reading file: " + filePath);
			byte[] fileBytes = Files.readAllBytes(filePath);
			System.out.println("File bytes length: " + fileBytes.length);

			// Convert byte array to string using UTF-8 encoding
			fileContent = new String(fileBytes, StandardCharsets.UTF_8);
			System.out.println("File content: " + fileContent);

		} catch (IOException e) {
			ErrorUtils.showDetailedErrorMessage(e);
			System.err.println("IOException: " + e.getMessage());
		}
		return fileContent;
	}




	public static boolean doesFileExist(String diagramId, Path directory) {
		// Costruisce il percorso completo del file
		Path filePath = directory.resolve(diagramId + ".txt");

		// Verifica se il file esiste
		return Files.exists(filePath);
	}

}