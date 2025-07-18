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
	public static final String PLUGIN_LICENSEFILE = pluginProperties.getProperty("plugin.license");
	public static final String PLUGIN_TEAM = pluginProperties.getProperty("plugin.team");

	public static final String EXPORT_INFO_ACTION = pluginProperties.getProperty("actions.ExportInfo.label");
	public static final String EXPORT_INFO_OK = pluginProperties.getProperty("actions.export.infodiag.successfully");
	public static final String EXPORT_INFO_ERROR = pluginProperties.getProperty("actions.export.infodiag.error");

	public static final String EXPORT_VIOLATIONS_ACTION = pluginProperties.getProperty("actions.Violations.label");
	public static final String EXPORT_VIOLATIONS_OK = pluginProperties
			.getProperty("actions.export.violations.successfully");
	public static final String EXPORT_VIOLATIONS_INPUT_ERROR = pluginProperties
			.getProperty("actions.export.violations.inputerror");

	public static final String FEEDBACK_LABEL= pluginProperties
			.getProperty("actions.Feedback.label");
	public static final String FEEDBACK_TOOLTIP= pluginProperties
			.getProperty("actions.Feedback.tooltip");
	public static final String FEEDBACK_TOOLTIPREQU= pluginProperties
			.getProperty("actions.Feedback.tooltipRequ");
	public static final String FEEDBACK_LABELREQU= pluginProperties
			.getProperty("actions.Feedback.labelRequ");

	

	public static final String FEEDBACK_NODIAGRAM_OPENED= pluginProperties.getProperty("dialogs.feedback.nodiagraopened");
	public static final String DIALOG_FEEDBACK_MESSAGE_PLACEHOLDER = pluginProperties.getProperty("dialogs.feedback.placeholder");
	public static final String FEEDBACK_BUTTON_ADD = pluginProperties.getProperty("dialogs.feedback.button.add");
	public static final String FEEDBACK_BUTTON_IMROVEMENT = pluginProperties
			.getProperty("dialogs.feedback.button.improvement");
	public static final String FEEDBACK_BUTTON_ISSUES = pluginProperties.getProperty("dialogs.feedback.button.issues");
	public static final String FEEDBACK_BUTTON_EXPLAIN = pluginProperties
			.getProperty("dialogs.feedback.button.explain");
	public static final String FEEDBACK_PREFIX_ANSWER = pluginProperties.getProperty("dialogs.feedback.prefixAnswer");
	public static final String FEEDBACK_TITLE = pluginProperties.getProperty("plugin.name") + " - "
			+ pluginProperties.getProperty("dialogs.feedback.name");
	
	public static final String FEEDBACK_SUFFIX_COUNT_LABEL = pluginProperties
			.getProperty("dialogs.feedback.suffixCountLabel");

	public static final String FEEDBACK_CHAR_COUNTING = pluginProperties
			.getProperty("dialogs.feedback.label.characters");

	public static final String PLUGIN_WINDOWS_SEPARATOR = " - ";
	
	public static final String FEEDBACK_BUTTON_MODELING = "Provide feedback on the modeling process";
	public static final String FEEDBACK_BUTTON_QUALITY = "Provide feedback on the design quality";

	
	public static final String QUALITYPROMPT = 
		    "You are an expert in object-oriented software design.\n" +
		    "Based on the following Obbject Oriented summaries, analyze the quality of the UML class diagram.\n" +
		    "Focus on the following aspects using the metric values:\n\n" +
		    "- Class size and complexity (NumAttr, NumOps)\n" +
		    "- Encapsulation (Setters, Getters, public methods)\n" +
		    "- Inheritance structure (DIT, NOC, NumDesc)\n" +
		    "- Potential code smells or anti-patterns (e.g., missing accessors, data classes, deep inheritance)\n\n" +
		    "Please provide actionable feedback, citing specific metric values or class names where appropriate.";

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
