package plugin.mining.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

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
    private static final XFactory xFactory = new XFactoryBufferedImpl();
    private static final XIDFactory xIdFactory = XIDFactory.instance();
    private static final XTimeExtension xTimeExtension = XTimeExtension.instance();
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

    private static void addAttribute(XAttributeMap attributes, LogAttribute attribute, Object value) {
        if (!attribute.getType().getValueType().isInstance(value))
            throw new UnsupportedOperationException("type of \"value\" not match the type of \"attribute\"");

        XAttribute xAttribute = null;
        if (attribute.getType() == LogAttribute.Type.LITERAL)
            xAttribute = xFactory.createAttributeLiteral(attribute.getName(), (String) value, null);
        else if (attribute.getType() == LogAttribute.Type.DISCRETE)
            xAttribute = xFactory.createAttributeDiscrete(attribute.getName(), (Long) value, null);
        else if (attribute.getType() == LogAttribute.Type.CONTINUOUS)
            xAttribute = xFactory.createAttributeContinuous(attribute.getName(), (Double) value, null);
        else if (attribute.getType() == LogAttribute.Type.BOOLEAN)
            xAttribute = xFactory.createAttributeBoolean(attribute.getName(), (Boolean) value, null);
        else if (attribute.getType() == LogAttribute.Type.TIMESTAMP)
            xAttribute = xFactory.createAttributeTimestamp(attribute.getName(), (Long) value, null);

        attributes.put(attribute.getName(), xAttribute);
    }

    public static void createLog() {
        long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        XAttributeMap attributes = xFactory.createAttributeMap();
        VPProductInfo vpProductInfo = Application.getProductInfo();
        addAttribute(attributes, LogAttribute.PRODUCT_NAME, vpProductInfo.getName());
        addAttribute(attributes, LogAttribute.PRODUCT_VERSION, vpProductInfo.getVersion());
        addAttribute(attributes, LogAttribute.PRODUCT_BUILD, vpProductInfo.getBuildNumber());
        addAttribute(attributes, LogAttribute.CREATED_AT, timestamp);

        xLog = xFactory.createLog(attributes);
    }

    public static void createTrace(IProject project) {
        long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
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
        long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        IDiagramUIModel diagramUIModel = modelElement.getMasterView().getDiagramUIModel();
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
        if (propertyName != null || propertyValue != null) {
            addAttribute(attributes, LogAttribute.PROPERTY_NAME, propertyName);
            addAttribute(attributes, LogAttribute.PROPERTY_VALUE, propertyValue);
        }

        xEvent = xFactory.createEvent(attributes);
        xTimeExtension.assignTimestamp(xEvent, timestamp);

        xTrace.add(xEvent);
    }

    public static XAttributeMap getLastEventAttributes() {
        return xEvent.getAttributes();
    }

    public static void setLastEventAttributes(XAttributeMap attributes) {
        xEvent.setAttributes(attributes);
    }

    public static void saveLog() {
        try {
            String logName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss"))
                    + ".xes";
            Path logPath = logDirectory.resolve(logName);
            File logFile = Files.createFile(logPath).toFile();
            OutputStream logOutputStream = new FileOutputStream(logFile);
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
