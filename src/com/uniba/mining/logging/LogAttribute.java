package com.uniba.mining.logging;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttribute;

import com.uniba.mining.logging.extensions.XIdentityExtension;

enum LogAttribute {
    /* Log attributes */
    PROCESS_ID(XIdentityExtension.KEY_ID, Type.LITERAL, "Process Id", Logger.xIdentityExtension),
    PROCESS_NAME(XConceptExtension.KEY_NAME, Type.LITERAL, "Log Name", Logger.xConceptExtension),
    PROCESS_TIMESTAMP("CaseTimestamp", Type.TIMESTAMP, Instant.now().toEpochMilli()),
    PRODUCT_NAME("ProductName", Type.LITERAL, "Product Name"),
    PRODUCT_VERSION("ProductVersion", Type.LITERAL, "Product Version"),
    PRODUCT_BUILD("ProductBuild", Type.LITERAL, "Product Build"),
    /* Trace attributes */
    CASE_ID(XIdentityExtension.KEY_ID, Type.LITERAL, "Case Id", Logger.xIdentityExtension, false, true),
    CASE_NAME(XConceptExtension.KEY_NAME, Type.LITERAL, "Case Name", Logger.xConceptExtension, false, true),
    CASE_TIMESTAMP("CaseTimestamp", Type.TIMESTAMP, Instant.now().toEpochMilli(), false, true),
    AUTHOR_NAME("AuthorName", Type.LITERAL, "Author Name", false, true),
    PROJECT_NAME("ProjectName", Type.LITERAL, "Project Name", false, true),
    /* Event attributes */
    ACTIVITY_ID(XIdentityExtension.KEY_ID, Type.LITERAL, "Activity Id", Logger.xIdentityExtension),
    ACTIVITY_NAME(XConceptExtension.KEY_NAME, Type.LITERAL, "Activity Name", Logger.xConceptExtension, true),
    ACTIVITY_INSTANCE(XConceptExtension.KEY_INSTANCE, Type.LITERAL, "Activity Instance", Logger.xConceptExtension,
            true),
    ACTIVITY_TIMESTAMP(XTimeExtension.KEY_TIMESTAMP, Type.TIMESTAMP, Instant.now().toEpochMilli(),
            Logger.xTimeExtension, true),
    DIAGRAM_ID("DiagramId", Type.LITERAL, "Diagram Id", true),
    DIAGRAM_TYPE("DiagramType", Type.LITERAL, "Diagram Type", true),
    DIAGRAM_NAME("DiagramName", Type.LITERAL, "Diagram Name", true),
    UML_ELEMENT_ID("UMLElementId", Type.LITERAL, "UML Element Id", true),
    UML_ELEMENT_TYPE("UMLElementType", Type.LITERAL, "UML Element Type", true),
    UML_ELEMENT_NAME("UMLElementName", Type.LITERAL, "UML Element Name", true),
    PROPERTY_NAME("PropertyName", Type.LITERAL, "Property Name"),
    PROPERTY_VALUE("PropertyValue", Type.LITERAL, "Property Value"),
    /* Extra event attributes */
    UML_ELEMENT_CHILDREN("UMLElementChildren", Type.LITERAL, "UML Element Children"),
    PARAMETERS("Parameters", Type.LITERAL, "Parameters"),
    RELATIONSHIP_FROM_END("RelationshipFromEnd", Type.LITERAL, "Relationship From End"),
    RELATIONSHIP_TO_END("RelationshipToEnd", Type.LITERAL, "Relationship To End");

    enum Type {
        LITERAL(String.class) {
            @Override
            public XAttribute createAttribute(String key, Object value, XExtension extension) {
                return Logger.xFactory.createAttributeLiteral(key, (String) value, extension);
            }
        },
        DISCRETE(Long.class) {
            @Override
            public XAttribute createAttribute(String key, Object value, XExtension extension) {
                return Logger.xFactory.createAttributeDiscrete(key, (Long) value, extension);
            }
        },
        CONTINUOUS(Double.class) {
            @Override
            public XAttribute createAttribute(String key, Object value, XExtension extension) {
                return Logger.xFactory.createAttributeContinuous(key, (Double) value, extension);
            }
        },
        BOOLEAN(Boolean.class) {
            @Override
            public XAttribute createAttribute(String key, Object value, XExtension extension) {
                return Logger.xFactory.createAttributeBoolean(key, (Boolean) value, extension);
            }
        },
        TIMESTAMP(Long.class) {
            @Override
            public XAttribute createAttribute(String key, Object value, XExtension extension) {
                return Logger.xFactory.createAttributeTimestamp(key, (Long) value, extension);
            }
        };

        private Class<?> valueClass;

        Type(Class<?> valueClass) {
            this.valueClass = valueClass;
        }

        public Class<?> getValueClass() {
            return valueClass;
        }

        public abstract XAttribute createAttribute(String key, Object value, XExtension extension);
    }

    public static Set<XExtension> getExtensions() {
        return Arrays.stream(LogAttribute.values())
                .map(LogAttribute::getExtension)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public static List<XEventClassifier> getEventClassifiers() {
        return Arrays.stream(LogAttribute.values()).filter(LogAttribute::isGlobalEvent)
                .map(LogAttribute::createClassifier)
                .collect(Collectors.toList());
    }

    public static List<XAttribute> getGlobalEventAttributes() {
        return Arrays.stream(LogAttribute.values()).filter(LogAttribute::isGlobalEvent)
                .map(LogAttribute::createAttribute)
                .collect(Collectors.toList());
    }

    public static List<XAttribute> getGlobalTraceAttributes() {
        return Arrays.stream(LogAttribute.values()).filter(LogAttribute::isGlobalTrace)
                .map(LogAttribute::createAttribute)
                .collect(Collectors.toList());
    }

    private String key;
    private Type type;
    private Object defaultValue;
    private boolean isGlobalEvent;
    private boolean isGlobalTrace;
    private XExtension extension;

    LogAttribute(String key, Type type, Object defaultValue, boolean isGlobalEvent, boolean isGlobalTrace) {
        this.key = key;
        this.type = type;
        this.defaultValue = defaultValue;
        this.isGlobalEvent = isGlobalEvent;
        this.isGlobalTrace = isGlobalTrace;
    }

    LogAttribute(String key, Type type, Object defaultValue, XExtension extension, boolean isGlobalEvent,
            boolean isGlobalTrace) {
        this(key, type, defaultValue, isGlobalEvent, isGlobalTrace);
        if (extension.getDefinedAttributes().stream()
                .noneMatch(xExtensionAttribute -> xExtensionAttribute.getKey().equals(key)))
            throw new UnsupportedOperationException(
                    String.format("Attribute %s is not defined by %s extension", key, extension.getName()));
        this.extension = extension;
    }

    LogAttribute(String key, Type type, Object defaultValue, XExtension extension, boolean isGlobalEvent) {
        this(key, type, defaultValue, extension, isGlobalEvent, false);
    }

    LogAttribute(String key, Type type, Object defaultValue, XExtension extension) {
        this(key, type, defaultValue, extension, false);
    }

    LogAttribute(String key, Type type, Object defaultValue, boolean isGlobalEvent) {
        this(key, type, defaultValue, isGlobalEvent, false);
    }

    LogAttribute(String key, Type type, Object defaultValue) {
        this(key, type, defaultValue, false);
    }

    public String getKey() {
        return key;
    }

    public Type getType() {
        return type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public XExtension getExtension() {
        return extension;
    }

    public boolean isGlobalEvent() {
        return isGlobalEvent;
    }

    public boolean isGlobalTrace() {
        return isGlobalTrace;
    }

    public XAttribute createAttribute() {
        return type.createAttribute(key, defaultValue, extension);
    }

    public XAttribute createAttribute(Object value) {
        return type.createAttribute(key, value, extension);
    }

    public XEventClassifier createClassifier() {
        return new XEventAttributeClassifier(String.format("%s Classifier", key), key);
    }
}
