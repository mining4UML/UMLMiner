package com.uniba.mining.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import com.uniba.mining.utils.Application;

public class Config {
    private static final String ROOT_PATH = Application.getPluginInfo("UMLMiner").getPluginDir().getAbsolutePath();
    private static final String CONFIG_PATH = String.join(File.separator, ROOT_PATH, "plugin.properties");
    private static final String EXT_CONFIG_PATH = String.join(File.separator, ROOT_PATH, "ext.properties");
    private static final String ASSETS_PATH = String.join("/", "", "assets");
    public static final String IMAGES_PATH = String.join("/", ASSETS_PATH, "images");
    public static final String ICONS_PATH = String.join("/", ASSETS_PATH, "icons");
    private static final Properties pluginProperties = new Properties();
    private static final Properties extProperties = new Properties();
    static {
        try (InputStream pluginPropertiesInputStream = new FileInputStream(CONFIG_PATH);) {
            Path extPath = Path.of(EXT_CONFIG_PATH);
            pluginProperties.load(pluginPropertiesInputStream);
            if (Files.notExists(extPath))
                Files.createFile(extPath);
            extProperties.load(new FileInputStream(EXT_CONFIG_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static final String PLUGIN_ID = pluginProperties.getProperty("plugin.id");
    public static final String PLUGIN_NAME = pluginProperties.getProperty("plugin.name");
    public static final String PLUGIN_DESCRIPTION = pluginProperties.getProperty("plugin.description");
    public static final String PLUGIN_VERSION = pluginProperties.getProperty("plugin.version");
    public static final String PLUGIN_PROVIDER = pluginProperties.getProperty("plugin.provider");
    public static final String PLUGIN_HOMEPAGE = pluginProperties.getProperty("plugin.homepage");

    public static String getExternalToolPath(ExternalTool externalTool) {
        return extProperties.getProperty(String.join(".", externalTool.getName(), "path"));
    }

    public static void setExternalToolPath(ExternalTool externalTool, String path) {
        extProperties.setProperty(String.join(".", externalTool.getName(), "path"), path);
    }

    public static void storeExtProperties() {
        try (OutputStream extPropertiesOutputStream = new FileOutputStream(EXT_CONFIG_PATH)) {
            extProperties.store(extPropertiesOutputStream, "External Tools Properties");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Config() {
        // Empty
    }
}
