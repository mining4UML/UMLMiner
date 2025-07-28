package com.uniba.mining.sdmetrics;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import org.dom4j.Document;

import com.uniba.mining.logging.LogStreamer;
import com.uniba.mining.tasks.exportdiag.DiagramInfo;
import com.uniba.mining.utils.Application;
import com.vp.plugin.diagram.IDiagramUIModel;

/**
 * Utility class for extracting, converting, and analyzing UML diagrams
 * using the SDMetrics toolset.
 *
 * <p>This class orchestrates the process of:
 * <ul>
 *   <li>Exporting a diagram from Visual Paradigm as XML</li>
 *   <li>Converting the XML to XMI format compatible with SDMetrics</li>
 *   <li>Executing SDMetrics via an external process</li>
 *   <li>Summarizing and interpreting the results</li>
 * </ul>
 *
 * <p>The class supports multiple UML diagram types (e.g., Class, Use Case),
 * adapting output file parsing and metric summary accordingly.
 *
 * <p>Dependencies include SDMetrics JAR and configuration files (metamodel and metrics definitions),
 * which are automatically loaded or copied if missing.
 *
 * <p>Typical usage:
 * <pre>
 *   String feedback = RunSDMetrics.readSdmetricsOutput(diagram);
 * </pre>
 *
 * @author pasqualeardimento
 */


public class RunSDMetrics {

	private static void calculateMetrics(IDiagramUIModel diagram) throws IOException, InterruptedException {
		// 1. Estrai XML dal diagramma
		Document diagramAsXML = DiagramInfo.exportAsXML(diagram);
		System.out.println(diagramAsXML.asXML());

		// 2. Definisci percorso file XMI temporaneo
		String convertedXmi = "converted_model.xmi";


		// 3. Converti da XML a XMI
		try {
			System.out.println("Conversione del file XML in XMI...");
			VPXmlToXMIConverter.convertFromDocument(diagramAsXML, convertedXmi);
			System.out.println("Conversione completata: " + convertedXmi);
		} catch (Exception e) {
			System.err.println("Errore durante la conversione XML→XMI: " + e.getMessage());
			return;
		}

		// 4. Parametri SDMetrics centralizzati
		Path sdmetricsDir = LogStreamer.getSDMetricsDirectory(); // o PathManager.getSDMetricsDirectory()
		Path outputPrefix = LogStreamer.getSDMetricsOutputDirectory().resolve("output");

		//String sdmetricsJar = sdmetricsDir.resolve("SDMetrics.jar").toString();
		Path sdmetricsJarPath = LogStreamer.getSDMetricsDirectory().resolve("SDMetrics.jar");

		if (!Files.exists(sdmetricsJarPath)) {
			System.err.println("SDMetrics.jar not found in: " + sdmetricsJarPath);
			// Puoi decidere se interrompere o provare a copiarlo da una risorsa embedded
			copyJarFromResources(sdmetricsJarPath);
		}
		copyFileIfMissing("metamodel2.xml");
		copyFileIfMissing("metrics2.xml");

		String metamodel = sdmetricsDir.resolve("metamodel2.xml").toString();
		String metrics = sdmetricsDir.resolve("metrics2.xml").toString();
		convertedXmi = LogStreamer.getXMIDirectory().resolve("converted_model.xmi").toString();

		// Costruzione comando
		ProcessBuilder builder = new ProcessBuilder(
				"java", "-jar", sdmetricsJarPath.toString(),
				"-xmi", convertedXmi,
				"-meta", metamodel,
				"-metrics", metrics,
				"-model", "-stats", "-f", "csv",
				outputPrefix.toString()
				);

		builder.redirectErrorStream(true);
		Process process = builder.start();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println("[SDMetrics] " + line);
			}
		}

		int exitCode = process.waitFor();
		if (exitCode == 0) {
			System.out.println("SDMetrics successfully completed. Results in: output/");
		} else {
			System.err.println("SDMetrics finished with error. Error code: " + exitCode);
		}
	}
	public static String readSdmetricsOutput(IDiagramUIModel diagram) {
		// Calcolo delle metriche
		try {
			calculateMetrics(diagram);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return "Error running SDMetrics: " + e.getMessage();
		}

		StringBuilder content = new StringBuilder();

		Path outputPrefix = LogStreamer.getSDMetricsOutputDirectory();

		// Identifica tipo di diagramma (es. "Class", "UseCase", ecc.)
		String type = diagram.getType(); // es: "ClassDiagram", "UseCaseDiagram"
		String diagramType = type.replace("Diagram", ""); // es: "Class", "UseCase"

		// 1. Riepilogo metriche aggregate (outputDS_<Tipo>.csv)
		Path dsPath = outputPrefix.resolve("outputDS_" + diagramType + ".csv");
		String summaryStats = summarizeSdmetricsStats(dsPath);
		content.append("### SDMetrics Summary (").append(diagramType).append(") ###\n")
		.append(summaryStats).append("\n");

		// 2. Riepilogo metriche per entità (output_<Tipo>.csv)
		Path entityPath = outputPrefix.resolve("output_" + diagramType + ".csv");
		String summaryEntities;

		switch (diagramType) {
		case "Class":
			summaryEntities = summarizePerClassMetrics(entityPath);
			content.append("### Per-Class Metric Summary ###\n");
			break;
		case "UseCase":
			summaryEntities = summarizePerUseCaseMetrics(entityPath);
			content.append("### Per-UseCase Metric Summary ###\n");
			break;
		default:
			summaryEntities = "Metric summary for diagram type '" + diagramType + "' is not yet implemented.\n";
			content.append("### Per-" + diagramType + " Metric Summary ###\n");
			break;
		}

		content.append(summaryEntities).append("\n");


		//deleteConvertedXmi();
		return content.toString();
	}



	private static String summarizeSdmetricsStats(Path csvFilePath) {
		StringBuilder summary = new StringBuilder();
		summary.append("The following use case metrics were computed using SDMetrics:\n\n");

		try (BufferedReader reader = Files.newBufferedReader(csvFilePath)) {
			String header = reader.readLine(); // Read the header
			if (header == null || !header.startsWith("Name,")) {
				return "Invalid SDMetrics file format.";
			}

			String line;
			int count = 0;
			while ((line = reader.readLine()) != null && count < 10) {
				String[] tokens = line.split(",", -1); // include empty trailing tokens
				if (tokens.length >= 8) {
					String name = tokens[0].trim();
					String domain = tokens[1].trim();
					String description = tokens[2].trim();
					String mean = tokens[3].trim();
					String median = tokens[4].trim();
					String stddev = tokens[5].trim();
					String min = tokens[6].trim();
					String max = tokens[7].trim();

					summary.append(String.format("- %s (domain: %s)%n", name, domain));
					if (!description.isEmpty()) {
						summary.append(String.format("  Description: %s%n", description));
					}
					summary.append(String.format("  Stats: Mean = %s, Median = %s, StdDev = %s, Min = %s, Max = %s%n%n",
							mean, median, stddev, min, max));
					count++;
				}
			}

			if (count == 0) {
				summary.append("No significant metrics found.\n");
			}

		} catch (IOException e) {
			return "Error reading SDMetrics summary: " + e.getMessage();
		}

		return summary.toString();
	}


	private static String summarizePerClassMetrics(Path csvFilePath) {
		StringBuilder summary = new StringBuilder();
		summary.append("Class-level design metrics from SDMetrics:\n\n");

		try (BufferedReader reader = Files.newBufferedReader(csvFilePath)) {
			String headerLine = reader.readLine(); // intestazione
			if (headerLine == null) {
				return "No content in output_Class.csv.";
			}

			String[] headers = headerLine.split(",");

			String line;
			int classCount = 0;
			while ((line = reader.readLine()) != null && classCount < 10) {
				String[] values = line.split(",");
				if (values.length != headers.length) continue;

				String className = values[0].trim();
				StringBuilder classMetrics = new StringBuilder();

				for (int i = 1; i < headers.length; i++) {
					String metric = headers[i].trim();
					String value = values[i].trim();

					classMetrics.append(metric).append("=").append(value).append(", ");
				}

				if (classMetrics.length() > 0) {
					// Rimuovi ultima virgola e spazio
					classMetrics.setLength(classMetrics.length() - 2);
					summary.append("Class: ").append(className).append("\n  ")
					.append(classMetrics).append("\n\n");
					classCount++;
				}
			}

			if (classCount == 0) {
				summary.append("All class metrics are zero or not significant.\n");
			}

		} catch (IOException e) {
			return "Error reading class metrics: " + e.getMessage();
		}

		return summary.toString();
	}

	private static boolean deleteConvertedXmi() {
		Path convertedXmi = LogStreamer.getXMIDirectory().resolve("converted_model.xmi");

		try {
			Files.deleteIfExists(convertedXmi);
			System.out.println("File XMI eliminato: " + convertedXmi);
			return true;
		} catch (IOException e) {
			System.err.println("Errore durante l'eliminazione del file XMI: " + e.getMessage());
			return false;
		}
	}

	private static void copyJarFromResources(Path destinationPath) {
		try {
			// Ottieni la directory assoluta del plugin
			String rootPath = Application.getPluginInfo("UMLMiner").getPluginDir().getAbsolutePath();

			// Costruisci il percorso del file da copiare
			File jarFile = new File(rootPath + File.separator + "lib" + File.separator + "SDMetrics.jar");

			if (!jarFile.exists()) {
				System.err.println("SDMetrics.jar non trovato in: " + jarFile.getAbsolutePath());
				return;
			}

			// Assicurati che la directory di destinazione esista
			Files.createDirectories(destinationPath.getParent());

			// Copia il file nella destinazione
			Files.copy(jarFile.toPath(), destinationPath);
			System.out.println("SDMetrics.jar copiato in: " + destinationPath);

		} catch (IOException e) {
			System.err.println("Errore durante la copia di SDMetrics.jar: " + e.getMessage());
		}
	}

	private static void copyFileIfMissing(String fileName) {
		try {
			String rootPath = Application.getPluginInfo("UMLMiner").getPluginDir().getAbsolutePath();
			File sourceFile = new File(rootPath + File.separator + "assets" 
					+ File.separator + "sdmetrics"+ File.separator + fileName);
			Path destinationPath = LogStreamer.getSDMetricsDirectory().resolve(fileName);

			if (Files.exists(destinationPath)) {
				return; // Già presente
			}

			if (!sourceFile.exists()) {
				System.err.println(fileName + " not found in: " + sourceFile.getAbsolutePath());
				return;
			}

			Files.createDirectories(destinationPath.getParent());
			Files.copy(sourceFile.toPath(), destinationPath);
			System.out.println(fileName + " copied in: " + destinationPath);

		} catch (IOException e) {
			System.err.println("Errore durante la copia di " + fileName + ": " + e.getMessage());
		}
	}

	private static String summarizePerUseCaseMetrics(Path csvFilePath) {
		StringBuilder summary = new StringBuilder();
		summary.append("UseCase-level design metrics from SDMetrics:\n\n");

		try (BufferedReader reader = Files.newBufferedReader(csvFilePath)) {
			String headerLine = reader.readLine(); // intestazione
			if (headerLine == null || !headerLine.startsWith("Name,")) {
				return "Invalid SDMetrics file format for UseCase metrics.";
			}

			String[] headers = headerLine.split(",");
			String line;
			int useCaseCount = 0;

			while ((line = reader.readLine()) != null && useCaseCount < 10) {
				String[] values = line.split(",");
				if (values.length != headers.length) continue;

				String useCaseName = values[0].trim();
				StringBuilder useCaseMetrics = new StringBuilder();

				for (int i = 1; i < headers.length; i++) {
					String metricName = headers[i].trim();
					String metricValue = values[i].trim();
					useCaseMetrics.append(metricName).append("=").append(metricValue).append(", ");
				}

				if (useCaseMetrics.length() > 0) {
					useCaseMetrics.setLength(useCaseMetrics.length() - 2); // rimuove ", "
					summary.append("UseCase: ").append(useCaseName).append("\n  ")
					.append(useCaseMetrics).append("\n\n");
					useCaseCount++;
				}
			}

			if (useCaseCount == 0) {
				summary.append("All use case metrics are zero or not significant.\n");
			}

		} catch (IOException e) {
			return "Error reading UseCase metrics: " + e.getMessage();
		}

		return summary.toString();
	}

}