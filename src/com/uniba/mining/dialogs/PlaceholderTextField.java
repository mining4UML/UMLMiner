package com.uniba.mining.dialogs;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class PlaceholderTextField extends JTextField {

	private static final long serialVersionUID = 1L;
	private String placeholder;
	private boolean focused = false;

	public PlaceholderTextField(String placeholder) {
		this.placeholder = placeholder;

		// Imposta il testo iniziale del placeholder
		setText(placeholder);
		setForeground(Color.GRAY);

		// Aggiungi un listener di focus per controllare quando il campo di testo
		// ottiene o perde il focus
		addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				focused = true;
				if (getText().equals(placeholder)) {
					setText("");
					setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				focused = false;
				if (getText().isEmpty()) {
					setText(placeholder);
					setForeground(Color.GRAY);
				}
			}
		});

		// Aggiungi un listener per controllare quando il testo del campo di testo
		// cambia
		getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				updatePlaceholder();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updatePlaceholder();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updatePlaceholder();
			}
		});
	}

	private void updatePlaceholder() {
		if (focused && getText().isEmpty()) {
			setText(placeholder);
			setForeground(Color.GRAY);
		}
		else if (!focused && getText().isEmpty()) {
			setText(placeholder);
			setForeground(Color.GRAY);
		} else {
			setForeground(Color.BLACK);
		}
	}

	/*
	 * // Override del metodo getText() per restituire una stringa vuota anziché il
	 * // placeholder se il testo è il placeholder
	 * 
	 * @Override public String getText() { String text = super.getText(); return
	 * text.equals(placeholder) ? "" : text; }
	 */

	public String getPlaceholder() {
		return placeholder;
	}
}