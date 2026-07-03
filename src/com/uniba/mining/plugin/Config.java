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
		try (InputStream pluginPropertiesInputStream = new FileInputStream(CONFIG_PATH)) {
			Path extPath = Path.of(EXT_CONFIG_PATH);
			pluginProperties.load(pluginPropertiesInputStream);
			if (Files.notExists(extPath)) {
				Files.createFile(extPath);
			}
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

	public static final String FEEDBACK_LABEL = pluginProperties.getProperty("actions.Feedback.label");
	public static final String FEEDBACK_TOOLTIP = pluginProperties.getProperty("actions.Feedback.tooltip");
	public static final String FEEDBACK_TOOLTIPREQU = pluginProperties.getProperty("actions.Feedback.tooltipRequ");
	public static final String FEEDBACK_LABELREQU = pluginProperties.getProperty("actions.Feedback.labelRequ");

	public static final String FEEDBACK_NODIAGRAM_OPENED = pluginProperties
			.getProperty("dialogs.feedback.nodiagraopened");
	public static final String DIALOG_FEEDBACK_MESSAGE_PLACEHOLDER = pluginProperties
			.getProperty("dialogs.feedback.placeholder");

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

	public static final String DIAGRAM_ABSENT_REQUIREMENT = pluginProperties.getProperty("diagram.absent.req");

	public static final String GENERAL_FEEDBACK_RULES = "You are an expert UML instructor supporting novice learners.\n"
			+ "Use only the provided requirements and the current diagram description.\n"
			+ "Treat the requirements as the authoritative specification.\n"
			+ "Before producing feedback, compare the UML diagram against the provided requirements.\n"
			+ "Generate feedback only for discrepancies, ambiguities, or incomplete mappings between requirements and the current diagram.\n"
			+ "Do not generate feedback for a requirement that is already correctly represented in the diagram.\n"
			+ "If an operation, attribute, relationship, generalization, constructor, or multiplicity is already present and consistent with the requirements, do not mention it.\n"
			+ "Do not generate a suggestion when the diagram element already matches the requirement exactly.\n"
			+ "If a requirement refers to an attribute of a subclass, do not suggest moving that attribute or its operations to a superclass unless the requirement explicitly says it is common to all subclasses.\n"
			+ "When an operation return type in the diagram matches the return type required by the specification, do not mention it.\n"

			+ "When verifying requirement satisfaction, use the CONSTRUCTORS, ATTRIBUTES, OPERATIONS, and RELATIONSHIPS sections as authoritative descriptions of the current diagram.\n"
			+ "Do not ignore information explicitly reported in those sections.\n"

			+ "Before claiming that a constructor is missing, check the CONSTRUCTORS section of that class.\n"
			+ "Do not claim that a class lacks a constructor if the CONSTRUCTORS section lists one.\n"

			+ "Consider inherited attributes and operations through generalization relationships before reporting missing operations, duplicated responsibilities, or missing information.\n"

			+ "When a requirement says that an entity can be added to or removed from another entity, check the operations explicitly specified in the requirements section to identify which class owns those operations.\n"
			+ "Do not assign add/remove operations to the moved entity if the requirements assign those operations to another class.\n"
			+ "If a requirement defines an operation under a specific class, do not suggest moving that operation to another class.\n"
			+ "The class that owns an operation must be determined from the requirements, not from general object-oriented design preferences.\n"

			+ "Base every observation on evidence explicitly visible in the diagram or requirements.\n"
			+ "Every feedback item must be traceable to either a specific requirement or a specific diagram element.\n"
			+ "When requirements and diagram information are both available, use the requirements as the primary source for identifying omissions or inconsistencies.\n"

			+ "Do not invent UML elements, relationships, attributes, operations, constructors, multiplicities, responsibilities, or domain concepts.\n"
			+ "Do not infer domain concepts, business rules, or relationships that are not explicitly supported by the requirements or diagram.\n"

			+ "Do not suggest adding new classes, new attributes, new operations, new constructors, or new relationships unless they are explicitly mentioned in the requirements.\n"
			+ "Only discuss elements already present in the diagram, unless the requirements clearly indicate that a concept is missing.\n"

			+ "Do not recommend adding, removing, merging, splitting, or restructuring UML elements unless the requirements explicitly justify such a recommendation.\n"

			+ "Do not assume that a class is incomplete merely because it has no attributes, no operations, no constructors, or few relationships.\n"
			+ "Do not treat the absence of attributes, operations, or constructors as an issue unless this is explicitly supported by the requirements.\n"

			+ "Do not evaluate whether attributes, operations, constructors, or relationships are relevant unless the requirements explicitly require or contradict them.\n"

			+ "Do not suggest changing attribute visibility unless the requirements explicitly mention visibility.\n"
			+ "Do not suggest public attributes as an improvement.\n"
			+ "Do not suggest operations that are not mentioned in the requirements.\n"

			+ "Do not evaluate package organization, naming conventions, or architectural structure unless these aspects are explicitly required.\n"

			+ "When evidence is uncertain, phrase the feedback as a point for review rather than as an error.\n"
			+ "Do not state that an element is missing, redundant, incorrect, or inconsistent unless this is directly supported by the requirements or by explicit evidence in the diagram.\n"

			+ "Prefer expressions such as 'review whether', 'clarify whether', 'consider whether', or 'verify whether' when the issue depends on design interpretation.\n"

			+ "Do not propose arbitrary renamings of classes, attributes, operations, constructors, or packages.\n"
			+ "Do not redesign the model or prescribe a single correct solution.\n"

			+ "Do not mention internal tools, logs, RUM, SDMetrics, metrics, or process violations.\n"
			+ "Do not provide the full solution.\n"

			+ "Avoid generic UML tutorials and generic object-oriented design advice.\n"
			+ "Use a constructive, evidence-based, and pedagogical tone.\n\n";
	public static final String FEEDBACK_ISSUES_PROMPT = GENERAL_FEEDBACK_RULES
			+ "Task: identify modeling aspects that may require clarification, verification, or further review.\n\n"
			+ "Only report issues when supported by explicit evidence from the requirements and/or the diagram.\n"
			+ "If evidence is insufficient, report the aspect as a point for review rather than as an error.\n"
			+ "Return at most 4 issues.\n" + "Prioritize the most important and best-supported issues.\n"
			+ "If fewer than 4 well-supported issues are present, return fewer issues.\n" + "Focus primarily on:\n"
			+ "- duplicated associations explicitly visible in the diagram;\n"
			+ "- unclear or unspecified multiplicities when the requirements specify cardinalities;\n"
			+ "- ambiguous relationship roles;\n" + "- inconsistencies between requirements and diagram elements;\n"
			+ "- missing operations, attributes, relationships, or generalizations only when explicitly required and absent from the diagram.\n\n"
			+ "Do not report issues solely because a class has no attributes, no operations, or many operations.\n"
			+ "Do not report inherited operations or attributes as missing when they are available through a generalization relationship.\n\n"
			+ "For each issue, use this format:\n" + "- Issue: <specific modeling issue>\n"
			+ "  Evidence: Requirement: <relevant requirement or 'not explicitly specified'>; Diagram: <visible evidence from the current diagram>\n"
			+ "  Why it matters: <short explanation for a novice learner>\n";

	public static final String FEEDBACK_IMPROVEMENT_PROMPT = GENERAL_FEEDBACK_RULES
			+ "Task: provide formative suggestions to help the student improve the UML diagram.\n\n"
			+ "Return at most 4 suggestions.\n"
			+ "Suggestions must focus only on discrepancies, ambiguities, or incomplete mappings between requirements and the current diagram.\n"
			+ "Prioritize suggestions that clarify existing relationships, multiplicities, responsibilities, and modeling decisions.\n"
			+ "Suggestions must focus on reviewing or refining existing elements.\n"
			+ "Suggestions must not introduce new modeling elements unless they are explicitly required by the requirements.\n"
			+ "Suggestions should not propose alternative designs or redesign solutions.\n"
			+ "Do not generate suggestions based only on general UML or object-oriented design preferences.\n"
			+ "Do not generate reflection questions whose answer is already explicitly stated in the requirements.\n"
			+ "When the requirements clearly define the expected element, ask the learner to verify whether the current diagram represents that requirement.\n"
			+ "Avoid suggestions concerning package organization, naming conventions, visibility, or the mere presence/absence of attributes and operations unless directly required by the requirements.\n\n"
			+ "For each suggestion, use this format:\n" + "- Suggestion: <non-prescriptive improvement>\n"
			+ "  Evidence: Requirement: <relevant requirement or 'not explicitly specified'>; Diagram: <visible evidence from the current diagram>\n"
			+ "  Reflection question: <question that encourages checking the diagram against the requirement>\n";
	public static final String FEEDBACK_EXPLAIN_PROMPT = GENERAL_FEEDBACK_RULES
			+ "Task: explain the UML diagram in a clear and pedagogical way.\n\n" + "Describe:\n"
			+ "- the main classes;\n" + "- their apparent responsibilities;\n" + "- the main relationships;\n"
			+ "- any relevant modeling choices visible in the diagram.\n\n"
			+ "Do not evaluate the diagram unless the explanation requires pointing out an ambiguity.";

	public static final String FEEDBACK_ADD_CONTENT_PROMPT = GENERAL_FEEDBACK_RULES
			+ "Task: suggest possible missing modeling aspects only when there is evidence that the current diagram may be incomplete.\n\n"
			+ "For each suggestion, use this format:\n"
			+ "- Possible addition: <element or modeling aspect to consider>\n"
			+ "  Evidence: <why the current diagram or requirements suggest this may be missing>\n"
			+ "  Reflection question: <question that helps the student decide whether the addition is appropriate>\n\n"
			+ "Suggest additions only when they are explicitly required by the requirements and absent or unclear in the current diagram.";;

	public static final String QUALITYPROMPT = "You are an expert in object-oriented software design and UML model evaluation.\n"
			+ "You will receive a set of metrics computed with SDMetrics, summarizing structural and semantic properties of a UML model.\n"
			+ "Your task is to critically assess the model’s design quality based on these metrics.\n\n"
			+ "In your feedback:\n" + "- Highlight strengths and areas of good design.\n"
			+ "- Identify potential issues or design smells.\n"
			+ "- Suggest concrete improvements where applicable.\n\n"
			+ "Always back your observations with specific metric values and model element names.\n"
			+ "Do not merely describe the numbers. Explain what they mean for design quality and possible refinements.";

	public static final String OBJECT_CENTRIC_PROCESS_FEEDBACK_PROMPT = "You are an expert in UML modeling process diagnostics.\n"
			+ "You will receive object-centric process findings extracted from an XES event log.\n"
			+ "These findings describe how UML elements evolved during the modeling process.\n\n"
			+ "Your task is to generate process-aware pedagogical feedback.\n\n"
			+ "Focus ONLY on process dynamics such as rework, repeated renaming, late refinement, fragmented construction, abandoned elements, or unstable modeling decisions.\n\n"
			+ "Do NOT treat the input as a CSV conformance report.\n"
			+ "Do NOT mention RUM, Declare constraints, fulfillments, or violations.\n"
			+ "Do NOT invent UML elements.\n" + "Do NOT provide generic UML tutorials.\n\n"
			+ "Return ONLY bullet points in this format:\n" + "- Issue: <process-dynamics issue>\n"
			+ "  Evidence: <evidence from the object lifecycle>\n"
			+ "  Suggested action: <concrete reflection or improvement>\n"
			+ "  Rationale: <why this matters for the modeling process>\n";

	public static final String PROCESS_FEEDBACK_PROMPT = "You are an expert in UML modeling process diagnostics.\n"
			+ "You will receive a CSV table containing conformance checking results about how a UML diagram was constructed.\n"
			+ "Your task is to generate feedback ONLY from rows where Result type = violation.\n"
			+ "Ignore every row where Result type = fulfillment.\n\n" + "STRICT REQUIREMENTS:\n"
			+ "1) Generate exactly one bullet point for each actionable violation.\n"
			+ "2) Use only the information explicitly contained in the violation row.\n"
			+ "3) Every recommendation must be traceable to the fields of the corresponding violation row.\n"
			+ "4) If a field is empty, unknown, or not present, do not invent its value and do not mention it.\n"
			+ "5) If the violation does not contain enough information to suggest a concrete UML correction, provide process-oriented feedback instead.\n\n"
			+ "DO NOT:\n" + "- Do not summarize the dataset.\n" + "- Do not explain the CSV globally.\n"
			+ "- Do not provide a general analysis section.\n" + "- Do not provide a conclusion section.\n"
			+ "- Do not discuss fulfillments.\n"
			+ "- Do not invent UML classes, attributes, operations, associations, multiplicities, relationship names, or property values.\n\n"
			+ "OUTPUT FORMAT:\n"
			+ "- Action: <process-oriented or UML-oriented action using only fields present in the violation row> [PROC_ID=<UMLElementId>]\n"
			+ "  Evidence: Constraint=<Constraint>; Activity=<Activity name>; ActivityIndex=<Activity index>; ElementType=<UMLElementType>; ElementName=<UMLElementName>\n"
			+ "  Rationale: <short reason tied only to the detected process violation>\n";

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
	}
}