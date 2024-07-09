package com.uniba.mining.feedback;

import com.uniba.mining.utils.Application;
import com.uniba.mining.utils.GUI;

public class ErrorUtils {
    // Metodo per mostrare un messaggio di errore dettagliato
    public static void showDetailedErrorMessage(Exception e1) {
        StringBuilder errorMessage = new StringBuilder();

        // Aggiunge il messaggio dell'eccezione, se presente
        if (e1.getMessage() != null) {
            errorMessage.append(e1.getMessage()).append("\n");
        }

        // Aggiunge informazioni dettagliate sullo stack trace
        StackTraceElement[] stackTrace = e1.getStackTrace();
        if (stackTrace.length > 0) {
            StackTraceElement element = stackTrace[0];
            errorMessage.append("\nClass: ").append(element.getClassName()).append("\n");
            errorMessage.append("Method: ").append(element.getMethodName()).append("\n");
            errorMessage.append("Line: ").append(element.getLineNumber()).append("\n");
        }

        // Mostra il messaggio di errore
        GUI.showErrorMessageDialog(Application.getViewManager().getRootFrame(), "Feedback", errorMessage.toString());
    }
}
