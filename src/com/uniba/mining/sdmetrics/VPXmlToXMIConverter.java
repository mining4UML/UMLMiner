package com.uniba.mining.sdmetrics;

import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.*;

import com.uniba.mining.logging.LogStreamer;

/**
 * Converts UML diagrams exported from Visual Paradigm in XML format 
 * into XMI files compatible with the SDMetrics analysis tool.
 * 
 * <p>The class supports both class diagrams and use case diagrams. 
 * It automatically detects the diagram type from the input document 
 * and generates the appropriate XMI structure.
 * 
 * <p>The generated XMI conforms to the metamodel used by SDMetrics, 
 * enabling automated metric computation and quality analysis.
 * 
 * <p>A logging mechanism is integrated to trace the conversion process and capture errors.
 *
 * <p>Typical usage:
 * <pre>
 *   String xmiPath = VPXmlToXMIConverter.convertFromDocument(xmlDocument, "output.xmi");
 * </pre>
 *
 * @author pasqualeardimento
 */


public class VPXmlToXMIConverter {

	private static final Logger logger = Logger.getLogger(VPXmlToXMIConverter.class.getName());

	static {
		try {
			File logDir = LogStreamer.getSDMetricsDirectory().toFile();
			if (!logDir.exists()) logDir.mkdirs();
			FileHandler fh = new FileHandler(new File(logDir, "VPXmlToXMIConverter.log").getAbsolutePath());
			fh.setFormatter(new SimpleFormatter());
			logger.addHandler(fh);
			logger.setUseParentHandlers(false);
		} catch (IOException e) {
			System.err.println("Errore nella configurazione del logger: " + e.getMessage());
		}
	}

	public static String convert(String inputXmlPath, String outputXmiFileName) throws Exception {
		logger.info("Inizio conversione del file: " + inputXmlPath);
		SAXReader reader = new SAXReader();
		Document doc = reader.read(new File(inputXmlPath));
		return convertFromDocument(doc, outputXmiFileName);
	}

	public static String convertFromDocument(Document doc, String outputXmiFileName) throws Exception {
		List<Node> classNodes = doc.selectNodes("//Class[not(@Idref)]");
		List<Node> useCaseNodes = doc.selectNodes("//UseCase[not(@Idref)]");

		if (!classNodes.isEmpty()) {
			return convertClassDiagram(doc, outputXmiFileName);
		} else if (!useCaseNodes.isEmpty()) {
			return convertUseCaseDiagram(doc, outputXmiFileName);
		} else {
			throw new Exception("Il documento non contiene né classi né casi d'uso.");
		}
	}

	private static String convertClassDiagram(Document doc, String outputXmiFileName) throws Exception {
		Path xmiPath = LogStreamer.getXMIDirectory().resolve(outputXmiFileName);
		File xmiFile = xmiPath.toFile();
		xmiFile.getParentFile().mkdirs();

		Map<String, String> classIdToXmiId = new HashMap<>();
		Map<String, String> subclassToSuperclass = new HashMap<>();

		List<Element> genElements = doc.selectNodes("//ModelRelationshipContainer[@Name='Generalization']//Generalization");
		for (Element gen : genElements) {
			String subId = gen.attributeValue("From");
			String superId = gen.attributeValue("To");
			if (subId != null && superId != null) {
				subclassToSuperclass.put(subId, superId);
			}
		}

		List<Node> classNodes = doc.selectNodes("//Class[not(@Idref)]");
		for (Node node : classNodes) {
			if (!(node instanceof Element)) continue;
			Element classEl = (Element) node;
			String id = classEl.attributeValue("Id");
			String xmiId = UUID.randomUUID().toString();
			classIdToXmiId.put(id, xmiId);
		}

		try (PrintWriter writer = new PrintWriter(new FileWriter(xmiFile))) {
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.println("<xmi:XMI xmlns:xmi=\"http://www.omg.org/spec/XMI/2.1\" xmlns:uml=\"http://www.omg.org/spec/UML/20090901\">");
			writer.println("  <uml:Model xmi:type=\"uml:Model\" name=\"VPConvertedModel\">");

			for (Node node : classNodes) {
				if (!(node instanceof Element)) continue;
				Element classEl = (Element) node;
				String id = classEl.attributeValue("Id");
				String name = classEl.attributeValue("Name", "UnnamedClass");
				String xmiId = classIdToXmiId.get(id);
				String isAbstract = classEl.attributeValue("Abstract", "false");
				String isLeaf = classEl.attributeValue("Leaf", "false");
				String visibility = classEl.attributeValue("Visibility", "public");

				writer.printf("    <packagedElement xmi:type=\"uml:Class\" xmi:id=\"%s\" name=\"%s\" visibility=\"%s\" isAbstract=\"%s\" isLeaf=\"%s\">%n",
						xmiId, name, visibility, isAbstract, isLeaf);

				Element modelChildren = classEl.element("ModelChildren");
				if (modelChildren != null) {
					for (Iterator<?> it = modelChildren.elementIterator("Attribute"); it.hasNext(); ) {
						Element attr = (Element) it.next();
						String attrName = attr.attributeValue("Name", "unnamedAttribute");
						String attrVisibility = attr.attributeValue("Visibility", "private");
						String type = attr.attributeValue("Type");
						if ((type == null || type.isEmpty()) && attr.element("Type") != null) {
							Element typeEl = attr.element("Type");
							type = typeEl.attributeValue("Name", "String");
						}
						if (type == null || type.isEmpty()) type = "String";
						String attrId = UUID.randomUUID().toString();
						writer.printf("      <ownedAttribute xmi:id=\"%s\" name=\"%s\" visibility=\"%s\">%n",
								attrId, attrName, attrVisibility);
						writer.printf("        <type xmi:type=\"uml:PrimitiveType\" href=\"http://www.omg.org/spec/UML/20090901/PrimitiveTypes.xmi#%s\"/>%n", type);
						writer.println("      </ownedAttribute>");
					}
				}

				if (subclassToSuperclass.containsKey(id)) {
					String superId = subclassToSuperclass.get(id);
					String superXmi = classIdToXmiId.get(superId);
					if (superXmi != null) {
						writer.printf("      <generalization xmi:id=\"%s\" general=\"%s\"/>%n", UUID.randomUUID(), superXmi);
					} else {
						logger.warning("Generalizzazione ignorata: superclasse non trovata per ID=" + superId);
					}
				}

				writer.println("    </packagedElement>");
			}

			writer.println("  </uml:Model>");
			writer.println("</xmi:XMI>");
		}

		logger.info("Conversione diagramma delle classi completata. File salvato in: " + xmiFile.getAbsolutePath());
		return xmiFile.getAbsolutePath();
	}

//	private static String convertUseCaseDiagram(Document doc, String outputXmiFileName) throws Exception {
//		Path xmiPath = LogStreamer.getXMIDirectory().resolve(outputXmiFileName);
//		File xmiFile = xmiPath.toFile();
//		xmiFile.getParentFile().mkdirs();
//
//		Map<String, String> idToXmiId = new HashMap<>();
//
//		try (PrintWriter writer = new PrintWriter(new FileWriter(xmiFile))) {
//			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//			writer.println("<xmi:XMI xmlns:xmi=\"http://www.omg.org/spec/XMI/2.1\" xmlns:uml=\"http://www.omg.org/spec/UML/20090901\">");
//			writer.println("  <uml:Model xmi:type=\"uml:Model\" name=\"VPConvertedModel\">");
//
//			List<Node> actors = doc.selectNodes("//Actor[not(@Idref)]");
//			for (Node actor : actors) {
//			    if (!(actor instanceof Element)) continue;
//			    Element el = (Element) actor;
//			    String id = el.attributeValue("Id");
//			    String name = escapeXml(el.attributeValue("Name", "UnnamedActor"));
//			    String xmiId = UUID.randomUUID().toString();
//
//			    // Aggiunta di attributi utili
//			    String isAbstract = el.attributeValue("Abstract", "false");
//			    String isLeaf = el.attributeValue("Leaf", "false");
//			    String visibility = el.attributeValue("Visibility", "public");
//
//			    idToXmiId.put(id, xmiId);
//
//			    writer.printf(
//			        "    <packagedElement xmi:type=\"uml:Actor\" xmi:id=\"%s\" name=\"%s\" visibility=\"%s\" isAbstract=\"%s\" isLeaf=\"%s\"/>%n",
//			        xmiId, name, visibility, isAbstract, isLeaf
//			    );
//			}
//
//
//			List<Node> usecases = doc.selectNodes("//UseCase[not(@Idref)]");
//			for (Node uc : usecases) {
//			    if (!(uc instanceof Element)) continue;
//			    Element el = (Element) uc;
//			    String id = el.attributeValue("Id");
//			    String name = escapeXml(el.attributeValue("Name", "UnnamedUseCase"));
//			    String xmiId = UUID.randomUUID().toString();
//
//			    // Attributi addizionali opzionali
//			    String isAbstract = el.attributeValue("Abstract", "false");
//			    String isLeaf = el.attributeValue("Leaf", "false");
//			    String visibility = el.attributeValue("Visibility", "public");
//
//			    idToXmiId.put(id, xmiId);
//
//			    writer.printf(
//			        "    <packagedElement xmi:type=\"uml:UseCase\" xmi:id=\"%s\" name=\"%s\" visibility=\"%s\" isAbstract=\"%s\" isLeaf=\"%s\"/>%n",
//			        xmiId, name, visibility, isAbstract, isLeaf
//			    );
//			}
//
//
////			// Include (uml:Include)
////			List<Node> includes = doc.selectNodes("//ModelRelationshipContainer[@Name='Include']//Include");
////			for (Node inc : includes) {
////				Element el = (Element) inc;
////				String from = el.attributeValue("From");
////				String to = el.attributeValue("To");
////				String fromXmi = idToXmiId.get(from); // includingCase
////				String toXmi = idToXmiId.get(to);     // addition
////				if (fromXmi != null && toXmi != null) {
////					writer.printf("    <packagedElement xmi:type=\"uml:Include\" xmi:id=\"%s\" addition=\"%s\" includingCase=\"%s\"/>%n",
////							UUID.randomUUID(), toXmi, fromXmi);
////				}
////			}
//			
//			// Relazioni Include
//			List<Node> includes = doc.selectNodes("//ModelRelationshipContainer[@Name='Include']//Include");
//			for (Node inc : includes) {
//			    Element el = (Element) inc;
//			    String from = el.attributeValue("From"); // includingCase
//			    String to = el.attributeValue("To");     // included use case
//
//			    String fromXmiId = idToXmiId.get(from);
//			    String toXmiId = idToXmiId.get(to);
//
//			    if (fromXmiId != null && toXmiId != null) {
//			        String includeId = UUID.randomUUID().toString();
//			        writer.printf(
//			            "    <packagedElement xmi:type=\"uml:Include\" xmi:id=\"%s\" addition=\"%s\" includingCase=\"%s\"/>%n",
//			            includeId, toXmiId, fromXmiId
//			        );
//			    }
//			}
//
//
//
////			// Extend (uml:Extend)
////			List<Node> extendsList = doc.selectNodes("//ModelRelationshipContainer[@Name='Extend']//Extend");
////			for (Node ext : extendsList) {
////				Element el = (Element) ext;
////				String from = el.attributeValue("From"); // extending
////				String to = el.attributeValue("To");     // extended
////				String fromXmi = idToXmiId.get(from);
////				String toXmi = idToXmiId.get(to);
////				if (fromXmi != null && toXmi != null) {
////					writer.printf("    <packagedElement xmi:type=\"uml:Extend\" xmi:id=\"%s\" extendedCase=\"%s\" extension=\"%s\"/>%n",
////							UUID.randomUUID(), toXmi, fromXmi);
////				}
////			}
//			
//			// Extend (SDMetrics: usecaseextend)
//			List<Node> extendsList = doc.selectNodes("//ModelRelationshipContainer[@Name='Extend']//Extend");
//			for (Node ext : extendsList) {
//			    Element el = (Element) ext;
//			    String from = el.attributeValue("From"); // extending use case
//			    String to = el.attributeValue("To");     // extended use case
//
//			    String fromXmi = idToXmiId.get(from);
//			    String toXmi = idToXmiId.get(to);
//
//			    if (fromXmi != null && toXmi != null) {
//			        writer.printf("    <usecaseextend extendedcase=\"%s\" usecaseextensionpoint=\"%s\"/>%n", toXmi, toXmi);
//			    }
//			}
//
//
//			// Associazioni tra attori e use case
//			List<Node> associations = doc.selectNodes("//ModelRelationshipContainer[@Name='Association']//Association");
//			System.out.println("\nnumero di associazioni attori-casi uso:" + associations.size() + "\n");
//
//			for (Node assocNode : associations) {
//				if (!(assocNode instanceof Element)) continue;
//				Element assocEl = (Element) assocNode;
//
//				Element fromEnd = assocEl.element("FromEnd");
//				Element toEnd = assocEl.element("ToEnd");
//
//				if (fromEnd != null && toEnd != null) {
//					Element fromAssoc = fromEnd.element("AssociationEnd");
//					Element toAssoc = toEnd.element("AssociationEnd");
//
//					if (fromAssoc != null && toAssoc != null) {
//						Element fromType = fromAssoc.element("Type");
//						Element toType = toAssoc.element("Type");
//
//						Element fromElement = null;
//						Element toElement = null;
//
//						if (fromType != null) {
//							fromElement = fromType.element("Actor");
//							if (fromElement == null) fromElement = fromType.element("UseCase");
//						}
//
//						if (toType != null) {
//							toElement = toType.element("UseCase");
//							if (toElement == null) toElement = toType.element("Actor");
//						}
//
//						if (fromElement != null && toElement != null) {
//							String fromIdRef = fromElement.attributeValue("Idref");
//							String toIdRef = toElement.attributeValue("Idref");
//
//							String fromXmiId = idToXmiId.get(fromIdRef);
//							String toXmiId = idToXmiId.get(toIdRef);
//
//							if (fromXmiId != null && toXmiId != null) {
//								String assocXmiId = UUID.randomUUID().toString();
//								writer.printf("    <packagedElement xmi:type=\"uml:Association\" xmi:id=\"%s\">%n", assocXmiId);
//								writer.printf("      <ownedEnd xmi:id=\"%s\" type=\"%s\"/>%n", UUID.randomUUID(), fromXmiId);
//								writer.printf("      <ownedEnd xmi:id=\"%s\" type=\"%s\"/>%n", UUID.randomUUID(), toXmiId);
//								writer.println("    </packagedElement>");
//							}
//						}
//					}
//				}
//			}
//
//
//
//
//
//			writer.println("  </uml:Model>");
//			writer.println("</xmi:XMI>");
//		}
//
//		logger.info("Conversione diagramma dei casi d'uso completata. File salvato in: " + xmiFile.getAbsolutePath());
//		return xmiFile.getAbsolutePath();
//	}
	private static String convertUseCaseDiagram(Document doc, String outputXmiFileName) throws Exception {
	    Path xmiPath = LogStreamer.getXMIDirectory().resolve(outputXmiFileName);
	    File xmiFile = xmiPath.toFile();
	    xmiFile.getParentFile().mkdirs();

	    Map<String, String> idToXmiId = new HashMap<>();

	    try (PrintWriter writer = new PrintWriter(new FileWriter(xmiFile))) {
	        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	        writer.println("<xmi:XMI xmlns:xmi=\"http://www.omg.org/spec/XMI/2.1\" xmlns:uml=\"http://www.omg.org/spec/UML/20090901\">");
	        writer.println("  <uml:Model xmi:type=\"uml:Model\" name=\"VPConvertedModel\">");

	        // Precarica tutti gli ID degli attori e dei casi d'uso
	        List<Node> allNodes = doc.selectNodes("//Actor[not(@Idref)] | //UseCase[not(@Idref)]");
	        for (Node node : allNodes) {
	            if (!(node instanceof Element)) continue;
	            Element el = (Element) node;
	            String id = el.attributeValue("Id");
	            if (id != null && !id.isEmpty()) {
	                idToXmiId.put(id, UUID.randomUUID().toString());
	            }
	        }

	        // Casi d'uso
	        Map<String, List<String>> usecaseToIncludes = new HashMap<>();
	        Map<String, List<String>> usecaseToExtends = new HashMap<>();
	        Map<String, String> usecaseToExtensionPoint = new HashMap<>();
	        Map<String, String> usecaseToEPId = new HashMap<>();
	        Map<String, List<String>> usecaseToIncludeIds = new HashMap<>();
	        Map<String, List<String>> usecaseToExtendIds = new HashMap<>();

	        // Include
	        List<Node> includes = doc.selectNodes("//ModelRelationshipContainer[@Name='Include']//Include");
	        for (Node inc : includes) {
	            Element el = (Element) inc;
	            String from = el.attributeValue("From");
	            String to = el.attributeValue("To");
	            String fromXmiId = idToXmiId.get(from);
	            String toXmiId = idToXmiId.get(to);
	            if (fromXmiId != null && toXmiId != null) {
	                usecaseToIncludes.computeIfAbsent(from, k -> new ArrayList<>()).add(toXmiId);
	            }
	        }

	        // Extend
	        List<Node> extendsList = doc.selectNodes("//ModelRelationshipContainer[@Name='Extend']//Extend");
	        for (Node ext : extendsList) {
	            Element el = (Element) ext;
	            String from = el.attributeValue("From");
	            String to = el.attributeValue("To");
	            String fromXmiId = idToXmiId.get(from);
	            String toXmiId = idToXmiId.get(to);
	            String extensionPointId = "ep_" + UUID.randomUUID();
	            if (fromXmiId != null && toXmiId != null) {
	                usecaseToExtends.computeIfAbsent(from, k -> new ArrayList<>()).add(toXmiId);
	                usecaseToExtensionPoint.put(from, extensionPointId);
	            }
	        }

	        // Scrittura UseCase
	        List<Node> usecases = doc.selectNodes("//UseCase[not(@Idref)]");
	        for (Node uc : usecases) {
	            if (!(uc instanceof Element)) continue;
	            Element el = (Element) uc;
	            String id = el.attributeValue("Id");
	            String name = escapeXml(el.attributeValue("Name", "UnnamedUseCase"));
	            String xmiId = idToXmiId.get(id);
	            String isAbstract = el.attributeValue("Abstract", "false");
	            String isLeaf = el.attributeValue("Leaf", "false");
	            String visibility = el.attributeValue("Visibility", "public");

	            writer.printf("    <packagedElement xmi:type=\"uml:UseCase\" xmi:id=\"%s\" name=\"%s\" visibility=\"%s\" isAbstract=\"%s\" isLeaf=\"%s\">%n",
	                    xmiId, name, visibility, isAbstract, isLeaf);

	            if (usecaseToExtensionPoint.containsKey(id)) {
	                writer.printf("      <ownedMember xmi:type=\"uml:ExtensionPoint\" xmi:id=\"%s\" name=\"autogenEP\"/>%n",
	                        usecaseToExtensionPoint.get(id));
	            }

	            List<String> includesList = usecaseToIncludes.getOrDefault(id, Collections.emptyList());
	            for (String includedId : includesList) {
	                writer.printf("      <include xmi:type=\"uml:Include\" xmi:id=\"%s\" addition=\"%s\"/>%n", UUID.randomUUID(), includedId);
	            }

	            List<String> extendsListIds = usecaseToExtends.getOrDefault(id, Collections.emptyList());
	            for (String extendedId : extendsListIds) {
	                String extPointId = usecaseToExtensionPoint.get(id);
	                writer.printf("      <extend xmi:type=\"uml:Extend\" xmi:id=\"%s\" extendedCase=\"%s\" extensionLocation=\"%s\"/>%n", UUID.randomUUID(), extendedId, extPointId);
	            }

	            writer.println("    </packagedElement>");
	        }

	        // Attori
	        List<Node> actors = doc.selectNodes("//Actor[not(@Idref)]");
	        for (Node actor : actors) {
	            if (!(actor instanceof Element)) continue;
	            Element el = (Element) actor;
	            String id = el.attributeValue("Id");
	            String name = escapeXml(el.attributeValue("Name", "UnnamedActor"));
	            String xmiId = idToXmiId.get(id);
	            String isAbstract = el.attributeValue("Abstract", "false");
	            String isLeaf = el.attributeValue("Leaf", "false");
	            String visibility = el.attributeValue("Visibility", "public");

	            writer.printf("    <packagedElement xmi:type=\"uml:Actor\" xmi:id=\"%s\" name=\"%s\" visibility=\"%s\" isAbstract=\"%s\" isLeaf=\"%s\"/>%n",
	                    xmiId, name, visibility, isAbstract, isLeaf);
	        }

	        // Associazioni
	        List<Node> associations = doc.selectNodes("//ModelRelationshipContainer[@Name='Association']//Association");
	        for (Node assocNode : associations) {
	            if (!(assocNode instanceof Element)) continue;
	            Element assocEl = (Element) assocNode;
	            Element fromEnd = assocEl.element("FromEnd");
	            Element toEnd = assocEl.element("ToEnd");
	            if (fromEnd != null && toEnd != null) {
	                Element fromAssoc = fromEnd.element("AssociationEnd");
	                Element toAssoc = toEnd.element("AssociationEnd");
	                if (fromAssoc != null && toAssoc != null) {
	                    Element fromType = fromAssoc.element("Type");
	                    Element toType = toAssoc.element("Type");
	                    Element fromElement = (fromType != null) ? fromType.element("Actor") : null;
	                    if (fromElement == null && fromType != null) fromElement = fromType.element("UseCase");
	                    Element toElement = (toType != null) ? toType.element("UseCase") : null;
	                    if (toElement == null && toType != null) toElement = toType.element("Actor");
	                    if (fromElement != null && toElement != null) {
	                        String fromIdRef = fromElement.attributeValue("Idref");
	                        String toIdRef = toElement.attributeValue("Idref");
	                        String fromXmiId = idToXmiId.get(fromIdRef);
	                        String toXmiId = idToXmiId.get(toIdRef);
	                        if (fromXmiId != null && toXmiId != null) {
	                            String assocXmiId = UUID.randomUUID().toString();
	                            writer.printf("    <packagedElement xmi:type=\"uml:Association\" xmi:id=\"%s\">%n", assocXmiId);
	                            writer.printf("      <ownedEnd xmi:id=\"%s\" xmi:type=\"uml:Property\" type=\"%s\" name=\"from\"/>%n", UUID.randomUUID(), fromXmiId);
	                            writer.printf("      <ownedEnd xmi:id=\"%s\" xmi:type=\"uml:Property\" type=\"%s\" name=\"to\"/>%n", UUID.randomUUID(), toXmiId);
	                            writer.println("    </packagedElement>");
	                        }
	                    }
	                }
	            }
	        }

	        writer.println("  </uml:Model>");
	        writer.println("</xmi:XMI>");
	    }

	    logger.info("Conversione diagramma dei casi d'uso completata. File salvato in: " + xmiFile.getAbsolutePath());
	    return xmiFile.getAbsolutePath();
	}



	private static String escapeXml(String input) {
		if (input == null) return "";
		return input.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&apos;");
	}

}
