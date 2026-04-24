package com.app.view;

import javax.swing.JOptionPane;

/**
 * Vista basada en JOptionPane (Swing).
 */
public class SwingView extends BaseView {

    private String[] lastMenuOptions = new String[0];
    private String   lastMenuTitle   = "Menú";

    @Override
    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Información",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showList(String title, String formatted) {
        JOptionPane.showMessageDialog(null, formatted, title, JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public void showItem(String title, String formatted) {
        JOptionPane.showMessageDialog(null, formatted, title, JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public String askInput(String prompt) {
        return JOptionPane.showInputDialog(null, prompt);
    }

    @Override
    public boolean confirm(String question) {
        int r = JOptionPane.showConfirmDialog(null, question,
                "Confirmar", JOptionPane.YES_NO_OPTION);
        return r == JOptionPane.YES_OPTION;
    }

    @Override
    public void showMenu(String[] options, String title) {
        this.lastMenuOptions = options != null ? options : new String[0];
        this.lastMenuTitle   = (title == null || title.isBlank()) ? "Menú" : title;
    }

    @Override
    public int getMenuChoice() {
        String[] opts = (lastMenuOptions == null) ? new String[0] : lastMenuOptions;
        if (opts.length == 0) return -1;

        Object sel = JOptionPane.showInputDialog(
                null, "Selecciona una opción:", lastMenuTitle,
                JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);
        if (sel == null) return opts.length; // última opción suele ser “Salir/Volver”
        for (int i = 0; i < opts.length; i++) {
            if (opts[i].equals(sel)) return i + 1;
        }
        return -1;
    }
}