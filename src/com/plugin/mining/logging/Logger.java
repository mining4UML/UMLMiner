package com.plugin.mining.logging;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.id.XIDFactory;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XesXmlSerializer;

import com.plugin.mining.logging.LogActivity.ModelType;
import com.plugin.mining.logging.extensions.XIdentityExtension;
import com.plugin.mining.util.Application;
import com.plugin.mining.util.StringPlaceholders;
import com.plugin.mining.util.StringPlaceholders.Placeholder;
import com.vp.plugin.VPProductInfo;
import com.vp.plugin.ViewManager;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectProperties;
import com.vp.plugin.model.IRelationship;

public class Logger {
    private static final String EXTENSION = ".xes";
    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd_HH.mm.ss";
    private static final String USER_NAME = System.getProperty("user.name");
    private static final Path logDirectory = Paths.get(System.getProperty("user.dir"), "logs", USER_NAME);
    public static final XFactory xFactory = new XFactoryBufferedImpl();
    public static final XIDFactory xIdFactory = XIDFactory.instance();
    public static final XIdentityExtension xIdentityExtension = XIdentityExtension.instance();
    public static final XConceptExtension xConceptExtension = XConceptExtension.instance();
    public static final XTimeExtension xTimeExtension = XTimeExtension.instance();
    private static final XesXmlParser xesXmlParser = new XesXmlParser();
    private static final XesXmlSerializer xesXmlSerializer = new XesXmlSerializer();
    private static XLog xLog;
    private static XTrace xTrace;
    private static final ViewManager viewManager = Application.getViewManager();

    private static void createDirectory() {
        try {
            if (Files.notExists(logDirectory))
                Files.createDirectory(logDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        createDirectory();
    }

    private static void addAttribute(XAttributeMap attributes, LogAttribute logAttribute, Object value) {
        XAttribute xAttribute = logAttribute.createAttribute(value);
        attributes.put(logAttribute.getKey(), xAttribute);
    }

    private static void addExtraAttributes(XAttributeMap attributes, IModelElement modelElement) {
        if (modelElement instanceof IOperation) {
            addAttribute(attributes, LogAttribute.PARAMETERS,
                    Arrays.toString(
                            Arrays.stream(((IOperation) modelElement).toParameterArray()).map(IModelElement::getId)
                                    .toArray(String[]::new)));
        }
        if (modelElement instanceof IRelationship) {
            IRelationship relationship = (IRelationship) modelElement;

            if (relationship instanceof IAssociation) {
                IAssociation association = (IAssociation) relationship;
                String relationshipFromEnd = association.getFromEnd().getModelElement() != null
                        ? association.getFromEnd().getModelElement().getId()
                        : LogExtractor.DEFAULT_VALUE;
                String relationshipToEnd = association.getToEnd().getModelElement() != null
                        ? association.getToEnd().getModelElement().getId()
                        : LogExtractor.DEFAULT_VALUE;

                addAttribute(attributes, LogAttribute.RELATIONSHIP_FROM_END, relationshipFromEnd);
                addAttribute(attributes, LogAttribute.RELATIONSHIP_TO_END, relationshipToEnd);
            } else {
                String relationshipFromEnd = relationship.getFrom() != null ? relationship.getFrom().getId()
                        : LogExtractor.DEFAULT_VALUE;
                String relationshipToEnd = relationship.getTo() != null ? relationship.getTo().getId()
                        : LogExtractor.DEFAULT_VALUE;

                addAttribute(attributes, LogAttribute.RELATIONSHIP_FROM_END, relationshipFromEnd);
                addAttribute(attributes, LogAttribute.RELATIONSHIP_TO_END, relationshipToEnd);
            }
        }
        if (modelElement instanceof IPackage) {
            addAttribute(attributes, LogAttribute.UML_ELEMENT_CHILDREN,
                    Arrays.toString(
                            Arrays.stream(((IPackage) modelElement).toChildArray()).map(IModelElement::getId)
                                    .toArray(String[]::new)));
        }
    }

    public static void createLog() {
        long timestamp = Instant.now().toEpochMilli();
        String processId = xIdFactory.createId().toString();
        VPProductInfo vpProductInfo = Application.getProductInfo();
        String productName = vpProductInfo.getName();
        String processName = String.join("-", productName, processId);
        String productVersion = vpProductInfo.getVersion();
        String productBuild = vpProductInfo.getBuildNumber();
        XAttributeMap attributes = xFactory.createAttributeMap();
        addAttribute(attributes, LogAttribute.PROCESS_ID, processId);
        addAttribute(attributes, LogAttribute.PROCESS_NAME, processName);
        addAttribute(attributes, LogAttribute.PROCESS_TIMESTAMP, timestamp);
        addAttribute(attributes, LogAttribute.PRODUCT_NAME, productName);
        addAttribute(attributes, LogAttribute.PRODUCT_VERSION, productVersion);
        addAttribute(attributes, LogAttribute.PRODUCT_BUILD, productBuild);

        xLog = xFactory.createLog(attributes);

        Set<XExtension> extensions = xLog.getExtensions();
        extensions.addAll(Arrays.asList(xIdentityExtension, xConceptExtension, xTimeExtension));

        List<XEventClassifier> eventClassifiers = xLog.getClassifiers();
        eventClassifiers.addAll(LogAttribute.getEventClassifiers());

        List<XAttribute> globalTraceAttributes = xLog.getGlobalTraceAttributes();
        globalTraceAttributes.addAll(LogAttribute.getGlobalTraceAttributes());

        List<XAttribute> globalEventAttributes = xLog.getGlobalEventAttributes();
        globalEventAttributes.addAll(LogAttribute.getGlobalEventAttributes());
    }

    public static void createTrace(IProject project) {
        long timestamp = Instant.now().toEpochMilli();
        String caseId = xIdFactory.createId().toString();
        IProjectProperties projectProperties = project.getProjectProperties();
        String authorName = projectProperties.getAuthor();
        String caseName = String.join("-", authorName, caseId);
        String projectName = projectProperties.getProjectName();
        XAttributeMap attributes = xFactory.createAttributeMap();
        addAttribute(attributes, LogAttribute.CASE_ID, caseId);
        addAttribute(attributes, LogAttribute.CASE_NAME, caseName);
        addAttribute(attributes, LogAttribute.CASE_TIMESTAMP, timestamp);
        addAttribute(attributes, LogAttribute.AUTHOR_NAME, authorName);
        addAttribute(attributes, LogAttribute.PROJECT_NAME, projectName);

        xTrace = xFactory.createTrace(attributes);
        xLog.add(xTrace);
    }

    public static void createEvent(LogActivity logActivity, IProject project, String propertyName,
            String propertyValue) {
        System.out.println(
                String.format("%s %s %s %s", logActivity.toString(), project.getName(), propertyName,
                        propertyValue));
        long timestamp = Instant.now().toEpochMilli();
        IDiagramUIModel diagram = Application.getDiagram();
        String activityId = xIdFactory.createId().toString();
        String diagramId = diagram != null ? diagram.getId() : LogExtractor.DEFAULT_VALUE;
        String diagramType = diagram != null ? diagram.getType() : LogExtractor.DEFAULT_VALUE;
        String diagramName = diagram != null ? diagram.getName() : LogExtractor.DEFAULT_VALUE;
        String projectId = project.getId();
        String projectType = logActivity.getModelType().getName();
        String projectName = project.getName();
        Placeholder typePlaceholder = new Placeholder("type", projectType);
        Placeholder propertyNamePlaceholder = new Placeholder("propertyName", propertyName);
        String activityName = StringPlaceholders.setPlaceholders(logActivity.getName(), typePlaceholder,
                propertyNamePlaceholder);
        String activityInstance = String.join(" - ", activityName, activityId);
        XAttributeMap attributes = xFactory.createAttributeMap();
        addAttribute(attributes, LogAttribute.ACTIVITY_ID, activityId);
        addAttribute(attributes, LogAttribute.ACTIVITY_NAME, activityName);
        addAttribute(attributes, LogAttribute.ACTIVITY_INSTANCE, activityInstance);
        addAttribute(attributes, LogAttribute.ACTIVITY_TIMESTAMP, timestamp);
        addAttribute(attributes, LogAttribute.DIAGRAM_ID, diagramId);
        addAttribute(attributes, LogAttribute.DIAGRAM_TYPE, diagramType);
        addAttribute(attributes, LogAttribute.DIAGRAM_NAME, diagramName);
        addAttribute(attributes, LogAttribute.UML_ELEMENT_ID, projectId);
        addAttribute(attributes, LogAttribute.UML_ELEMENT_TYPE, projectType);
        addAttribute(attributes, LogAttribute.UML_ELEMENT_NAME, projectName);
        if (propertyName != null && propertyValue != null && !propertyName.equals("childAdded")
                && !propertyName.equals("childRemoved")) {
            addAttribute(attributes, LogAttribute.PROPERTY_NAME, propertyName);
            addAttribute(attributes, LogAttribute.PROPERTY_VALUE, propertyValue);
        }

        XEvent xEvent = xFactory.createEvent(attributes);
        xTrace.add(xEvent);
    }

    public static void createEvent(LogActivity logActivity, IProject project) {
        createEvent(logActivity, project, null, null);
    }

    public static void createEvent(LogActivity logActivity, IDiagramUIModel diagramUIModel, String propertyName,
            String propertyValue) {
        System.out.println(
                String.format("%s %s %s %s", logActivity.toString(), diagramUIModel.getName(), propertyName,
                        propertyValue));
        long timestamp = Instant.now().toEpochMilli();
        String activityId = xIdFactory.createId().toString();
        String diagramId = diagramUIModel.getId();
        String diagramType = diagramUIModel.getType();
        String diagramName = diagramUIModel.getName();
        Placeholder typePlaceholder = new Placeholder("type", diagramType);
        Placeholder propertyNamePlaceholder = new Placeholder("propertyName", propertyName);
        String activityName = StringPlaceholders.setPlaceholders(logActivity.getName(), typePlaceholder,
                propertyNamePlaceholder);
        String activityInstance = String.join(" - ", activityName, activityId);
        XAttributeMap attributes = xFactory.createAttributeMap();
        addAttribute(attributes, LogAttribute.ACTIVITY_ID, activityId);
        addAttribute(attributes, LogAttribute.ACTIVITY_NAME, activityName);
        addAttribute(attributes, LogAttribute.ACTIVITY_INSTANCE, activityInstance);
        addAttribute(attributes, LogAttribute.ACTIVITY_TIMESTAMP, timestamp);
        addAttribute(attributes, LogAttribute.DIAGRAM_ID, diagramId);
        addAttribute(attributes, LogAttribute.DIAGRAM_TYPE, diagramType);
        addAttribute(attributes, LogAttribute.DIAGRAM_NAME, diagramName);
        addAttribute(attributes, LogAttribute.UML_ELEMENT_ID, diagramId);
        addAttribute(attributes, LogAttribute.UML_ELEMENT_TYPE, diagramType);
        addAttribute(attributes, LogAttribute.UML_ELEMENT_NAME, diagramName);
        if (propertyName != null && propertyValue != null && !propertyName.equals("childAdded")
                && !propertyName.equals("childRemoved")) {
            addAttribute(attributes, LogAttribute.PROPERTY_NAME, propertyName);
            addAttribute(attributes, LogAttribute.PROPERTY_VALUE, propertyValue);
        }

        XEvent xEvent = xFactory.createEvent(attributes);
        xTrace.add(xEvent);
    }

    public static void createEvent(LogActivity logActivity, IDiagramUIModel diagramUIModel) {
        createEvent(logActivity, diagramUIModel, null, null);
    }

    public static void createEvent(LogActivity logActivity, IModelElement modelElement, ModelType sourceModelType,
            String propertyName, String propertyValue) {
        System.out.println(
                String.format("%s %s %s %s %s", logActivity.toString(), modelElement.getName(),
                        sourceModelType.getName(),
                        propertyName,
                        propertyValue));
        long timestamp = Instant.now().toEpochMilli();
        String activityId = xIdFactory.createId().toString();
        IDiagramUIModel diagramUIModel = LogExtractor.getDiagramUIModel(modelElement);
        String diagramId = diagramUIModel != null ? diagramUIModel.getId() : LogExtractor.DEFAULT_VALUE;
        String diagramType = diagramUIModel != null ? diagramUIModel.getType() : LogExtractor.DEFAULT_VALUE;
        String diagramName = diagramUIModel != null ? diagramUIModel.getName() : LogExtractor.DEFAULT_VALUE;
        String modelElementId = modelElement.getId();
        String modelElementType = LogExtractor.extractModelType(modelElement);
        String modelElementName = LogExtractor.extractModelName(modelElement);
        String sourceType = LogExtractor.extractSourceType(sourceModelType, diagramUIModel);
        Placeholder typePlaceholder = new Placeholder("type", modelElementType);
        Placeholder sourceTypePlaceholder = new Placeholder("sourceType", sourceType);
        Placeholder propertyNamePlaceholder = new Placeholder("propertyName", propertyName);
        Placeholder childTypePlaceholder = new Placeholder("childType", propertyValue);
        String activityName = StringPlaceholders.setPlaceholders(logActivity.getName(), typePlaceholder,
                sourceTypePlaceholder, propertyNamePlaceholder, childTypePlaceholder);
        String activityInstance = String.join(" - ", activityName, activityId);
        XAttributeMap attributes = xFactory.createAttributeMap();
        addAttribute(attributes, LogAttribute.ACTIVITY_ID, activityId);
        addAttribute(attributes, LogAttribute.ACTIVITY_NAME, activityName);
        addAttribute(attributes, LogAttribute.ACTIVITY_INSTANCE, activityInstance);
        addAttribute(attributes, LogAttribute.ACTIVITY_TIMESTAMP, timestamp);
        addAttribute(attributes, LogAttribute.DIAGRAM_ID, diagramId);
        addAttribute(attributes, LogAttribute.DIAGRAM_TYPE, diagramType);
        addAttribute(attributes, LogAttribute.DIAGRAM_NAME, diagramName);
        addAttribute(attributes, LogAttribute.UML_ELEMENT_ID, modelElementId);
        addAttribute(attributes, LogAttribute.UML_ELEMENT_TYPE, modelElementType);
        addAttribute(attributes, LogAttribute.UML_ELEMENT_NAME, modelElementName);
        if (propertyName != null && propertyValue != null && !propertyName.equals("childAdded")
                && !propertyName.equals("childRemoved")) {
            addAttribute(attributes, LogAttribute.PROPERTY_NAME, propertyName);
            addAttribute(attributes, LogAttribute.PROPERTY_VALUE, propertyValue);
        }

        addExtraAttributes(attributes, modelElement);

        XEvent xEvent = xFactory.createEvent(attributes);
        xTrace.add(xEvent);
    }

    public static void createEvent(LogActivity logActivity, IModelElement modelElement, String propertyName,
            String propertyValue) {
        createEvent(logActivity, modelElement, ModelType.DIAGRAM, propertyName, propertyValue);
    }

    public static void createEvent(LogActivity logActivity, IModelElement modelElement, ModelType sourceModelType) {
        createEvent(logActivity, modelElement, sourceModelType, null, null);
    }

    public static void createEvent(LogActivity logActivity, IModelElement modelElement) {
        createEvent(logActivity, modelElement, ModelType.DIAGRAM);
    }

    private static String getLogName() {
        return Application.getProject().getId() + EXTENSION;
    }

    public static void loadLog() {
        try {
            String logName = getLogName();
            System.out.println("Parse log " + logName);
            Path logPath = logDirectory.resolve(logName);

            if (Files.isReadable(logPath))
                xLog = xesXmlParser.parse(logPath.toFile()).get(0);
            else
                createLog();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void saveLog() {
        try {
            String logName = getLogName();
            System.out.println("Save log " + logName);
            Path logPath = logDirectory.resolve(logName);

            OutputStream logOutputStream = new FileOutputStream(
                    Files.isWritable(logPath) ? logPath.toFile() : Files.createFile(logPath).toFile());

            xLog.removeIf(Collection::isEmpty);
            xesXmlSerializer.serialize(xLog, logOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasDiagram(IDiagramUIModel diagramUIModel) {
        for (XTrace xTrace : xLog) {
            for (XEvent xEvent : xTrace) {
                XAttributeLiteral diagramIdAttribute = (XAttributeLiteral) xEvent.getAttributes()
                        .get(LogAttribute.DIAGRAM_ID.getKey());
                if (diagramIdAttribute != null && diagramUIModel.getId().equals(diagramIdAttribute.getValue()))
                    return true;
            }
        }
        return false;
    }

    private final Class<?> classId;

    public Logger(Class<?> classId) {
        this.classId = classId;
    }

    public void info(String message) {
        viewManager.showMessage(String.format("%s: %s at %s", classId.getSimpleName(), message,
                LocalDateTime.now()), Application.PLUGIN_ID);
    }

    public void info(String message, Object... args) {
        String formattedMessage = String.format(message, args);
        viewManager.showMessage(String.format("%s: %s at %s", classId.getSimpleName(),
                formattedMessage,
                LocalDateTime.now()), Application.PLUGIN_ID);
    }
}
