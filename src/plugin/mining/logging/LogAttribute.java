package plugin.mining.logging;

enum LogAttribute {
    PRODUCT_NAME("productName", Type.LITERAL),
    PRODUCT_VERSION("productVersion", Type.LITERAL),
    PRODUCT_BUILD("productBuild", Type.LITERAL),
    CREATED_AT("createdAt", Type.TIMESTAMP),
    CASE_ID("caseId", Type.LITERAL),
    AUTHOR_NAME("authorName", Type.LITERAL),
    PROJECT_NAME("projectName", Type.LITERAL),
    EVENT_ID("eventId", Type.LITERAL),
    ACTIVITY_NAME("activityName", Type.LITERAL),
    DIAGRAM_ID("diagramId", Type.LITERAL),
    DIAGRAM_TYPE("diagramType", Type.LITERAL),
    DIAGRAM_NAME("diagramName", Type.LITERAL),
    UML_ELEMENT_ID("umlElementId", Type.LITERAL),
    UML_ELEMENT_TYPE("umlElementType", Type.LITERAL),
    UML_ELEMENT_NAME("umlElementName", Type.LITERAL),
    PROPERTY_NAME("propertyName", Type.LITERAL),
    PROPERTY_VALUE("propertyValue", Type.LITERAL);

    enum Type {
        LITERAL(String.class),
        DISCRETE(Long.class),
        CONTINUOUS(Double.class),
        BOOLEAN(Boolean.class),
        TIMESTAMP(Long.class);

        private Class<?> valueType;

        Type(Class<?> valueType) {
            this.valueType = valueType;
        }

        public Class<?> getValueType() {
            return valueType;
        }
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
