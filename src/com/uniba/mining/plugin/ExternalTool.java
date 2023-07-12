package com.uniba.mining.plugin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang.SystemUtils;

public enum ExternalTool {
    RUM("RuM"),
    DISCO("Disco"),
    PROM("ProM");

    private static final String EXECUTABLE_EXTENSION = "exe";
    private static final String BASH_EXTENSION = "sh";
    private static final String JAR_EXTENSION = "jar";

    private static final FileFilter externalToolExecutableFileFilter = new FileNameExtensionFilter(
            String.format("Executable (*.%s)", EXECUTABLE_EXTENSION), EXECUTABLE_EXTENSION);
    private static final FileFilter externalToolBashFileFilter = new FileNameExtensionFilter(
            String.format("Bash Script (*.%s)", BASH_EXTENSION), BASH_EXTENSION);
    private static final FileFilter externalToolJarFileFilter = new FileNameExtensionFilter(
            String.format("Java Archive (*.%s)", JAR_EXTENSION), JAR_EXTENSION);

    public static ExternalTool getExternalTool(String name) {
        for (ExternalTool externalTool : ExternalTool.values()) {
            if (externalTool.name.equals(name))
                return externalTool;
        }
        throw new UnsupportedOperationException("External tool " + name + " not found");
    }

    public static String getBundledJavaHome(String externalToolDirectory) {
        String javaFolderName = "";
        if (SystemUtils.IS_OS_WINDOWS)
            javaFolderName = "windows";
        else if (SystemUtils.IS_OS_MAC)
            javaFolderName = "osx";
        else if (SystemUtils.IS_OS_UNIX)
            javaFolderName = "linux";

        return Path.of(externalToolDirectory, "jre", javaFolderName,
                SystemUtils.IS_OS_MAC ? "Contents" : "",
                SystemUtils.IS_OS_MAC ? "Home" : "").toString();
    }

    public static String[] getExecutionCommand(String externalToolPath) {
        if (externalToolPath.endsWith(EXECUTABLE_EXTENSION))
            return new String[] { "start", externalToolPath };
        if (externalToolPath.endsWith(BASH_EXTENSION))
            return new String[] { "bash", externalToolPath };
        if (externalToolPath.endsWith(JAR_EXTENSION)) {
            String externalToolDirectoryPath = Path.of(externalToolPath).getParent().toString();
            String bundledJavaHome = getBundledJavaHome(externalToolDirectoryPath);
            String javaHome = Files.isDirectory(Path.of(bundledJavaHome)) ? bundledJavaHome
                    : System.getenv("JAVA_HOME");
            String java = javaHome + File.separator + "bin" + File.separator + "java";
            if (!new File(java).setExecutable(true))
                System.err.println("Error to set executable permission on: " + java);
            return new String[] { java, "-jar", externalToolPath };
        }
        return new String[] { externalToolPath };
    }

    public static FileFilter getExternalToolExecutableFileFilter() {
        return externalToolExecutableFileFilter;
    }

    public static FileFilter getExternalToolBashFileFilter() {
        return externalToolBashFileFilter;
    }

    public static FileFilter getExternalToolJarFileFilter() {
        return externalToolJarFileFilter;
    }

    private String name;

    ExternalTool(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
