package com.uniba.mining.feedback;

//import javax.swing.*;
//import java.awt.*;
//
//public class ConversationListCellRenderer implements ListCellRenderer<Conversation> {
//    
//    @Override
//    public Component getListCellRendererComponent(JList<? extends Conversation> list, Conversation value, int index, boolean isSelected, boolean cellHasFocus) {
//        // Crea un panel che userai come cell renderer
//        JPanel panel = new JPanel(new BorderLayout());
//        JLabel textLabel = new JLabel(value.getTitle());
//        JLabel dotsLabel = new JLabel("...");
//
//        // Imposta il testo della conversazione e i puntini sospensivi
//        panel.add(textLabel, BorderLayout.CENTER);
//        panel.add(dotsLabel, BorderLayout.EAST);
//
//        // Gestisci la selezione della lista per cambiare lo sfondo e il colore del testo
//        if (isSelected) {
//            panel.setBackground(list.getSelectionBackground());
//            panel.setForeground(list.getSelectionForeground());
//            textLabel.setForeground(list.getSelectionForeground());
//            dotsLabel.setForeground(list.getSelectionForeground());
//        } else {
//            panel.setBackground(list.getBackground());
//            panel.setForeground(list.getForeground());
//            textLabel.setForeground(list.getForeground());
//            dotsLabel.setForeground(list.getForeground());
//        }
//
//        return panel;
//    }
//}


import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

import com.uniba.mining.utils.GUI;

public class ConversationListCellRenderer extends JPanel implements ListCellRenderer<Conversation> {
    private static final long serialVersionUID = 1L;

    private final JLabel textLabel;
    private final JLabel iconLabel;
    private final Icon menuIcon;

    private static final Color LIGHT_BLUE = new Color(200, 220, 250); // Azzurrino chiaro
    private static final Color DARK_BLUE = new Color(100, 150, 230);  // Azzurro più scuro per la selezione

    public ConversationListCellRenderer() {
        setLayout(new BorderLayout(10, 10)); // Aggiunge spazio tra gli elementi
        setBorder(new EmptyBorder(5, 10, 5, 10)); // Margini per migliorare l'aspetto

        textLabel = new JLabel();
        iconLabel = new JLabel();

        // Carica l'icona (modifica il percorso in base alla tua icona)
        menuIcon = GUI.getImageIcon("ellipsis_icon.png");
        iconLabel.setIcon(menuIcon);

        add(textLabel, BorderLayout.CENTER);
        add(iconLabel, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Conversation> list, Conversation value, int index, boolean isSelected, boolean cellHasFocus) {
        textLabel.setText(value.getTitle());

        if (isSelected) {
            setBackground(DARK_BLUE);
            textLabel.setForeground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.BLUE, 2)); // Bordo per evidenziare meglio
        } else {
            setBackground(LIGHT_BLUE);
            textLabel.setForeground(Color.BLACK);
            setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // Margine regolare
        }

        setOpaque(true); // Importante per visualizzare il colore di sfondo
        return this;
    }
}


