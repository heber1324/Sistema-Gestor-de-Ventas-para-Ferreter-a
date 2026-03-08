package client.ui;

import server.model.UsuarioDTO;
import server.rmi.UsuarioService;
import java.rmi.Naming;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import client.util.RMIConfig;

public class LoginFrame extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JLabel lblMensaje;
    private JButton btnLogin;
    private JButton btnSalir;

    public LoginFrame() {
        setTitle("Login - Sistema de Ferretería");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setBounds(50, 40, 80, 25);
        add(lblUsuario);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(140, 40, 180, 25);
        add(txtUsuario);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setBounds(50, 80, 80, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(140, 80, 180, 25);
        add(txtPassword);

        lblMensaje = new JLabel("");
        lblMensaje.setBounds(50, 110, 300, 25);
        add(lblMensaje);

        btnLogin = new JButton("Iniciar sesión");
        btnLogin.setBounds(50, 150, 130, 30);
        add(btnLogin);

        btnSalir = new JButton("Salir");
        btnSalir.setBounds(190, 150, 130, 30);
        add(btnSalir);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarLogin();
            }
        });

        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salirAplicacion();
            }
        });
    }

    private void realizarLogin() {
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            lblMensaje.setText("Ingresa usuario y contraseña.");
            return;
        }

        try {
            String url = RMIConfig.url("UsuarioService");
            UsuarioService service = (UsuarioService) Naming.lookup(url);

            UsuarioDTO usuarioDTO = service.login(usuario, password);

            if (usuarioDTO != null) {
                String rol = usuarioDTO.getRol();
                lblMensaje.setText("Bienvenido, " + usuarioDTO.getNombre() + " (" + rol + ")");

                // 🔹 AHORA PASAMOS EL USUARIODTO COMPLETO
                MainMenuFrame menu = new MainMenuFrame(usuarioDTO);
                menu.setVisible(true);
                this.dispose();
            } else {
                lblMensaje.setText("Usuario o contraseña incorrectos.");
            }

        } catch (Exception e) {
            lblMensaje.setText("Error al conectar con el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void salirAplicacion() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que deseas salir?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION
        );

        if (opcion == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
