package com.uniba.mining.utils;

import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.io.OutputFormat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.nio.file.*;


public class exportXMLCustomized {

	// Metodo principale che rimuove nodi, elimina attributi specifici e aggiunge nuovi attributi
	public Document getCustomizedXML(String SourceXmlName, 
			String DestinationXmlName, File path, boolean save) throws Exception {
		// Carica il file XML di input
		SAXReader reader = new SAXReader();
		File inputFile = new File(path+"/"+SourceXmlName);
		Document doc = reader.read(inputFile);

		// Rimuovi nodi specificati
		removeNodes(doc, "//ProjectInfo");
		removeNodes(doc, "//Diagrams");
		removeNodes(doc, "//DataType");

		List<String> attributes = List.of("BacklogActivityId", "BusinessKeyMutable", 
				"BusinessModel", "ConnectToCodeModel", "Documentation_plain", "QualityReason_IsNull",
				"QualityScore", "UserIDLastNumericValue");

		// Rimuovi attributi da nodi specifici
		removeAttributes(doc, "//Class", attributes);
		removeAttributes(doc, "//Operation", attributes);
		removeAttributes(doc, "//Project", List.of("Description", "CommentTableSortAscending",
				"CommentTableSortColumn", "Description", "TextualAnalysisHighlightOptionCaseSensitive"));
		removeAttributes(doc, "//ModelRelationshipContainer", attributes);

		// Crea mappa ID-Nome per le classi
		Map<String, String> classNames = createClassIdToNameMap(doc);

		// Aggiungi attributi fromName e toName ai nodi
		addAttributesToNodes(doc, "Generalization", classNames, "MasterView");
		addAttributesToNodes(doc, "Usage", classNames, "MasterView");
		addAttributesToNodes(doc, "Dependency", classNames, "MasterView");
		addAttributesToNodes(doc, "Realization", classNames, "MasterView");

		// Salva il documento aggiornato
		if(save)
			saveDocument(doc, path+"/"+DestinationXmlName+".xml");

		// cancella i file generati da VP 
		deleteFilesExcept(path.toString(), DestinationXmlName+".xml");
		System.out.println("File XML modificato con successo, nodi specificati rimossi e attributi aggiunti.");
		return doc;
	}

	// Metodo per creare una mappa degli ID delle classi con i loro nomi
	private Map<String, String> createClassIdToNameMap(Document doc) {
		Map<String, String> classNames = new HashMap<>();
		List<Node> classNodes = doc.selectNodes("//Class");
		for (Node classNode : classNodes) {
			Element classElement = (Element) classNode;
			String id = classElement.attributeValue("Id");
			String name = classElement.attributeValue("Name");
			if (id != null && name != null) {
				classNames.put(id, name);
			}
		}
		System.out.println("Mappa ID-Nome delle classi creata con successo.");
		return classNames;
	}

	// Metodo per aggiungere attributi fromName e toName ai nodi specificati (ad es., <Generalization>),
	// con un controllo per evitare l'operazione se il nodo ha come padre il nodo specificato
	private void addAttributesToNodes(Document doc, String nodeName, Map<String, String> classNames, String parentNodeName) {
		// Seleziona tutti i nodi con il nome specificato
		List<Node> nodes = doc.selectNodes("//" + nodeName);

		for (Node node : nodes) {
			Element element = (Element) node;

			// Verifica se il nodo ha come padre il nodo con il nome specificato
			Element parent = element.getParent();
			if (parent != null && parent.getName().equals(parentNodeName)) {
				// Se il nodo ha come padre il nodo specificato, salta l'operazione
				continue;
			}

			// Recupera gli attributi 'From' e 'To' e i relativi nomi
			String fromId = element.attributeValue("From");
			String toId = element.attributeValue("To");

			// Recupera i nomi delle classi usando gli ID e aggiungi gli attributi
			String fromName = classNames.getOrDefault(fromId, "Unknown");
			String toName = classNames.getOrDefault(toId, "Unknown");

			// Aggiungi gli attributi 'fromName' e 'toName' al nodo
			element.addAttribute("fromName", fromName);
			element.addAttribute("toName", toName);
		}

		System.out.println("Attributi fromName e toName aggiunti ai nodi <" + nodeName + ">.");
	}


	// Metodo per rimuovere nodi specificati
	private void removeNodes(Document doc, String nodeName) {
		List<Node> nodes = doc.selectNodes(nodeName);
		for (Node node : nodes) {
			if (node instanceof Element) {
				Element element = (Element) node;
				element.getParent().remove(element);
				System.out.println("Nodo rimosso: " + element.getName());
			}
		}
	}

	// Metodo per rimuovere attributi specifici da un XPath
	private static void removeAttributes(Document doc, String xpathExpression, List<String> attributeNames) {
		List<Node> nodes = doc.selectNodes(xpathExpression);
		for (Node node : nodes) {
			Element element = (Element) node;
			for (String attributeName : attributeNames) {
				Attribute attribute = element.attribute(attributeName);
				if (attribute != null) {
					attribute.detach();
				}
			}
		}
	}

	// Metodo per salvare il documento modificato
	private static void saveDocument(Document doc, String outputPath) {
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			XMLWriter writer = new XMLWriter(new FileWriter(outputPath), format);
			writer.write(doc);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void deleteFilesExcept(String directoryPath, String excludedFileName) {
		try {
			Files.walk(Paths.get(directoryPath))
			.filter(Files::isRegularFile) // Filtra solo i file regolari
			.filter(path -> !path.getFileName().toString().equals(excludedFileName)) // Esclude il file specificato
			.forEach(path -> {
				try {
					Files.delete(path); // Elimina il file
					System.out.println("File eliminato: " + path.getFileName());
				} catch (IOException e) {
					System.err.println("Errore nell'eliminazione del file: " + path.getFileName());
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			System.err.println("Errore durante l'eliminazione dei file nella cartella: " + directoryPath);
			e.printStackTrace();
		}
	}

}
