package plugin.mining.logging;

public enum LogActivity {
    ADD_CLASS("Add class", ActionType.ADD, ModelType.CLASS),
    ADD_ATTRIBUTE("Add class attribute", ActionType.ADD, ModelType.ATTRIBUTE),
    ADD_OPERATION("Add class operation", ActionType.ADD, ModelType.OPERATION),
    ADD_CLASS_RELATION("Add class relation", ActionType.ADD, ModelType.CLASS),
    ADD_CLASS_ASSOCIATION("Add class association", ActionType.ADD, ModelType.CLASS),
    ADD_CLASS_GENERALIZATION("Add class generalization", ActionType.ADD, ModelType.CLASS),
    ADD_USE_CASE("Add use case", ActionType.ADD, ModelType.USE_CASE),
    ADD_USE_CASE_INCLUSION("Add use case inclusion", ActionType.ADD, ModelType.USE_CASE),
    ADD_USE_CASE_EXTENSION("Add use case extension", ActionType.ADD, ModelType.USE_CASE),
    UPDATE_CLASS("Update class", ActionType.UPDATE, ModelType.CLASS),
    UPDATE_ATTRIBUTE("Update class attribute", ActionType.UPDATE, ModelType.ATTRIBUTE),
    UPDATE_OPERATION("Update class operation", ActionType.UPDATE, ModelType.OPERATION),
    UPDATE_CLASS_RELATION("Update class relation", ActionType.UPDATE, ModelType.CLASS),
    UPDATE_CLASS_ASSOCIATION("Update class association", ActionType.UPDATE, ModelType.CLASS),
    UPDATE_CLASS_GENERALIZATION("Update class generalization", ActionType.UPDATE, ModelType.CLASS),
    UPDATE_USE_CASE("Update use case", ActionType.UPDATE, ModelType.USE_CASE),
    UPDATE_USE_CASE_INCLUSION("Update use case inclusion", ActionType.UPDATE, ModelType.USE_CASE),
    UPDATE_USE_CASE_EXTENSION("Update use case extension", ActionType.UPDATE, ModelType.USE_CASE),
    REMOVE_CLASS("Remove class", ActionType.REMOVE, ModelType.CLASS),
    REMOVE_ATTRIBUTE("Remove class attribute", ActionType.REMOVE, ModelType.ATTRIBUTE),
    REMOVE_OPERATION("Remove class operation", ActionType.REMOVE, ModelType.OPERATION),
    REMOVE_CLASS_RELATION("Remove class relation", ActionType.REMOVE, ModelType.CLASS),
    REMOVE_CLASS_ASSOCIATION("Remove class association", ActionType.REMOVE, ModelType.CLASS),
    REMOVE_CLASS_GENERALIZATION("Remove class generalization", ActionType.REMOVE, ModelType.CLASS),
    REMOVE_USE_CASE("Remove use case", ActionType.REMOVE, ModelType.USE_CASE),
    REMOVE_USE_CASE_INCLUSION("Remove use case inclusion", ActionType.REMOVE, ModelType.USE_CASE),
    REMOVE_USE_CASE_EXTENSION("Remove use case extension", ActionType.REMOVE, ModelType.USE_CASE);

    public enum ActionType {
        ADD,
        UPDATE,
        REMOVE
    }

    private enum ModelType {
        CLASS("Class"),
        ATTRIBUTE("Attribute"),
        OPERATION("Operation"),
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
            if (logActivity.type.equals(actionType) && logActivity.modelType.getName().equals(modelTypeName))
                return logActivity;
        }
        throw new UnsupportedOperationException("LogActivity not found");
    }

    private String name;
    private ActionType type;
    private ModelType modelType;

    LogActivity(String name, ActionType type, ModelType modelType) {
        this.name = name;
        this.type = type;
        this.modelType = modelType;
    }

    public String getName() {
        return name;
    }

    public ActionType getType() {
        return type;
    }

    public ModelType getModelType() {
        return modelType;
    }

}
