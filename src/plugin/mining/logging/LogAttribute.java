package plugin.mining.logging;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.model.XAttribute;

enum LogAttribute {
    PRODUCT_NAME("ProductName", Type.LITERAL),
    PRODUCT_VERSION("ProductVersion", Type.LITERAL),
    PRODUCT_BUILD("ProductBuild", Type.LITERAL),
    CREATED_AT("CreatedAt", Type.TIMESTAMP),
    CASE_ID("CaseId", Type.LITERAL),
    AUTHOR_NAME("AuthorName", Type.LITERAL),
    PROJECT_NAME("ProjectName", Type.LITERAL),
    EVENT_ID("EventId", Type.LITERAL),
    ACTIVITY_NAME("ActivityName", Type.LITERAL),
    DIAGRAM_ID("DiagramId", Type.LITERAL),
    DIAGRAM_TYPE("DiagramType", Type.LITERAL),
    DIAGRAM_NAME("DiagramName", Type.LITERAL),
    UML_ELEMENT_ID("UMLElementId", Type.LITERAL),
    UML_ELEMENT_TYPE("UMLElementType", Type.LITERAL),
    UML_ELEMENT_NAME("UMLElementName", Type.LITERAL),
    PROPERTY_NAME("PropertyName", Type.LITERAL),
    PROPERTY_VALUE("PropertyValue", Type.LITERAL);

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

    private String name;
    private Type type;

    LogAttribute(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
}
