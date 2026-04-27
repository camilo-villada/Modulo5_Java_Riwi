package com.huellassanas.view.desktop;

import com.huellassanas.controller.ClinicaController;
import com.huellassanas.model.Cita;
import com.huellassanas.model.Cliente;
import com.huellassanas.model.Mascota;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Vista de escritorio (Swing) del sistema Huellas Sanas.
 *
 * <p><strong>Principio MVC:</strong> esta clase se ocupa exclusivamente de
 * los componentes gráficos y la interacción con el usuario. Toda la lógica
 * de negocio se delega al {@link ClinicaController}, el mismo que usa
 * {@link com.huellassanas.view.console.ConsoleView}.</p>
 *
 * <p>La ventana principal implementa un patrón de panel apilado:
 * muestra el {@code LoginPanel} inicialmente y lo reemplaza por el
 * {@code MainPanel} tras una autenticación exitosa.</p>
 */
public class MainFrame extends JFrame {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ClinicaController controller;

    // ─── Paneles ──────────────────────────────────────────────────────────────
    private JPanel loginPanel;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // ─── Componentes de login ─────────────────────────────────────────────────
    private JTextField  txtUsername;
    private JPasswordField txtPassword;

    /**
     * Construye la ventana principal con el controlador compartido.
     *
     * @param controller controlador agnóstico de UI
     */
    public MainFrame(ClinicaController controller) {
        this.controller = controller;
        initUI();
    }

    // ─── Inicialización de la UI ──────────────────────────────────────────────

    private void initUI() {
        setTitle("Huellas Sanas — Sistema de Gestión Veterinaria");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        JPanel root = new JPanel(cardLayout);
        root.add(buildLoginPanel(), "login");
        root.add(buildMainPanel(),  "main");

        setContentPane(root);
        cardLayout.show(root, "login");
        pack();
    }

    // ─── Panel de Login ───────────────────────────────────────────────────────

    private JPanel buildLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(30, 30, 46));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(49, 50, 68));
        card.setBorder(new EmptyBorder(40, 60, 40, 60));
        card.setMaximumSize(new Dimension(380, 400));

        // Título
        JLabel lblTitulo = new JLabel("🐾 Huellas Sanas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(180, 190, 254));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Inicie sesión para continuar");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(166, 173, 200));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Campos
        txtUsername = createStyledTextField("Usuario");
        txtPassword = new JPasswordField(20);
        styleTextField(txtPassword, "Contraseña");

        // Botón login
        JButton btnLogin = createStyledButton("Iniciar Sesión", new Color(137, 180, 250));
        btnLogin.addActionListener(this::onLogin);

        // Ensamblado
        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(6));
        card.add(lblSub);
        card.add(Box.createVerticalStrut(30));
        card.add(new JLabel("Usuario:") {{ setForeground(new Color(205, 214, 244)); }});
        card.add(Box.createVerticalStrut(4));
        card.add(txtUsername);
        card.add(Box.createVerticalStrut(16));
        card.add(new JLabel("Contraseña:") {{ setForeground(new Color(205, 214, 244)); }});
        card.add(Box.createVerticalStrut(4));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(24));
        card.add(btnLogin);

        loginPanel.add(card);
        return loginPanel;
    }

    // ─── Panel principal ──────────────────────────────────────────────────────

    private JPanel buildMainPanel() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(30, 30, 46));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(49, 50, 68));
        header.setBorder(new EmptyBorder(12, 20, 12, 20));
        JLabel lblHeader = new JLabel("🐾 Huellas Sanas — Panel Principal");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(new Color(180, 190, 254));
        JButton btnLogout = createStyledButton("Cerrar Sesión", new Color(243, 139, 168));
        btnLogout.addActionListener(e -> onLogout());
        header.add(lblHeader, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);

        // Botones de acciones
        JPanel acciones = new JPanel(new GridLayout(1, 2, 16, 0));
        acciones.setBackground(new Color(30, 30, 46));

        JButton btnCliente = createStyledButton("➕ Registrar Cliente", new Color(166, 227, 161));
        btnCliente.addActionListener(e -> mostrarDialogoCliente());

        JButton btnMascota = createStyledButton("🐶 Registrar Mascota + Cita", new Color(137, 220, 235));
        btnMascota.addActionListener(e -> mostrarDialogoMascotaCita());

        acciones.add(btnCliente);
        acciones.add(btnMascota);

        // Log de operaciones
        JTextArea log = new JTextArea();
        log.setEditable(false);
        log.setBackground(new Color(24, 24, 37));
        log.setForeground(new Color(166, 173, 200));
        log.setFont(new Font("Consolas", Font.PLAIN, 13));
        log.setText("Sistema listo. Seleccione una operación.\n");
        JScrollPane scrollLog = new JScrollPane(log);
        scrollLog.setBorder(BorderFactory.createLineBorder(new Color(69, 71, 90)));

        mainPanel.add(header,    BorderLayout.NORTH);
        mainPanel.add(acciones,  BorderLayout.CENTER);
        mainPanel.add(scrollLog, BorderLayout.SOUTH);
        return mainPanel;
    }

    // ─── Diálogos de operación ────────────────────────────────────────────────

    private void mostrarDialogoCliente() {
        JTextField nome   = new JTextField(20);
        JTextField apellido = new JTextField(20);
        JTextField dni    = new JTextField(15);
        JTextField tel    = new JTextField(15);
        JTextField correo = new JTextField(25);
        JTextField dir    = new JTextField(30);

        Object[] campos = {
            "Nombre:",   nome,
            "Apellido:", apellido,
            "DNI:",      dni,
            "Teléfono:", tel,
            "Correo:",   correo,
            "Dirección:", dir
        };

        int res = JOptionPane.showConfirmDialog(this, campos,
                "Registrar Cliente", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        Cliente c = new Cliente();
        c.setNombre(nome.getText().trim());
        c.setApellido(apellido.getText().trim());
        c.setDni(dni.getText().trim());
        c.setTelefono(tel.getText().trim());
        c.setCorreo(correo.getText().trim());
        c.setDireccion(dir.getText().trim());

        ClinicaController.Resultado r = controller.registrarCliente(c);
        mostrarResultado(r);
    }

    private void mostrarDialogoMascotaCita() {
        // Datos de mascota
        JTextField nomMas  = new JTextField(20);
        JTextField especie = new JTextField(15);
        JTextField raza    = new JTextField(20);
        JTextField peso    = new JTextField(8);
        JTextField cliId   = new JTextField(8);
        // Datos de cita
        JTextField vetId   = new JTextField(8);
        JTextField fechaH  = new JTextField(20);
        fechaH.setText("dd/MM/yyyy HH:mm");
        JTextField motivo  = new JTextField(30);

        Object[] campos = {
            "── MASCOTA ──────────────────────", new JSeparator(),
            "Nombre:", nomMas,
            "Especie (PERRO/GATO/AVE/...):  ", especie,
            "Raza:", raza,
            "Peso (kg):", peso,
            "ID Cliente:", cliId,
            "── CITA ─────────────────────────", new JSeparator(),
            "ID Veterinario:", vetId,
            "Fecha y hora (dd/MM/yyyy HH:mm):", fechaH,
            "Motivo:", motivo
        };

        int res = JOptionPane.showConfirmDialog(this, campos,
                "Registrar Mascota + Cita (Atómico)",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            Mascota mascota = new Mascota();
            mascota.setNombre(nomMas.getText().trim());
            mascota.setEspecie(Mascota.Especie.valueOf(especie.getText().trim().toUpperCase()));
            mascota.setRaza(raza.getText().trim());
            mascota.setPeso(Double.parseDouble(peso.getText().trim()));
            mascota.setClienteId(Integer.parseInt(cliId.getText().trim()));

            LocalDateTime fechaHora = LocalDateTime.parse(fechaH.getText().trim(), FMT);
            Cita cita = new Cita(0, fechaHora, motivo.getText().trim(),
                                 Integer.parseInt(vetId.getText().trim()));

            ClinicaController.Resultado r = controller.registrarMascotaYCita(mascota, cita);
            mostrarResultado(r);
        } catch (IllegalArgumentException | DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Datos inválidos: " + ex.getMessage(),
                    "Error de entrada", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─── Eventos ──────────────────────────────────────────────────────────────

    private void onLogin(ActionEvent e) {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());

        ClinicaController.Resultado r = controller.iniciarSesion(user, pass);
        if (r.exito()) {
            cardLayout.show(getContentPane(), "main");
        } else {
            JOptionPane.showMessageDialog(this, r.mensaje(),
                    "Error de autenticación", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onLogout() {
        controller.cerrarSesion();
        txtUsername.setText("");
        txtPassword.setText("");
        cardLayout.show(getContentPane(), "login");
    }

    // ─── Helpers de presentación ──────────────────────────────────────────────

    private void mostrarResultado(ClinicaController.Resultado r) {
        if (r.exito()) {
            JOptionPane.showMessageDialog(this, r.mensaje(), "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, r.mensaje(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(20);
        styleTextField(field, placeholder);
        return field;
    }

    private void styleTextField(JTextField f, String placeholder) {
        f.setBackground(new Color(69, 71, 90));
        f.setForeground(new Color(205, 214, 244));
        f.setCaretColor(new Color(205, 214, 244));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(88, 91, 112), 1),
                new EmptyBorder(6, 10, 6, 10)));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }

    private JButton createStyledButton(String text, Color accent) {
        JButton btn = new JButton(text);
        btn.setBackground(accent);
        btn.setForeground(new Color(30, 30, 46));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return btn;
    }

    // ─── Punto de entrada de la vista Swing ──────────────────────────────────

    /**
     * Lanza la interfaz gráfica en el Event Dispatch Thread (EDT).
     *
     * @param controller controlador compartido con la vista de consola
     */
    public static void lanzar(ClinicaController controller) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new MainFrame(controller).setVisible(true);
        });
    }
}
