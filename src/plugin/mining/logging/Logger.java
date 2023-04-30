package plugin.mining.logging;

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
import java.util.Collection;
import java.util.List;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClassifier;
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

import com.vp.plugin.VPProductInfo;
import com.vp.plugin.ViewManager;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectProperties;

import plugin.mining.util.Application;

public class Logger {
    private static final String USER_NAME = System.getProperty("user.name");
    private static final Path logDirectory = Paths.get(System.getProperty("user.dir"), "logs", USER_NAME);
    public static final XFactory xFactory = new XFactoryBufferedImpl();
    public static final XIDFactory xIdFactory = XIDFactory.instance();
    public static final XTimeExtension xTimeExtension = XTimeExtension.instance();
    private static final XesXmlSerializer xesXmlSerializer = new XesXmlSerializer();
    private static XLog xLog;
    private static XTrace xTrace;
    private static XEvent xEvent;
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

    private static void addClassifier(List<XEventClassifier> eventClassifiers, LogAttribute logAttribute) {
        XEventClassifier xEventClassifier = new XEventAttributeClassifier(
                String.format("%s %s", logAttribute.getName(), "Classifier"), logAttribute.getName());
        eventClassifiers.add(xEventClassifier);
    }

    private static void addGlobalAttribute(List<XAttribute> attributes, LogAttribute logAttribute, Object value) {
        XAttribute xAttribute = logAttribute.getType().createAttribute(logAttribute.getName(), value, null);
        attributes.add(xAttribute);
    }

    private static void addAttribute(XAttributeMap attributes, LogAttribute logAttribute, Object value) {
        XAttribute xAttribute = logAttribute.getType().createAttribute(logAttribute.getName(), value, null);
        attributes.put(logAttribute.getName(), xAttribute);
    }

    public static void createLog() {
        long timestamp = Instant.now().toEpochMilli();
        VPProductInfo vpProductInfo = Application.getProductInfo();

        XAttributeMap attributes = xFactory.createAttributeMap();
        addAttribute(attributes, LogAttribute.PRODUCT_NAME, vpProductInfo.getName());
        addAttribute(attributes, LogAttribute.PRODUCT_VERSION, vpProductInfo.getVersion());
        addAttribute(attributes, LogAttribute.PRODUCT_BUILD, vpProductInfo.getBuildNumber());
        addAttribute(attributes, LogAttribute.CREATED_AT, timestamp);

        xLog = xFactory.createLog(attributes);

        List<XAttribute> globalTraceAttributes = xLog.getGlobalTraceAttributes();
        addGlobalAttribute(globalTraceAttributes, LogAttribute.CASE_ID, "Case Id");
        addGlobalAttribute(globalTraceAttributes, LogAttribute.AUTHOR_NAME, "Author Name");
        addGlobalAttribute(globalTraceAttributes, LogAttribute.PROJECT_NAME, "Project Name");
        addGlobalAttribute(globalTraceAttributes, LogAttribute.CREATED_AT, timestamp);

        List<XEventClassifier> eventClassifiers = xLog.getClassifiers();
        addClassifier(eventClassifiers, LogAttribute.EVENT_ID);
        addClassifier(eventClassifiers, LogAttribute.ACTIVITY_NAME);
        addClassifier(eventClassifiers, LogAttribute.DIAGRAM_ID);
        addClassifier(eventClassifiers, LogAttribute.DIAGRAM_TYPE);
        addClassifier(eventClassifiers, LogAttribute.DIAGRAM_NAME);
        addClassifier(eventClassifiers, LogAttribute.UML_ELEMENT_ID);
        addClassifier(eventClassifiers, LogAttribute.UML_ELEMENT_TYPE);
        addClassifier(eventClassifiers, LogAttribute.UML_ELEMENT_NAME);
        addClassifier(eventClassifiers, LogAttribute.CREATED_AT);

        List<XAttribute> globalEventAttributes = xLog.getGlobalEventAttributes();
        addGlobalAttribute(globalEventAttributes, LogAttribute.EVENT_ID, "Event Id");
        addGlobalAttribute(globalEventAttributes, LogAttribute.ACTIVITY_NAME, "Activity Name");
        addGlobalAttribute(globalEventAttributes, LogAttribute.DIAGRAM_ID, "Diagram Id");
        addGlobalAttribute(globalEventAttributes, LogAttribute.DIAGRAM_TYPE, "Diagram Type");
        addGlobalAttribute(globalEventAttributes, LogAttribute.DIAGRAM_NAME, "Diagram Name");
        addGlobalAttribute(globalEventAttributes, LogAttribute.UML_ELEMENT_ID, "UML Element Id");
        addGlobalAttribute(globalEventAttributes, LogAttribute.UML_ELEMENT_TYPE, "UML Element Type");
        addGlobalAttribute(globalEventAttributes, LogAttribute.UML_ELEMENT_NAME, "UML Element Name");
        addGlobalAttribute(globalEventAttributes, LogAttribute.CREATED_AT, timestamp);
    }

    public static void createTrace(IProject project) {
        long timestamp = Instant.now().toEpochMilli();
        IProjectProperties projectProperties = project.getProjectProperties();
        String authorName = projectProperties.getAuthor();
        String caseId = xIdFactory.createId().toString();
        String projectName = projectProperties.getProjectName();
        XAttributeMap attributes = xFactory.createAttributeMap();
        addAttribute(attributes, LogAttribute.CASE_ID, caseId);
        addAttribute(attributes, LogAttribute.AUTHOR_NAME, authorName);
        addAttribute(attributes, LogAttribute.PROJECT_NAME, projectName);
        addAttribute(attributes, LogAttribute.CREATED_AT, timestamp);

        xTrace = xFactory.createTrace(attributes);
        xLog.add(xTrace);
    }

    public static void createEvent(LogActivity activity, IModelElement modelElement) {
        createEvent(activity, modelElement, null, null);
    }

    public static void createEvent(LogActivity activity, IModelElement modelElement, String propertyName,
            String propertyValue) {
        System.out.println(
                String.format("%s %s %s %s", activity.toString(), modelElement.getName(), propertyName, propertyValue));
        long timestamp = Instant.now().toEpochMilli();
        IDiagramUIModel diagramUIModel = Application.getDiagram();
        String eventId = xIdFactory.createId().toString();
        XAttributeMap attributes = xFactory.createAttributeMap();
        addAttribute(attributes, LogAttribute.EVENT_ID, eventId);
        addAttribute(attributes, LogAttribute.ACTIVITY_NAME, activity.getName());
        addAttribute(attributes, LogAttribute.DIAGRAM_ID, diagramUIModel.getId());
        addAttribute(attributes, LogAttribute.DIAGRAM_TYPE, diagramUIModel.getType());
        addAttribute(attributes, LogAttribute.DIAGRAM_NAME, diagramUIModel.getName());
        addAttribute(attributes, LogAttribute.UML_ELEMENT_ID, modelElement.getId());
        addAttribute(attributes, LogAttribute.UML_ELEMENT_TYPE, modelElement.getModelType());
        addAttribute(attributes, LogAttribute.UML_ELEMENT_NAME, modelElement.getName());
        addAttribute(attributes, LogAttribute.CREATED_AT, timestamp);
        if (propertyName != null && propertyValue != null) {
            addAttribute(attributes, LogAttribute.PROPERTY_NAME, propertyName);
            addAttribute(attributes, LogAttribute.PROPERTY_VALUE, propertyValue);
        }

        xEvent = xFactory.createEvent(attributes);
        xTrace.add(xEvent);
    }

    public static XAttributeMap getLastEventAttributes() {
        return xEvent.getAttributes();
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
