package com.uniba.mining.sdmetrics;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
				"-model", "-stats", "-rules", "-f", "csv",
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
		try {
			calculateMetrics(diagram);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return "Error running SDMetrics: " + e.getMessage();
		}

		Path outputPrefix = LogStreamer.getSDMetricsOutputDirectory();
		String type = diagram.getType();
		String diagramType = type.replace("Diagram", "");

		StringBuilder report = new StringBuilder();
		Map<String, String> labelMap = MetricLabels.getDefaultLabels();

		List<String> subtypes = diagramType.equals("UseCase") ? List.of("Actor", "UseCase") : List.of(diagramType);

		for (String subtype : subtypes) {
			Path summaryPath = outputPrefix.resolve("outputDS_" + subtype + ".csv");
			Path entityPath = outputPrefix.resolve("output_" + subtype + ".csv");
			Path rulesPath = outputPrefix.resolve("outputRULES_" + subtype + ".csv");

			// Parsing metric summaries + element-level metrics
			if (Files.exists(summaryPath)) {
				try {
					List<SdMetricsReportFormatter.MetricSummary> summaries =
						SdMetricsReportFormatter.parseSummaryCsv(summaryPath, labelMap);

					List<SdMetricsReportFormatter.ElementMetricRow> details =
						Files.exists(entityPath)
							? SdMetricsReportFormatter.parseElementLevelCsv(entityPath)
							: Collections.emptyList();

					report.append(SdMetricsReportFormatter.formatReport(subtype, summaries, details)).append("\n");
				} catch (IOException e) {
					report.append("Error reading SDMetrics results for ").append(subtype).append(": ").append(e.getMessage()).append("\n");
				}
			}

			// Parsing and formatting rule violations
			if (Files.exists(rulesPath)) {
				try {
					List<SdMetricsReportFormatter.RuleViolation> violations =
						SdMetricsReportFormatter.parseViolationsCsv(rulesPath);
					String violationReport =
						SdMetricsReportFormatter.formatViolations(subtype, violations);
					report.append(violationReport).append("\n");
				} catch (IOException e) {
					report.append("Error reading rule violations for ").append(subtype).append(": ").append(e.getMessage()).append("\n");
				}
			}
		}

		return report.toString();
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

}