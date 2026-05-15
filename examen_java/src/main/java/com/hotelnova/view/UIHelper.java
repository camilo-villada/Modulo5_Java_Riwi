package com.hotelnova.view;

import java.math.BigDecimal;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class UIHelper {

    public static void showTableDialog(String title, String content) {
        JTextArea textArea = new JTextArea(content);
        textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(860, 420));
        JOptionPane.showMessageDialog(null, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static String promptText(String message, String title, String initialValue) {
        return (String) JOptionPane.showInputDialog(null, message, title, JOptionPane.QUESTION_MESSAGE, null, null, initialValue);
    }

    public static Integer promptInt(String message, String initialValue) {
        while (true) {
            String value = promptText(message, "HotelNova", initialValue);
            if (value == null) {
                return null;
            }
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                showError("Invalid Input", "Please enter a valid integer.");
            }
        }
    }

    public static Integer promptInt(String message) {
        return promptInt(message, null);
    }

    public static BigDecimal promptDecimal(String message, String initialValue) {
        while (true) {
            String value = promptText(message, "HotelNova", initialValue);
            if (value == null) {
                return null;
            }
            try {
                return new BigDecimal(value.trim());
            } catch (NumberFormatException e) {
                showError("Invalid Input", "Please enter a valid decimal number.");
            }
        }
    }

    public static String promptPassword(String message, boolean allowBlank) {
        JPasswordField passwordField = new JPasswordField();
        int result = JOptionPane.showConfirmDialog(null, passwordField, message, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }
        String password = new String(passwordField.getPassword());
        if (!allowBlank && password.isBlank()) {
            throw new IllegalArgumentException("The password cannot be empty.");
        }
        return password;
    }

    public static String promptPassword(String message) {
        return promptPassword(message, false);
    }

    public static void showError(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showSuccess(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
}
