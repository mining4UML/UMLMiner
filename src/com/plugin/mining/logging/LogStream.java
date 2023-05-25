package com.plugin.mining.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlSerializer;

import com.plugin.mining.util.Application;

public class LogStream {
    private static final String LOG_EXTENSION = ".xes";
    private static final String ZIP_EXTENSION = ".zip";
    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd_HH.mm.ss";
    private static final String USER_NAME = System.getProperty("user.name");
    private static final String USER_DIR = System.getProperty("user.dir");
    private static final Path logDirectory = Paths.get(USER_DIR, "logs", USER_NAME);
    private static final Logger logger = new Logger(LogStream.class);
    private static final XesXmlParser xesXmlParser = new XesXmlParser();
    private static final XesXmlSerializer xesXmlSerializer = new XesXmlSerializer();
    private static final Set<String> logExtensions = new HashSet<>(
            Arrays.asList("xes", "csv", "jsoncel", "xmlocel"));
    private static final String LOG_FILENAME_REGEX = String.format(".*\\.(%s)",
            logExtensions.stream().reduce("", (t, u) -> String.join("|", t, u)));

    private LogStream() {

    }

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
            Path logPath = logDirectory.resolve(logName);

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
            Path logPath = logDirectory.resolve(logName);

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
        return logDirectory.toFile().listFiles(LogStream::isLogFile).length;
    }

    public static void exportLogs(Path directoryPath) {
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT)) + ZIP_EXTENSION;
        Path filePath = directoryPath.resolve(fileName);

        try (OutputStream logsOutputStream = new FileOutputStream(Files.createFile(filePath).toFile());
                ZipOutputStream zipOutputStream = new ZipOutputStream(logsOutputStream)) {

            for (File logFile : logDirectory.toFile().listFiles(LogStream::isLogFile)) {
                ZipEntry zipEntry = new ZipEntry(logFile.getName());
                zipOutputStream.putNextEntry(zipEntry);

                try (FileInputStream logInputStream = new FileInputStream(logFile)) {
                    byte[] bytes = new byte[logInputStream.available()];
                    if (logInputStream.read(bytes) != -1)
                        zipOutputStream.write(bytes);
                }
            }

            logger.info("Logs exported in %s", directoryPath.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
