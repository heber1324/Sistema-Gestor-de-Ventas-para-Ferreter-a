package client.ui;

import server.model.UsuarioDTO;

import javax.swing.*;

public class MainMenuFrame extends JFrame {

    private UsuarioDTO usuarioActual;

    private JLabel lblTitulo;
    private JLabel lblUsuario;
    private JButton btnProductos;
    private JButton btnClientes;
    private JButton btnVentas;
    private JButton btnReportes;
    private JButton btnUsuarios;
    private JButton btnPedidos;
    private JButton btnSalir;

    public MainMenuFrame(UsuarioDTO usuarioActual) {
        this.usuarioActual = usuarioActual;

        String usuario = usuarioActual.getUsuario();
        String rol = usuarioActual.getRol();

        setTitle("Menú principal - Sistema de Ferretería");
        setSize(550, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        lblTitulo = new JLabel("Sistema de control de ventas - Ferretería");
        lblTitulo.setBounds(110, 20, 350, 25);
        add(lblTitulo);

        lblUsuario = new JLabel("Usuario: " + usuario + " (Rol: " + rol + ")");
        lblUsuario.setBounds(110, 50, 350, 25);
        add(lblUsuario);

        btnProductos = new JButton("Productos");
        btnProductos.setBounds(50, 100, 150, 30);
        add(btnProductos);

        btnClientes = new JButton("Clientes");
        btnClientes.setBounds(250, 100, 150, 30);
        add(btnClientes);

        btnVentas = new JButton("Registrar venta");
        btnVentas.setBounds(50, 140, 150, 30);
        add(btnVentas);

        btnReportes = new JButton("Reportes");
        btnReportes.setBounds(250, 140, 150, 30);
        add(btnReportes);

        btnPedidos = new JButton("Pedidos");
        btnPedidos.setBounds(50, 180, 150, 30);
        add(btnPedidos);

        btnUsuarios = new JButton("Usuarios");
        btnUsuarios.setBounds(250, 180, 150, 30);
        add(btnUsuarios);

        btnSalir = new JButton("Cerrar sesión");
        btnSalir.setBounds(180, 230, 150, 30);
        add(btnSalir);

        aplicarPermisosPorRol();

        btnClientes.addActionListener(e -> {
        ClientesAdminFrame frame = new ClientesAdminFrame();
        frame.setVisible(true);
        });

        // 🔹 AQUÍ ABRIMOS LA VENTANA DE REGISTRAR VENTA CON EL UsuarioDTO
        btnVentas.addActionListener(e -> {
            RegistrarVentaFrame frame = new RegistrarVentaFrame();
            frame.setVisible(true);
        });
        
        btnProductos.addActionListener(e -> {
            ProductosAdminFrame frame = new ProductosAdminFrame();
            frame.setVisible(true);
        });
        
        btnReportes.addActionListener(e -> {
        ReporteVentasBitacoraFrame frame = new ReporteVentasBitacoraFrame();
        frame.setVisible(true);
        });

        btnUsuarios.addActionListener(e -> {
        UsuariosAdminFrame frame = new UsuariosAdminFrame();
        frame.setVisible(true);
        });

        btnPedidos.addActionListener(e -> {
            PedidosFrame pedidosFrame = new PedidosFrame();
            pedidosFrame.setVisible(true);
        });

        btnSalir.addActionListener(e -> salirAlInicio());
    }

    private void aplicarPermisosPorRol() {
        String rol = usuarioActual.getRol();
        switch (rol) {
            case "ADMIN":
                // Todo habilitado
                break;
            case "CAJERO":
                btnUsuarios.setEnabled(false);
                break;
            case "INVITADO":
                btnVentas.setEnabled(false);
                btnUsuarios.setEnabled(false);
                btnReportes.setEnabled(false);
                btnPedidos.setEnabled(false);
                break;
        }
    }

    private void salirAlInicio() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Cerrar sesión y volver al inicio?",
                "Cerrar sesión",
                JOptionPane.YES_NO_OPTION
        );

        if (opcion == JOptionPane.YES_OPTION) {
            new InicioFrame().setVisible(true);
            this.dispose();
        }
    }
}
