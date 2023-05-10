package com.plugin.mining.logging;

public enum LogActivity {
    ADD_CLASS("Add {{type}}Class", ActionType.ADD, ModelType.CLASS),
    ADD_ATTRIBUTE(ActionType.ADD, ModelType.ATTRIBUTE),
    ADD_OPERATION(ActionType.ADD, ModelType.OPERATION),
    ADD_PARAMETER("Add Parameter to {{type}}", ActionType.ADD, ModelType.PARAMETER),
    ADD_RECEPTION(ActionType.ADD, ModelType.RECEPTION),
    ADD_RELATIONSHIP(ActionType.ADD, ModelType.RELATIONSHIP),
    ADD_USE_CASE(ActionType.ADD, ModelType.USE_CASE),
    UPDATE_CLASS("Update {{propertyName}} property for {{type}}Class", ActionType.UPDATE, ModelType.CLASS),
    UPDATE_ATTRIBUTE(ActionType.UPDATE, ModelType.ATTRIBUTE),
    UPDATE_OPERATION(ActionType.UPDATE, ModelType.OPERATION),
    UPDATE_PARAMETER("Update {{propertyName}} property for {{type}} Parameter", ActionType.UPDATE, ModelType.PARAMETER),
    UPDATE_RECEPTION(ActionType.UPDATE, ModelType.RECEPTION),
    UPDATE_RELATIONSHIP(ActionType.UPDATE, ModelType.RELATIONSHIP),
    UPDATE_USE_CASE(ActionType.UPDATE, ModelType.USE_CASE),
    REMOVE_CLASS("Remove {{type}}Class", ActionType.REMOVE, ModelType.CLASS),
    REMOVE_ATTRIBUTE(ActionType.REMOVE, ModelType.ATTRIBUTE),
    REMOVE_OPERATION(ActionType.REMOVE, ModelType.OPERATION),
    REMOVE_PARAMETER("Remove Parameter from {{type}}", ActionType.REMOVE, ModelType.PARAMETER),
    REMOVE_RECEPTION(ActionType.REMOVE, ModelType.RECEPTION),
    REMOVE_RELATIONSHIP(ActionType.REMOVE, ModelType.RELATIONSHIP),
    REMOVE_USE_CASE(ActionType.REMOVE, ModelType.USE_CASE);

    public enum ActionType {
        ADD("Add {{type}}"),
        UPDATE("Update {{propertyName}} property for {{type}}"),
        REMOVE("Remove {{type}}");

        private String name;

        ActionType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    public enum ModelType {
        CLASS("Class"),
        ATTRIBUTE("Attribute"),
        OPERATION("Operation"),
        PARAMETER("Parameter"),
        RECEPTION("Reception"),
        USE_CASE("UseCase"),
        RELATIONSHIP("Relationship");

        private String name;

        ModelType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static LogActivity getInstance(ActionType actionType, String modelTypeName) {
        for (LogActivity logActivity : LogActivity.values()) {
            if (logActivity.actionType.equals(actionType) && logActivity.modelType.getName().equals(modelTypeName))
                return logActivity;
        }
        throw new UnsupportedOperationException("LogActivity not found");
    }

    private String name;
    private ActionType actionType;
    private ModelType modelType;

    LogActivity(String name, ActionType actionType, ModelType modelType) {
        this.name = name;
        this.actionType = actionType;
        this.modelType = modelType;
    }

    LogActivity(ActionType actionType, ModelType modelType) {
        this(actionType.getName(), actionType, modelType);
    }

    public String getName() {
        return name;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public ModelType getModelType() {
        return modelType;
    }

}
