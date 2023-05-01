package plugin.mining.logging;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.model.XAttribute;

enum LogAttribute {
    PRODUCT_NAME("ProductName", Type.LITERAL, "Product Name"),
    PRODUCT_VERSION("ProductVersion", Type.LITERAL, "Product Version"),
    PRODUCT_BUILD("ProductBuild", Type.LITERAL, "Product Build"),
    CREATED_AT("CreatedAt", Type.TIMESTAMP, Instant.now().toEpochMilli(), true, true),
    CASE_ID("CaseId", Type.LITERAL, "Case Id", false, true),
    AUTHOR_NAME("AuthorName", Type.LITERAL, "Author Name", false, true),
    PROJECT_NAME("ProjectName", Type.LITERAL, "Project Name", false, true),
    EVENT_ID("EventId", Type.LITERAL, "Event Id", true),
    ACTIVITY_NAME("ActivityName", Type.LITERAL, "Activity Name", true),
    DIAGRAM_ID("DiagramId", Type.LITERAL, "Diagram Id", true),
    DIAGRAM_TYPE("DiagramType", Type.LITERAL, "Diagram Type", true),
    DIAGRAM_NAME("DiagramName", Type.LITERAL, "Diagram Name", true),
    UML_ELEMENT_ID("UMLElementId", Type.LITERAL, "UML Element Id", true),
    UML_ELEMENT_TYPE("UMLElementType", Type.LITERAL, "UML Element Type", true),
    UML_ELEMENT_NAME("UMLElementName", Type.LITERAL, "UML Element Name"),
    PROPERTY_NAME("PropertyName", Type.LITERAL, "Property Name"),
    PROPERTY_VALUE("PropertyValue", Type.LITERAL, "Property Value");

    enum Type {
        LITERAL(String.class) {
            @Override
            public XAttribute createAttribute(String key, Object value, XExtension xExtension) {
                return Logger.xFactory.createAttributeLiteral(key, (String) value, xExtension);
            }
        },
        DISCRETE(Long.class) {
            @Override
            public XAttribute createAttribute(String key, Object value, XExtension xExtension) {
                return Logger.xFactory.createAttributeDiscrete(key, (Long) value, xExtension);
            }
        },
        CONTINUOUS(Double.class) {
            @Override
            public XAttribute createAttribute(String key, Object value, XExtension xExtension) {
                return Logger.xFactory.createAttributeContinuous(key, (Double) value, xExtension);
            }
        },
        BOOLEAN(Boolean.class) {
            @Override
            public XAttribute createAttribute(String key, Object value, XExtension xExtension) {
                return Logger.xFactory.createAttributeBoolean(key, (Boolean) value, xExtension);
            }
        },
        TIMESTAMP(Long.class) {
            @Override
            public XAttribute createAttribute(String key, Object value, XExtension xExtension) {
                return Logger.xFactory.createAttributeTimestamp(key, (Long) value,
                        xExtension != null ? xExtension : Logger.xTimeExtension);
            }
        };

        private Class<?> valueClass;

        Type(Class<?> valueClass) {
            this.valueClass = valueClass;
        }

        public Class<?> getValueClass() {
            return valueClass;
        }

        public abstract XAttribute createAttribute(String key, Object value, XExtension xExtension);
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

    LogAttribute(String key, Type type, Object defaultValue, boolean isGlobalEvent, boolean isGlobalTrace) {
        this.key = key;
        this.type = type;
        this.defaultValue = defaultValue;
        this.isGlobalEvent = isGlobalEvent;
        this.isGlobalTrace = isGlobalTrace;
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

    public boolean isGlobalEvent() {
        return isGlobalEvent;
    }

    public boolean isGlobalTrace() {
        return isGlobalTrace;
    }

    public XAttribute createAttribute() {
        return type.createAttribute(key, defaultValue, null);
    }

    public XAttribute createAttribute(Object value) {
        return type.createAttribute(key, value, null);
    }

    public XEventClassifier createClassifier() {
        return new XEventAttributeClassifier(String.format("%s Classifier", key), key);
    }
}
