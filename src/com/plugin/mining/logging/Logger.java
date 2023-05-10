package com.plugin.mining.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XesXmlSerializer;

import com.plugin.mining.logging.extensions.XIdentityExtension;
import com.plugin.mining.util.Application;
import com.plugin.mining.util.StringPlaceholders;
import com.plugin.mining.util.StringPlaceholders.Placeholder;
import com.vp.plugin.VPProductInfo;
import com.vp.plugin.ViewManager;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectProperties;
import com.vp.plugin.model.IRelationship;

public class Logger {
    private static final String USER_NAME = System.getProperty("user.name");
    private static final Path logDirectory = Paths.get(System.getProperty("user.dir"), "logs", USER_NAME);
    public static final XFactory xFactory = new XFactoryBufferedImpl();
    public static final XIDFactory xIdFactory = XIDFactory.instance();
    public static final XIdentityExtension xIdentityExtension = XIdentityExtension.instance();
    public static final XConceptExtension xConceptExtension = XConceptExtension.instance();
    public static final XTimeExtension xTimeExtension = XTimeExtension.instance();
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

    private static String extractModelType(IModelElement modelElement) {
        return (modelElement instanceof IOperation && ((IOperation) modelElement).isConstructor())
                ? "Constructor"
                : modelElement instanceof IAssociation
                        ? String.format("Association[from=%s,to=%s]",
                                ((IAssociationEnd) ((IAssociation) modelElement).getFromEnd()).getAggregationKind(),
                                ((IAssociationEnd) ((IAssociation) modelElement).getToEnd()).getAggregationKind())
                        : modelElement.getModelType();
    }

    private static String extractModelName(IModelElement modelElement) {
        return modelElement.getName() != null ? modelElement.getName() : "unknown";
    }

    private static String extractModelStereotype(IModelElement modelElement) {
        return modelElement instanceof IClass && ((IClass) modelElement).stereotypesCount() > 0
                ? ((IClass) modelElement).toStereotypesArray()[0]
                : "";
    }

    public static void createEvent(LogActivity logActivity, IModelElement modelElement, String propertyName,
            String propertyValue) {
        System.out.println(
                String.format("%s %s %s %s", logActivity.toString(), modelElement.getName(), propertyName,
                        propertyValue));
        long timestamp = Instant.now().toEpochMilli();
        String activityId = xIdFactory.createId().toString();
        IDiagramUIModel diagramUIModel = Application.getDiagram();
        String diagramId = diagramUIModel.getId();
        String diagramType = diagramUIModel.getType();
        String diagramName = diagramUIModel.getName();
        String modelElementId = modelElement.getId();
        String modelElementType = extractModelType(modelElement);
        String modelElementName = extractModelName(modelElement);
        String type = extractModelStereotype(modelElement);
        Placeholder typePlaceholder = new Placeholder("type", type.isEmpty() ? modelElementType + " " : type);
        Placeholder propertyNamePlaceholder = new Placeholder("propertyName", propertyName);
        String activityName = StringPlaceholders.setPlaceholders(logActivity.getName(), typePlaceholder,
                propertyNamePlaceholder);
        String activityInstance = activityName + activityId;
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
        if (propertyName != null && propertyValue != null) {
            addAttribute(attributes, LogAttribute.PROPERTY_NAME, propertyName);
            addAttribute(attributes, LogAttribute.PROPERTY_VALUE, propertyValue);
        }
        if (modelElement instanceof IRelationship) {
            IRelationship relationship = (IRelationship) modelElement;
            String relationshipFromEnd = relationship instanceof IAssociation
                    ? ((IAssociation) relationship).getFromEnd().getModelElement().getId()
                    : relationship.getFrom().getId();
            String relationshipToEnd = relationship instanceof IAssociation
                    ? ((IAssociation) relationship).getToEnd().getModelElement().getId()
                    : relationship.getTo().getId();
            addAttribute(attributes, LogAttribute.RELATIONSHIP_FROM_END, relationshipFromEnd);
            addAttribute(attributes, LogAttribute.RELATIONSHIP_TO_END, relationshipToEnd);
        }

        XEvent xEvent = xFactory.createEvent(attributes);
        xTrace.add(xEvent);
    }

    public static void createEvent(LogActivity activity, IModelElement modelElement) {
        createEvent(activity, modelElement, null, null);
    }

    public static void saveLog() {
        try {
            String logName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss"))
                    + ".xes";
            Path logPath = logDirectory.resolve(logName);
            File logFile = Files.createFile(logPath).toFile();
            OutputStream logOutputStream = new FileOutputStream(logFile);
            xLog.removeIf(Collection::isEmpty);
            xesXmlSerializer.serialize(xLog, logOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
