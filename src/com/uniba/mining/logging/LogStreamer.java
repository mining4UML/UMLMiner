package com.uniba.mining.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlSerializer;

import com.uniba.mining.utils.Application;

public class LogStreamer {
    public static final String LOG_EXTENSION = ".xes";
    public static final String ZIP_EXTENSION = ".zip";
    private static final String USER_NAME = System.getProperty("user.name");
    private static final String USER_DIR = System.getProperty("user.dir");
    private static final Path logsDirectory = Paths.get(USER_DIR, "logs", USER_NAME);
    private static final Path modelsDirectory = Paths.get(USER_DIR, "models", USER_NAME);
    private static final Logger logger = new Logger(LogStreamer.class);
    private static final XesXmlParser xesXmlParser = new XesXmlParser();
    private static final XesXmlSerializer xesXmlSerializer = new XesXmlSerializer();
    private static final Set<String> logExtensions = new HashSet<>(
            Arrays.asList("xes", "csv", "jsoncel", "xmlocel"));
    private static final FileFilter logFileFilter = new FileNameExtensionFilter(
            "Log " + Arrays.toString(logExtensions.toArray()),
            logExtensions.toArray(String[]::new));
    private static final FileFilter modelFileFilter = new FileNameExtensionFilter("MP-Declare File", "decl");
    public static final String LOG_EXTENSIONS_REGEX = "\\.(" + logExtensions.stream().reduce("",
            (t, u) -> String.join(t.isEmpty() ? "" : "|", t, u)) + ").*";
    public static final String LOG_FILENAME_REGEX = String.format(".*%s", LOG_EXTENSIONS_REGEX);

    private LogStreamer() {

    }

    private static void createDirectories() {
        try {
            if (Files.notExists(logsDirectory))
                Files.createDirectories(logsDirectory);
            if (Files.notExists(modelsDirectory))
                Files.createDirectories(modelsDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        createDirectories();
    }

    public static Path getLogsDirectory() {
        return logsDirectory;
    }

    public static Path getModelsDirectory() {
        return modelsDirectory;
    }

    public static FileFilter getLogFileFilter() {
        return logFileFilter;
    }

    public static FileFilter getModelFileFilter() {
        return modelFileFilter;
    }

    private static String getLogName() {
        return Application.getProject().getId() + LOG_EXTENSION;
    }

    private static boolean isLogFile(File dir, String name) {
        return name.matches(LOG_FILENAME_REGEX);
    }

    public static XLog parseLog() {
        XLog xLog = null;

        try {
            String logName = getLogName();
            System.out.println("Parse log " + logName);
            Path logPath = logsDirectory.resolve(logName);

            if (Files.isReadable(logPath))
                xLog = xesXmlParser.parse(logPath.toFile()).get(0);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return xLog;
    }

    public static void serializeLog(XLog xLog) {
        try {
            String logName = getLogName();
            System.out.println("Save log " + logName);
            Path logPath = logsDirectory.resolve(logName);

            OutputStream logOutputStream = new FileOutputStream(
                    Files.isWritable(logPath) ? logPath.toFile() : Files.createFile(logPath).toFile());

            xLog.removeIf(Collection::isEmpty);
            xesXmlSerializer.serialize(xLog, logOutputStream);

            logOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int countLogs() {
        return logsDirectory.toFile().listFiles(LogStreamer::isLogFile).length;
    }

    public static void exportZip(Path filePath, File[] files) {
        try (OutputStream outputStream = new FileOutputStream(Files.createFile(filePath).toFile());
                ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {

            for (File file : files) {
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipOutputStream.putNextEntry(zipEntry);

                try (FileInputStream logInputStream = new FileInputStream(file)) {
                    byte[] bytes = new byte[logInputStream.available()];
                    if (logInputStream.read(bytes) != -1)
                        zipOutputStream.write(bytes);
                }
            }

            logger.info("Files exported in %s", filePath.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportLogs(Path directoryPath) {
        String fileName = Application.getTimestampString() + ZIP_EXTENSION;
        Path filePath = directoryPath.resolve(fileName);

        exportZip(filePath, logsDirectory.toFile().listFiles(LogStreamer::isLogFile));
    }
}
