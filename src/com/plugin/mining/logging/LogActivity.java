package com.plugin.mining.logging;

public enum LogActivity {
    ADD_CLASS("Add Class", ActionType.ADD, ModelType.CLASS),
    ADD_ATTRIBUTE("Add Attribute", ActionType.ADD, ModelType.ATTRIBUTE),
    ADD_OPERATION("Add Operation", ActionType.ADD, ModelType.OPERATION),
    ADD_RECEPTION("Add Reception", ActionType.ADD, ModelType.RECEPTION),
    ADD_RELATIONSHIP("Add Relationship", ActionType.ADD, ModelType.RELATIONSHIP),
    ADD_USE_CASE("Add Use Case", ActionType.ADD, ModelType.USE_CASE),
    UPDATE_CLASS("Update Class", ActionType.UPDATE, ModelType.CLASS),
    UPDATE_ATTRIBUTE("Update Attribute", ActionType.UPDATE, ModelType.ATTRIBUTE),
    UPDATE_OPERATION("Update Operation", ActionType.UPDATE, ModelType.OPERATION),
    UPDATE_RECEPTION("Update Reception", ActionType.UPDATE, ModelType.RECEPTION),
    UPDATE_RELATIONSHIP("Update Relationship", ActionType.UPDATE, ModelType.RELATIONSHIP),
    UPDATE_USE_CASE("Update Use Case", ActionType.UPDATE, ModelType.USE_CASE),
    REMOVE_CLASS("Remove Class", ActionType.REMOVE, ModelType.CLASS),
    REMOVE_ATTRIBUTE("Remove Attribute", ActionType.REMOVE, ModelType.ATTRIBUTE),
    REMOVE_OPERATION("Remove Operation", ActionType.REMOVE, ModelType.OPERATION),
    REMOVE_RECEPTION("Remove Reception", ActionType.REMOVE, ModelType.RECEPTION),
    REMOVE_RELATIONSHIP("Remove Relationship", ActionType.REMOVE, ModelType.RELATIONSHIP),
    REMOVE_USE_CASE("Remove Case", ActionType.REMOVE, ModelType.USE_CASE);

    public enum ActionType {
        ADD,
        UPDATE,
        REMOVE
    }

    public enum ModelType {
        CLASS("Class"),
        ATTRIBUTE("Attribute"),
        OPERATION("Operation"),
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
