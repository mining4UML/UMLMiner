package com.uniba.mining.feedback;

import javax.swing.*;
import java.awt.*;

public class ConversationListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

	@Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof Conversation) {
            Conversation conversation = (Conversation) value;
            setText(conversation.getTitle()); // Imposta il testo da visualizzare nella lista
        }

        return this;
    }
}