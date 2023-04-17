package plugin.mining.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XesXmlSerializer;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;

public class Logger {
    private static final Path logDirectory = Paths.get(System.getProperty("user.dir"), "logs");
    private static final XFactory xFactory = new XFactoryBufferedImpl();
    private static final XesXmlSerializer xesXmlSerializer = new XesXmlSerializer();
    private static XLog xLog;
    private static XTrace xTrace;
    private final Class<?> classId;
    private final ViewManager viewManager = ApplicationManager.instance().getViewManager();

    public Logger(Class<?> classId) {
        this.classId = classId;
    }

    private static void createDirectory() {
        try {
            if (Files.notExists(logDirectory))
                Files.createDirectory(logDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createLog() {
        createDirectory();
        xLog = xFactory.createLog();
    }

    public static void createTrace() {
        xTrace = xFactory.createTrace();
        xLog.add(xTrace);
    }

    public static void createEvent() {
        xTrace.add(xFactory.createEvent());
    }

    public static void saveLog() {
        try {
            Path logPath = logDirectory.resolve(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                    + ".xes");
            File logFile = Files.createFile(logPath).toFile();
            OutputStream logOutputStream = new FileOutputStream(logFile);
            xesXmlSerializer.serialize(xLog, logOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
