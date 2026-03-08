package client.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InicioFrame extends JFrame {

    private JButton btnCliente;
    private JButton btnUsuario;
    private JButton btnSalir;
    private JLabel lblTitulo;

    public InicioFrame() {
        setTitle("Sistema de Ferretería - Inicio");
        setSize(420, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Título
        lblTitulo = new JLabel("Bienvenido al sistema de ferretería");
        lblTitulo.setBounds(70, 20, 300, 25);
        add(lblTitulo);

        // Botón "Soy cliente"
        btnCliente = new JButton("Soy cliente");
        btnCliente.setBounds(40, 80, 140, 40);
        add(btnCliente);

        // Botón "Soy usuario / empleado"
        btnUsuario = new JButton("Soy usuario / empleado");
        btnUsuario.setBounds(210, 80, 160, 40);
        add(btnUsuario);

        // Botón "Salir"
        btnSalir = new JButton("Salir");
        btnSalir.setBounds(150, 140, 100, 30);
        add(btnSalir);

        // Acción: cliente
        btnCliente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirMenuCliente();
            }
        });

        // Acción: usuario
        btnUsuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirLoginUsuario();
            }
        });

        // Acción: salir
        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salirAplicacion();
            }
        });
    }

    private void abrirMenuCliente() {
        ClienteMenuFrame clienteMenu = new ClienteMenuFrame();
        clienteMenu.setVisible(true);
        this.dispose();
    }

    private void abrirLoginUsuario() {
        LoginFrame login = new LoginFrame();
        login.setVisible(true);
        this.dispose();
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

    // Punto de entrada general del sistema
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InicioFrame().setVisible(true));
    }

}
