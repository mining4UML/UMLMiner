package com.uniba.mining.feedback;

import javax.swing.*;
import java.awt.*;

public class ConversationListCellRenderer implements ListCellRenderer<Conversation> {
    
    @Override
    public Component getListCellRendererComponent(JList<? extends Conversation> list, Conversation value, int index, boolean isSelected, boolean cellHasFocus) {
        // Crea un panel che userai come cell renderer
        JPanel panel = new JPanel(new BorderLayout());
        JLabel textLabel = new JLabel(value.getTitle());
        JLabel dotsLabel = new JLabel("...");

        // Imposta il testo della conversazione e i puntini sospensivi
        panel.add(textLabel, BorderLayout.CENTER);
        panel.add(dotsLabel, BorderLayout.EAST);

        // Gestisci la selezione della lista per cambiare lo sfondo e il colore del testo
        if (isSelected) {
            panel.setBackground(list.getSelectionBackground());
            panel.setForeground(list.getSelectionForeground());
            textLabel.setForeground(list.getSelectionForeground());
            dotsLabel.setForeground(list.getSelectionForeground());
        } else {
            panel.setBackground(list.getBackground());
            panel.setForeground(list.getForeground());
            textLabel.setForeground(list.getForeground());
            dotsLabel.setForeground(list.getForeground());
        }

        return panel;
    }
}
