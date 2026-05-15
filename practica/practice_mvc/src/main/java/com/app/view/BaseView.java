package com.app.view;

/**
 * Comportamiento común a todas las vistas.
 * Evita duplicar lógica de formato entre ConsoleView y SwingView.
 */
public abstract class BaseView implements View {

    protected String buildMenu(String[] options, String title) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔═══ ").append(title).append(" ═══╗\n");
        for (int i = 0; i < options.length; i++) {
            sb.append(String.format("  %d. %s%n", i + 1, options[i]));
        }
        sb.append("╚" + "═".repeat(title.length() + 8) + "╝");
        return sb.toString();
    }
}