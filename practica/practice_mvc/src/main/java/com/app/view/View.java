package com.app.view;

/**
 * Contrato que toda vista debe cumplir.
 * El Controller solo conoce esta interfaz.
 */
public interface View {
    void       showMessage(String msg);
    void       showError(String msg);
    void       showList(String title, String formatted);
    void       showItem(String title, String formatted);
    String     askInput(String prompt);
    boolean    confirm(String question);
    void       showMenu(String[] options, String title);
    int        getMenuChoice();
}