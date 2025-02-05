package com.uniba.mining.utils;

import javax.swing.*;
import java.io.File;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FileSelectionUtil {

    /**
     * Mostra un dialogo di selezione file e restituisce i file selezionati.
     *
     * @param parent La finestra genitore (può essere JFrame, JDialog, o null).
     * @param logsDir La directory dei log.
     * @return Un array di file selezionati, o un array vuoto se l'utente annulla.
     */
    public static File[] showFileSelectionDialog(Window parent, File logsDir) {
        JFileChooser fileChooser = new JFileChooser(logsDir);
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setDialogTitle("Seleziona i file di log da esportare");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = (parent != null)
            ? fileChooser.showOpenDialog(parent)
            : fileChooser.showOpenDialog(null);

        return (result == JFileChooser.APPROVE_OPTION) ? fileChooser.getSelectedFiles() : new File[0];
    }
}
