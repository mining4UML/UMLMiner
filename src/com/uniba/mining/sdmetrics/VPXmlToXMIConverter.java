//package com.uniba.mining.sdmetrics;
//
//import org.dom4j.*;
//import org.dom4j.io.SAXReader;
//
//import java.io.*;
//import java.nio.file.Path;
//import java.util.*;
//import java.util.logging.*;
//
//import com.uniba.mining.logging.LogStreamer;
//
//public class VPXmlToXMIConverter {
//
//    private static final Logger logger = Logger.getLogger(VPXmlToXMIConverter.class.getName());
//
//    static {
//        try {
//            File logDir = LogStreamer.getSDMetricsDirectory().toFile();
//            if (!logDir.exists()) logDir.mkdirs();
//            FileHandler fh = new FileHandler(new File(logDir, "VPXmlToXMIConverter.log").getAbsolutePath());
//            fh.setFormatter(new SimpleFormatter());
//            logger.addHandler(fh);
//            logger.setUseParentHandlers(false);
//        } catch (IOException e) {
//            System.err.println("Errore nella configurazione del logger: " + e.getMessage());
//        }
//    }
//
//    public static String convert(String inputXmlPath, String outputXmiFileName) throws Exception {
//        logger.info("Inizio conversione del file: " + inputXmlPath);
//        SAXReader reader = new SAXReader();
//        Document doc = reader.read(new File(inputXmlPath));
//        return convertFromDocument(doc, outputXmiFileName);
//    }
//
//    public static String convertFromDocument(Document doc, String outputXmiFileName) throws Exception {
//        Path xmiPath = LogStreamer.getXMIDirectory().resolve(outputXmiFileName);
//        File xmiFile = xmiPath.toFile();
//        xmiFile.getParentFile().mkdirs();
//
//        Map<String, String> classIdToXmiId = new HashMap<>();
//        Map<String, String> subclassToSuperclass = new HashMap<>();
//
//        List<Element> genElements = doc.selectNodes("//ModelRelationshipContainer[@Name='Generalization']//Generalization");
//        for (Element gen : genElements) {
//            String subId = gen.attributeValue("From");
//            String superId = gen.attributeValue("To");
//            if (subId != null && superId != null) {
//                subclassToSuperclass.put(subId, superId);
//            }
//        }
//
//        List<Node> classNodes = doc.selectNodes("//Class[not(@Idref)]");
//        for (Node node : classNodes) {
//            if (!(node instanceof Element)) continue;
//            Element classEl = (Element) node;
//            String id = classEl.attributeValue("Id");
//            String xmiId = UUID.randomUUID().toString();
//            classIdToXmiId.put(id, xmiId);
//        }
//
//        try (PrintWriter writer = new PrintWriter(new FileWriter(xmiFile))) {
//            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//            writer.println("<xmi:XMI xmlns:xmi=\"http://www.omg.org/spec/XMI/2.1\" xmlns:uml=\"http://www.omg.org/spec/UML/20090901\">");
//            writer.println("  <uml:Model xmi:type=\"uml:Model\" name=\"VPConvertedModel\">");
//
//            for (Node node : classNodes) {
//                if (!(node instanceof Element)) continue;
//                Element classEl = (Element) node;
//                String id = classEl.attributeValue("Id");
//                String name = classEl.attributeValue("Name");
//                String xmiId = classIdToXmiId.get(id);
//                String isAbstract = classEl.attributeValue("Abstract", "false");
//                String isLeaf = classEl.attributeValue("Leaf", "false");
//
//                writer.printf("    <packagedElement xmi:type=\"uml:Class\" xmi:id=\"%s\" name=\"%s\" isAbstract=\"%s\" isLeaf=\"%s\">%n",
//                        xmiId, name, isAbstract, isLeaf);
//
//                Element modelChildren = classEl.element("ModelChildren");
//                if (modelChildren != null) {
//                    for (Iterator<?> it = modelChildren.elementIterator("Attribute"); it.hasNext(); ) {
//                        Element attr = (Element) it.next();
//                        String attrName = attr.attributeValue("Name");
//                        String visibility = attr.attributeValue("Visibility", "private");
//                        String type = attr.attributeValue("Type", "String");
//                        if ((type == null || type.isEmpty()) && attr.element("Type") != null) {
//                            Element typeEl = attr.element("Type");
//                            type = typeEl.attributeValue("Name", "String");
//                        }
//                        if (type == null || type.isEmpty()) type = "String";
//                        writer.printf("      <ownedAttribute xmi:id=\"%s\" name=\"%s\" visibility=\"%s\">%n",
//                                UUID.randomUUID(), attrName, visibility);
//                        writer.printf("        <type xmi:type=\"uml:PrimitiveType\" href=\"http://www.omg.org/spec/UML/20090901/PrimitiveTypes.xmi#%s\"/>%n", type);
//                        writer.println("      </ownedAttribute>");
//                    }
//                    for (Iterator<?> it = modelChildren.elementIterator("Operation"); it.hasNext(); ) {
//                        Element op = (Element) it.next();
//                        String opName = op.attributeValue("Name");
//                        String visibility = op.attributeValue("Visibility", "public");
//                        String opAbstract = op.attributeValue("Abstract", "false");
//                        writer.printf("      <ownedOperation xmi:id=\"%s\" name=\"%s\" visibility=\"%s\" isAbstract=\"%s\">%n",
//                                UUID.randomUUID(), opName, visibility, opAbstract);
//
//                        Element opChildren = op.element("ModelChildren");
//                        if (opChildren != null) {
//                            for (Iterator<?> pit = opChildren.elementIterator("Parameter"); pit.hasNext(); ) {
//                                Element param = (Element) pit.next();
//                                String paramName = param.attributeValue("Name", "param");
//                                String direction = param.attributeValue("Direction", "inout");
//                                String type = param.attributeValue("Type", "String");
//                                if ((type == null || type.isEmpty()) && param.element("Type") != null) {
//                                    Element typeEl = param.element("Type");
//                                    type = typeEl.attributeValue("Name", "String");
//                                }
//                                if (type == null || type.isEmpty()) type = "String";
//                                writer.printf("        <ownedParameter xmi:id=\"%s\" name=\"%s\" direction=\"%s\">%n",
//                                        UUID.randomUUID(), paramName, direction);
//                                writer.printf("          <type xmi:type=\"uml:PrimitiveType\" href=\"http://www.omg.org/spec/UML/20090901/PrimitiveTypes.xmi#%s\"/>%n", type);
//                                writer.println("        </ownedParameter>");
//                            }
//                        }
//
//                        writer.println("      </ownedOperation>");
//                    }
//                }
//
//                if (subclassToSuperclass.containsKey(id)) {
//                    String superId = subclassToSuperclass.get(id);
//                    String superXmi = classIdToXmiId.get(superId);
//                    if (superXmi != null) {
//                        writer.printf("      <generalization xmi:id=\"%s\" general=\"%s\"/>%n", UUID.randomUUID(), superXmi);
//                    } else {
//                        logger.warning("Generalizzazione ignorata: superclasse non trovata per ID=" + superId);
//                    }
//                }
//
//                writer.println("    </packagedElement>");
//            }
//
//            List<Element> associations = doc.selectNodes("//ModelRelationshipContainer[@Name='Association']//Association");
//            for (Element assoc : associations) {
//                String assocName = assoc.attributeValue("Name", "");
//
//                Element fromEnd = null;
//                Element toEnd = null;
//
//                try {
//                    Element from = assoc.element("FromEnd");
//                    if (from != null) {
//                        Element fromAssocEnd = from.element("AssociationEnd");
//                        if (fromAssocEnd != null) {
//                            Element fromType = fromAssocEnd.element("Type");
//                            if (fromType != null) {
//                                fromEnd = fromType.element("Class");
//                            }
//                        }
//                    }
//
//                    Element to = assoc.element("ToEnd");
//                    if (to != null) {
//                        Element toAssocEnd = to.element("AssociationEnd");
//                        if (toAssocEnd != null) {
//                            Element toType = toAssocEnd.element("Type");
//                            if (toType != null) {
//                                toEnd = toType.element("Class");
//                            }
//                        }
//                    }
//
//                } catch (Exception e) {
//                    logger.warning("❌ Errore durante il parsing degli estremi di un'associazione: " + e.getMessage());
//                }
//
//                if (fromEnd == null || toEnd == null) {
//                    logger.warning("⚠️ Associazione ignorata: estremità mancanti o mal formate (ID associazione: " + assoc.attributeValue("Id") + ")");
//                    continue;
//                }
//
//                String fromId = fromEnd.attributeValue("Idref");
//                String toId = toEnd.attributeValue("Idref");
//                String fromXmi = classIdToXmiId.get(fromId);
//                String toXmi = classIdToXmiId.get(toId);
//
//                if (fromXmi == null || toXmi == null) {
//                    logger.warning("Associazione ignorata: ID classi non trovati: from=" + fromId + ", to=" + toId);
//                    continue;
//                }
//
//                Element fromAssoc = assoc.element("FromEnd").element("AssociationEnd");
//                Element toAssoc = assoc.element("ToEnd").element("AssociationEnd");
//
//                String fromMult = fromAssoc.attributeValue("Multiplicity", "0..1");
//                String toMult = toAssoc.attributeValue("Multiplicity", "*");
//                String fromName = fromAssoc.attributeValue("JavaCodeAttributeName");
//                if (fromName == null || fromName.isEmpty()) fromName = "from_" + fromId.substring(0, 4);
//                String toName = toAssoc.attributeValue("JavaCodeAttributeName");
//                if (toName == null || toName.isEmpty()) toName = "to_" + toId.substring(0, 4);
//
//                String[] fromBounds = (fromMult.equals("*") || fromMult.equalsIgnoreCase("Unspecified")) ? new String[]{"0", "-1"} : fromMult.split("\\.\\.");
//                String[] toBounds = (toMult.equals("*") || toMult.equalsIgnoreCase("Unspecified")) ? new String[]{"0", "-1"} : toMult.split("\\.\\.");
//
//                String assocId = UUID.randomUUID().toString();
//                String fromEndId = UUID.randomUUID().toString();
//                String toEndId = UUID.randomUUID().toString();
//
//                writer.printf("    <packagedElement xmi:type=\"uml:Association\" xmi:id=\"%s\" name=\"%s\" memberEnd=\"%s %s\">%n",
//                        assocId, assocName, fromEndId, toEndId);
//
//                writer.printf("      <ownedEnd xmi:id=\"%s\" name=\"%s\" type=\"%s\" association=\"%s\">%n", fromEndId, fromName, fromXmi, assocId);
//                writer.printf("        <lowerValue xmi:type=\"uml:LiteralInteger\" value=\"%s\"/>%n", fromBounds[0]);
//                writer.printf("        <upperValue xmi:type=\"uml:LiteralInteger\" value=\"%s\"/>%n", fromBounds.length > 1 ? fromBounds[1] : fromBounds[0]);
//                writer.println("      </ownedEnd>");
//
//                writer.printf("      <ownedEnd xmi:id=\"%s\" name=\"%s\" type=\"%s\" association=\"%s\">%n", toEndId, toName, toXmi, assocId);
//                writer.printf("        <lowerValue xmi:type=\"uml:LiteralInteger\" value=\"%s\"/>%n", toBounds[0]);
//                writer.printf("        <upperValue xmi:type=\"uml:LiteralInteger\" value=\"%s\"/>%n", toBounds.length > 1 ? toBounds[1] : toBounds[0]);
//                writer.println("      </ownedEnd>");
//
//                writer.println("    </packagedElement>");
//            }
//
//            writer.println("  </uml:Model>");
//            writer.println("</xmi:XMI>");
//        }
//
//        logger.info("✅ Conversione completata. File salvato in: " + xmiFile.getAbsolutePath());
//        return xmiFile.getAbsolutePath();
//    }
//}
package com.uniba.mining.sdmetrics;

import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.*;

import com.uniba.mining.logging.LogStreamer;

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
        Path xmiPath = LogStreamer.getXMIDirectory().resolve(outputXmiFileName);
        File xmiFile = xmiPath.toFile();
        xmiFile.getParentFile().mkdirs();

        Map<String, String> classIdToXmiId = new HashMap<>();
        Map<String, String> subclassToSuperclass = new HashMap<>();
        Map<String, String> xmiIdToName = new HashMap<>();

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
                xmiIdToName.put(xmiId, name);
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
                        xmiIdToName.put(attrId, attrName);
                        writer.printf("      <ownedAttribute xmi:id=\"%s\" name=\"%s\" visibility=\"%s\">%n",
                                attrId, attrName, attrVisibility);
                        writer.printf("        <type xmi:type=\"uml:PrimitiveType\" href=\"http://www.omg.org/spec/UML/20090901/PrimitiveTypes.xmi#%s\"/>%n", type);
                        writer.println("      </ownedAttribute>");
                    }
                    for (Iterator<?> it = modelChildren.elementIterator("Operation"); it.hasNext(); ) {
                        Element op = (Element) it.next();
                        String opName = op.attributeValue("Name", "unnamedOperation");
                        String opVisibility = op.attributeValue("Visibility", "public");
                        String opAbstract = op.attributeValue("Abstract", "false");
                        String opId = UUID.randomUUID().toString();
                        xmiIdToName.put(opId, opName);
                        writer.printf("      <ownedOperation xmi:id=\"%s\" name=\"%s\" visibility=\"%s\" isAbstract=\"%s\">%n",
                                opId, opName, opVisibility, opAbstract);

                        Element opChildren = op.element("ModelChildren");
                        if (opChildren != null) {
                            for (Iterator<?> pit = opChildren.elementIterator("Parameter"); pit.hasNext(); ) {
                                Element param = (Element) pit.next();
                                String paramName = param.attributeValue("Name", "param");
                                String direction = param.attributeValue("Direction", "inout");
                                String paramVisibility = param.attributeValue("Visibility", "public");
                                String type = param.attributeValue("Type");
                                if ((type == null || type.isEmpty()) && param.element("Type") != null) {
                                    Element typeEl = param.element("Type");
                                    type = typeEl.attributeValue("Name", "String");
                                }
                                if (type == null || type.isEmpty()) type = "String";
                                String paramId = UUID.randomUUID().toString();
                                xmiIdToName.put(paramId, paramName);
                                writer.printf("        <ownedParameter xmi:id=\"%s\" name=\"%s\" direction=\"%s\" visibility=\"%s\">%n",
                                        paramId, paramName, direction, paramVisibility);
                                writer.printf("          <type xmi:type=\"uml:PrimitiveType\" href=\"http://www.omg.org/spec/UML/20090901/PrimitiveTypes.xmi#%s\"/>%n", type);
                                writer.println("        </ownedParameter>");
                            }
                        }

                        writer.println("      </ownedOperation>");
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

        logger.info("✅ Conversione completata. File salvato in: " + xmiFile.getAbsolutePath());
        return xmiFile.getAbsolutePath();
    }
}