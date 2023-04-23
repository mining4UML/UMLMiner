package plugin.mining.logging;

import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IUseCase;

public enum LogActivity {
    ADD_CLASS("Add class", Type.ADD, IClass.class),
    ADD_ATTRIBUTE("Add class attribute", Type.ADD, IAttribute.class),
    ADD_OPERATION("Add class operation", Type.ADD, IOperation.class),
    ADD_CLASS_RELATION("Add class relation", Type.ADD, IClass.class),
    ADD_CLASS_ASSOCIATION("Add class association", Type.ADD, IClass.class),
    ADD_CLASS_GENERALIZATION("Add class generalization", Type.ADD, IClass.class),
    ADD_USE_CASE("Add use case", Type.ADD, IUseCase.class),
    ADD_USE_CASE_INCLUSION("Add use case inclusion", Type.ADD, IUseCase.class),
    ADD_USE_CASE_EXTENSION("Add use case extension", Type.ADD, IUseCase.class),
    UPDATE_CLASS("Update class", Type.UPDATE, IClass.class),
    UPDATE_ATTRIBUTE("Update class attribute", Type.UPDATE, IAttribute.class),
    UPDATE_OPERATION("Update class operation", Type.UPDATE, IOperation.class),
    UPDATE_CLASS_RELATION("Update class relation", Type.UPDATE, IClass.class),
    UPDATE_CLASS_ASSOCIATION("Update class association", Type.UPDATE, IClass.class),
    UPDATE_CLASS_GENERALIZATION("Update class generalization", Type.UPDATE, IClass.class),
    UPDATE_USE_CASE("Update use case", Type.UPDATE, IUseCase.class),
    UPDATE_USE_CASE_INCLUSION("Update use case inclusion", Type.UPDATE, IUseCase.class),
    UPDATE_USE_CASE_EXTENSION("Update use case extension", Type.UPDATE, IUseCase.class),
    REMOVE_CLASS("Remove class", Type.REMOVE, IClass.class),
    REMOVE_ATTRIBUTE("Remove class attribute", Type.REMOVE, IAttribute.class),
    REMOVE_OPERATION("Remove class operation", Type.REMOVE, IOperation.class),
    REMOVE_CLASS_RELATION("Remove class relation", Type.REMOVE, IClass.class),
    REMOVE_CLASS_ASSOCIATION("Remove class association", Type.REMOVE, IClass.class),
    REMOVE_CLASS_GENERALIZATION("Remove class generalization", Type.REMOVE, IClass.class),
    REMOVE_USE_CASE("Remove use case", Type.REMOVE, IUseCase.class),
    REMOVE_USE_CASE_INCLUSION("Remove use case inclusion", Type.REMOVE, IUseCase.class),
    REMOVE_USE_CASE_EXTENSION("Remove use case extension", Type.REMOVE, IUseCase.class);

    public enum Type {
        ADD,
        UPDATE,
        REMOVE
    }

    public static LogActivity instance(Type type, Class<? extends IModelElement> modelType) {
        for (LogActivity logActivity : LogActivity.values()) {
            if (logActivity.type.equals(type) && logActivity.modelType.equals(modelType))
                return logActivity;
        }
        throw new UnsupportedOperationException("LogActivity not found");
    }

    private String name;
    private Type type;
    private Class<? extends IModelElement> modelType;

    LogActivity(String name, Type type, Class<? extends IModelElement> modelType) {
        this.name = name;
        this.type = type;
        this.modelType = modelType;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Class<? extends IModelElement> getModelType() {
        return modelType;
    }

}
