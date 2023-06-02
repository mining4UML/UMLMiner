package com.uniba.mining.plugin;

public class Config {
    public static final String PLUGIN_ID = "UMLMiner";
    public static final String PLUGIN_NAME = "UML Miner";
    private static final String ROOT_PATH = "";
    private static final String ASSETS_PATH = String.join("/", ROOT_PATH, "assets");
    public static final String IMAGES_PATH = String.join("/", ASSETS_PATH, "images");
    public static final String ICONS_PATH = String.join("/", ASSETS_PATH, "icons");

    private Config() {
        // Empty
    }
}
