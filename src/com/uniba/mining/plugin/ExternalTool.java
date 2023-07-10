package com.uniba.mining.plugin;

import java.io.File;
import java.nio.file.Path;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

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

    public static String[] getExecutionCommand(String externalToolPath) {
        if (externalToolPath.endsWith(EXECUTABLE_EXTENSION))
            return new String[] { "start", externalToolPath };
        if (externalToolPath.endsWith(BASH_EXTENSION))
            return new String[] { "bash", externalToolPath };
        if (externalToolPath.endsWith(JAR_EXTENSION)) {
            Path externalToolDirectoryPath = Path.of(externalToolPath).getParent();
            String java = externalToolDirectoryPath.resolve("jre" + File.separator + "bin" + File.separator + "java")
                    .toString();

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
