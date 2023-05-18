package com.plugin.mining.logging;

public enum LogActivity {
    ADD_PROJECT("Project opened", ActionType.ADD, ModelType.PROJECT),
    ADD_DIAGRAM(ActionType.ADD, ModelType.DIAGRAM),
    ADD_VIEW("View added to {{type}}", ActionType.ADD, ModelType.VIEW),
    ADD_CLASS(ActionType.ADD, ModelType.CLASS),
    ADD_ATTRIBUTE(ActionType.ADD, ModelType.ATTRIBUTE),
    ADD_OPERATION(ActionType.ADD, ModelType.OPERATION),
    ADD_PARAMETER("Parameter added to {{type}}", ActionType.ADD, ModelType.PARAMETER),
    ADD_RECEPTION(ActionType.ADD, ModelType.RECEPTION),
    ADD_RELATIONSHIP(ActionType.ADD, ModelType.RELATIONSHIP),
    ADD_PACKAGE(ActionType.ADD, ModelType.PACKAGE),
    ADD_PACKAGE_CHILD("{{childType}} added to Package", ActionType.ADD, ModelType.PACKAGE),
    ADD_USE_CASE(ActionType.ADD, ModelType.USE_CASE),
    UPDATE_PROJECT(ActionType.UPDATE, ModelType.PROJECT),
    UPDATE_DIAGRAM(ActionType.UPDATE, ModelType.DIAGRAM),
    UPDATE_CLASS(ActionType.UPDATE, ModelType.CLASS),
    UPDATE_ATTRIBUTE(ActionType.UPDATE, ModelType.ATTRIBUTE),
    UPDATE_OPERATION(ActionType.UPDATE, ModelType.OPERATION),
    UPDATE_PARAMETER("{{propertyName}} property updated for {{type}} Parameter", ActionType.UPDATE,
            ModelType.PARAMETER),
    UPDATE_RECEPTION(ActionType.UPDATE, ModelType.RECEPTION),
    UPDATE_RELATIONSHIP(ActionType.UPDATE, ModelType.RELATIONSHIP),
    UPDATE_PACKAGE(ActionType.UPDATE, ModelType.PACKAGE),
    UPDATE_USE_CASE(ActionType.UPDATE, ModelType.USE_CASE),
    REMOVE_PROJECT(ActionType.REMOVE, ModelType.PROJECT),
    REMOVE_DIAGRAM(ActionType.REMOVE, ModelType.DIAGRAM),
    REMOVE_VIEW("View removed from {{type}}", ActionType.REMOVE, ModelType.VIEW),
    REMOVE_CLASS(ActionType.REMOVE, ModelType.CLASS),
    REMOVE_ATTRIBUTE(ActionType.REMOVE, ModelType.ATTRIBUTE),
    REMOVE_OPERATION(ActionType.REMOVE, ModelType.OPERATION),
    REMOVE_PARAMETER("Parameter removed from {{type}}", ActionType.REMOVE, ModelType.PARAMETER),
    REMOVE_RECEPTION(ActionType.REMOVE, ModelType.RECEPTION),
    REMOVE_RELATIONSHIP(ActionType.REMOVE, ModelType.RELATIONSHIP),
    REMOVE_PACKAGE(ActionType.REMOVE, ModelType.PACKAGE),
    REMOVE_PACKAGE_CHILD("{{childType}} removed from Package", ActionType.REMOVE, ModelType.PACKAGE),
    REMOVE_USE_CASE(ActionType.REMOVE, ModelType.USE_CASE);

    public enum ActionType {
        ADD("{{type}} added to {{sourceType}}"),
        UPDATE("{{propertyName}} property updated for {{type}}"),
        REMOVE("{{type}} removed from {{sourceType}}");

        private String name;

        ActionType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    public enum ModelType {
        PROJECT("Project"),
        DIAGRAM("Diagram"),
        VIEW("View"),
        CLASS("Class"),
        ATTRIBUTE("Attribute"),
        OPERATION("Operation"),
        PARAMETER("Parameter"),
        RECEPTION("Reception"),
        RELATIONSHIP("Relationship"),
        PACKAGE("Package"),
        USE_CASE("UseCase");

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
            if (logActivity.actionType.equals(actionType) && logActivity.modelType.name.equals(modelTypeName))
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
