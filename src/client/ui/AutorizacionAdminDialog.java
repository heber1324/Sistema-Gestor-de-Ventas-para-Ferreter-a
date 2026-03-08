package client.ui;

import server.model.UsuarioDTO;
import server.rmi.UsuarioService;

import javax.swing.*;
import java.awt.*;
import java.rmi.Naming;
import client.util.RMIConfig;

public class AutorizacionAdminDialog extends JDialog {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnAutorizar;
    private JButton btnCancelar;
    private JLabel lblMensaje;

    private boolean autorizado = false;

    public AutorizacionAdminDialog(Frame parent) {
        super(parent, "Autorización de administrador", true);
        setSize(350, 220);
        setLocationRelativeTo(parent);
        setLayout(null);

        JLabel lblTitulo = new JLabel("Ingrese credenciales de administrador");
        lblTitulo.setBounds(30, 10, 280, 25);
        add(lblTitulo);

        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setBounds(30, 50, 80, 25);
        add(lblUser);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(110, 50, 180, 25);
        add(txtUsuario);

        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setBounds(30, 80, 80, 25);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(110, 80, 180, 25);
        add(txtPassword);

        lblMensaje = new JLabel("");
        lblMensaje.setBounds(30, 110, 280, 25);
        add(lblMensaje);

        btnAutorizar = new JButton("Autorizar");
        btnAutorizar.setBounds(50, 140, 100, 30);
        add(btnAutorizar);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(180, 140, 100, 30);
        add(btnCancelar);

        btnAutorizar.addActionListener(e -> intentarAutorizar());
        btnCancelar.addActionListener(e -> {
            autorizado = false;
            dispose();
        });
    }

    private void intentarAutorizar() {
        String user = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            lblMensaje.setText("Llena usuario y contraseña.");
            return;
        }

        try {
            String url = RMIConfig.url("UsuarioService");
            UsuarioService service = (UsuarioService) Naming.lookup(url);

            UsuarioDTO dto = service.login(user, pass);

            if (dto != null && "ADMIN".equalsIgnoreCase(dto.getRol())) {
                autorizado = true;
                dispose();
            } else {
                lblMensaje.setText("No es un usuario ADMIN válido.");
            }
        } catch (Exception e) {
            lblMensaje.setText("Error al validar admin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isAutorizado() {
        return autorizado;
    }
}
