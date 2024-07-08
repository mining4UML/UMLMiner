package com.uniba.mining.feedback;

import javax.swing.*;
import javax.swing.text.*;

public class LimitedTextField extends JTextField {
    private static final long serialVersionUID = 1L;
	public static final int LIMIT = 200; // Limite di caratteri

    public LimitedTextField() {
        super();
        ((AbstractDocument) this.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null || (fb.getDocument().getLength() + string.length()) <= LIMIT) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null || (fb.getDocument().getLength() - length + text.length()) <= LIMIT) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }
}