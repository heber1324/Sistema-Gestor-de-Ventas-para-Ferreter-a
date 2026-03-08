package client.ui;

import javax.swing.*;

public class ClienteMenuFrame extends JFrame {

    private JButton btnVerProductos;
    private JButton btnCarrito;
    private JButton btnRegresar;
    private JButton btnSalir;
    private JLabel lblTitulo;

    public ClienteMenuFrame() {
        setTitle("Modo Cliente - Ferretería");
        setSize(450, 230);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null); // seguimos usando posiciones manuales

        // Título
        lblTitulo = new JLabel("Modo Cliente - Consulta de información");
        lblTitulo.setBounds(80, 20, 300, 25);
        add(lblTitulo);

        // Botón "Ver catálogo de productos"
        btnVerProductos = new JButton("Ver catálogo de productos");
        btnVerProductos.setBounds(110, 60, 220, 35);
        add(btnVerProductos);

        // Botón "Ver carrito"
        btnCarrito = new JButton("Ver carrito");
        btnCarrito.setBounds(110, 100, 220, 35);
        add(btnCarrito);

        // Botón "Regresar al inicio"
        btnRegresar = new JButton("Regresar al inicio");
        btnRegresar.setBounds(70, 150, 140, 30);
        add(btnRegresar);

        // Botón "Salir"
        btnSalir = new JButton("Salir");
        btnSalir.setBounds(230, 150, 140, 30);
        add(btnSalir);

        // Acciones
        btnVerProductos.addActionListener(e -> {
            ProductosConsultaFrame productosFrame = new ProductosConsultaFrame();
            productosFrame.setVisible(true);
        });

        btnCarrito.addActionListener(e -> {
            CarritoFrame carritoFrame = new CarritoFrame();
            carritoFrame.setVisible(true);
        });

        btnRegresar.addActionListener(e -> {
            new InicioFrame().setVisible(true);
            this.dispose();
        });

        btnSalir.addActionListener(e -> {
            int op = JOptionPane.showConfirmDialog(
                    this,
                    "¿Seguro que deseas salir?",
                    "Confirmar salida",
                    JOptionPane.YES_NO_OPTION
            );
            if (op == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }
}
