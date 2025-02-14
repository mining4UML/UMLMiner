package com.uniba.mining.tasks.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.factory.IModelElementFactory; // Import corretto!

public class ClassExt {

    private IClass classe;
    private boolean optional;
    private Map<IOperation, Boolean> optionalOperations = new HashMap<>();

    /**
     * Costruttore per creare un'estensione di una classe UML.
     * @param classe La classe UML originale.
     * @param optional Indica se la classe è opzionale.
     */
    public ClassExt(IClass classe, boolean optional) {
        this.classe = classe;
        this.optional = optional;
    }

    /**
     * Restituisce la classe UML originale.
     * @return La classe UML associata.
     */
    public IClass getClasse() {
        return classe;
    }

    /**
     * Indica se la classe è opzionale.
     * @return `true` se la classe è opzionale, `false` altrimenti.
     */
    public boolean getOptional() {
        return optional;
    }

    /**
     * Imposta se un'operazione è opzionale.
     * @param operation L'operazione da marcare.
     * @param isOptional `true` se l'operazione è opzionale, `false` altrimenti.
     */
    public void setOperationOptional(IOperation operation, boolean isOptional) {
        optionalOperations.put(operation, isOptional);
    }

    /**
     * Controlla se un'operazione è opzionale.
     * @param operation L'operazione da verificare.
     * @return `true` se l'operazione è opzionale, `false` altrimenti.
     */
    public boolean isOperationOptional(IOperation operation) {
        return optionalOperations.getOrDefault(operation, false);
    }

    /**
     * Restituisce una lista delle operazioni opzionali della classe.
     * @return Lista di operazioni opzionali.
     */
    public List<IOperation> getOptionalOperations() {
        List<IOperation> result = new ArrayList<>();
        for (Map.Entry<IOperation, Boolean> entry : optionalOperations.entrySet()) {
            if (entry.getValue()) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    /**
     * Crea una copia della classe senza operazioni opzionali.
     * @return Nuova classe senza operazioni opzionali.
     */
    public IClass copyWithoutOptionalOperations() {
        IClass newClass = IModelElementFactory.instance().createClass();
        newClass.setName(this.classe.getName());

        // Copia attributi
        for (IAttribute attr : this.classe.toAttributeArray()) {
            IAttribute newAttr = IModelElementFactory.instance().createAttribute();
            newAttr.setName(attr.getName());
            newAttr.setType((String) attr.getType());
            newClass.addAttribute(newAttr);
        }

        // Aggiunge solo operazioni non opzionali
        for (IOperation operation : this.classe.toOperationArray()) {
            if (!isOperationOptional(operation)) {
                newClass.addOperation(operation);
            }
        }

        return newClass;
    }
}
