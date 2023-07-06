package com.uniba.mining.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.uniba.mining.utils.Application;

public class Config {
    private static final String ROOT_PATH = Application.getPluginInfo("UMLMiner").getPluginDir().getAbsolutePath();
    private static final String CONFIG_PATH = String.join(File.separator, ROOT_PATH, "plugin.properties");
    private static final String ASSETS_PATH = String.join("/", "", "assets");
    public static final String IMAGES_PATH = String.join("/", ASSETS_PATH, "images");
    public static final String ICONS_PATH = String.join("/", ASSETS_PATH, "icons");
    private static final Properties properties = new Properties();
    static {
        try {
            properties.load(new FileInputStream(CONFIG_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static final String PLUGIN_ID = properties.getProperty("plugin.id");
    public static final String PLUGIN_NAME = properties.getProperty("plugin.name");
    public static final String PLUGIN_DESCRIPTION = properties.getProperty("plugin.description");
    public static final String PLUGIN_VERSION = properties.getProperty("plugin.version");
    public static final String PLUGIN_PROVIDER = properties.getProperty("plugin.provider");
    public static final String PLUGIN_HOMEPAGE = properties.getProperty("plugin.homepage");
    public static final String RUM_PATH = properties.getProperty("rum.path");

    private Config() {
        // Empty
    }
}
