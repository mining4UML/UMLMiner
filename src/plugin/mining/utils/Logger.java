package plugin.mining.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;

public class Logger {
    private static final Path logPath = Paths.get(System.getProperty("user.dir"), "logs");
    private final Class<?> classId;
    private final ViewManager viewManager = ApplicationManager.instance().getViewManager();

    public Logger(Class<?> classId) {
        this.classId = classId;
    }

    public static void createLogFile() {
        try {
            if (Files.notExists(logPath))
                Files.createDirectory(logPath);
            Files.createFile(logPath.resolve(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) + ".xes"));
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
