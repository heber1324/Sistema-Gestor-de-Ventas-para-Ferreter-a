package client.ui;

import client.util.CarritoVenta;
import server.model.VentaDTO;
import server.model.VentaItemDTO;
import server.model.ClienteDTO;
import server.rmi.ProductoService;
import server.rmi.VentaService;
import server.rmi.ClienteService;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.rmi.Naming;
import java.util.List;
import client.util.RMIConfig;

public class RegistrarVentaFrame extends JFrame {
    
    private JLabel lblSubtotal;
    private JLabel lblIva;
    private JLabel lblTotal;
    private DecimalFormat df = new DecimalFormat("#,##0.00");
    
    private JTextField txtIdUsuario;
    private JComboBox<ClienteItem> cmbClientes;
    private JTable tblProductos;
    private JTable tblCarrito;
    private JTextField txtDescuento;
    private JComboBox<String> cmbPago;

    private JButton btnAgregar;
    private JButton btnQuitar;
    private JButton btnRegistrar;
    private JButton btnCerrar;

    // Clase interna para mostrar "nombre" pero guardar id_cliente
    private static class ClienteItem {
        int idCliente;
        String nombre;

        ClienteItem(int idCliente, String nombre) {
            this.idCliente = idCliente;
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    public RegistrarVentaFrame() {
        setTitle("Registrar nueva venta");
        setSize(900, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // ====== ID USUARIO (MANUAL) ======
        JLabel lblIdUsuario = new JLabel("ID Usuario:");
        lblIdUsuario.setBounds(20, 20, 80, 25);
        add(lblIdUsuario);

        txtIdUsuario = new JTextField("1"); // puedes dejar 1 por defecto
        txtIdUsuario.setBounds(100, 20, 60, 25);
        add(txtIdUsuario);

        // ====== CLIENTE ======
        JLabel lblCliente = new JLabel("Cliente:");
        lblCliente.setBounds(180, 20, 60, 25);
        add(lblCliente);

        cmbClientes = new JComboBox<>();
        cmbClientes.setBounds(240, 20, 250, 25);
        add(cmbClientes);

        // ====== TIPO DE PAGO ======
        JLabel lblPago = new JLabel("Tipo de pago:");
        lblPago.setBounds(510, 20, 100, 25);
        add(lblPago);

        cmbPago = new JComboBox<>(new String[]{"EFECTIVO", "TARJETA", "TRANSFERENCIA"});
        cmbPago.setBounds(600, 20, 150, 25);
        add(cmbPago);

        // ====== TABLA DE PRODUCTOS ======
        JLabel lblProductos = new JLabel("Productos:");
        lblProductos.setBounds(20, 60, 100, 25);
        add(lblProductos);

        tblProductos = new JTable();
        JScrollPane sp1 = new JScrollPane(tblProductos);
        sp1.setBounds(20, 90, 400, 300);
        add(sp1);

        // ====== TABLA CARRITO ======
        JLabel lblCarrito = new JLabel("Carrito de venta:");
        lblCarrito.setBounds(450, 60, 150, 25);
        add(lblCarrito);

        tblCarrito = new JTable();
        JScrollPane sp2 = new JScrollPane(tblCarrito);
        sp2.setBounds(450, 90, 400, 300);
        add(sp2);

        // ====== DESCUENTO Y BOTONES CARRITO ======
        JLabel lblDesc = new JLabel("Descuento:");
        lblDesc.setBounds(20, 400, 80, 25);
        add(lblDesc);

        txtDescuento = new JTextField("0");
        txtDescuento.setBounds(100, 400, 100, 25);
        add(txtDescuento);

        btnAgregar = new JButton("Agregar →");
        btnAgregar.setBounds(220, 400, 120, 30);
        add(btnAgregar);

        btnQuitar = new JButton("← Quitar");
        btnQuitar.setBounds(350, 400, 120, 30);
        add(btnQuitar);

        // ====== LABELS DE SUBTOTAL / IVA / TOTAL ======
        lblSubtotal = new JLabel("Subtotal: $0.00");
        lblSubtotal.setBounds(520, 400, 200, 25);
        add(lblSubtotal);

        lblIva = new JLabel("IVA (16%): $0.00");
        lblIva.setBounds(520, 425, 200, 25);
        add(lblIva);

        lblTotal = new JLabel("Total: $0.00");
        lblTotal.setBounds(520, 450, 200, 25);
        add(lblTotal);

        // ====== BOTONES FINALES ======
        btnRegistrar = new JButton("Registrar venta");
        btnRegistrar.setBounds(680, 400, 150, 30);
        add(btnRegistrar);

        btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(680, 440, 150, 30);
        add(btnCerrar);

        // ====== ACCIONES ======
        btnAgregar.addActionListener(e -> agregarAlCarrito());
        btnQuitar.addActionListener(e -> quitarDelCarrito());
        btnRegistrar.addActionListener(e -> registrarVenta());
        btnCerrar.addActionListener(e -> dispose());

        cargarClientes();
        cargarProductos();
        actualizarCarrito(); // esto ya recalcula totales
    }

    // ==========================
    // Cargar clientes desde BD
    // ==========================
    private void cargarClientes() {
        try {
            String url = RMIConfig.url("ClienteService");
            ClienteService service = (ClienteService) Naming.lookup(url);

            List<ClienteDTO> lista = service.listarClientes();

            cmbClientes.removeAllItems();
            // Opción "Público general"
            cmbClientes.addItem(new ClienteItem(0, "Público general"));

            for (ClienteDTO c : lista) {
                cmbClientes.addItem(new ClienteItem(c.getIdCliente(), c.getNombre()));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar clientes: " + e.getMessage());
            e.printStackTrace();

            cmbClientes.removeAllItems();
            cmbClientes.addItem(new ClienteItem(0, "Público general"));
        }
    }

    // ==========================
    // Cargar productos desde BD
    // ==========================
    private void cargarProductos() {
        try {
            String url = RMIConfig.url("ProductoService");
            ProductoService service = (ProductoService) Naming.lookup(url);

            List<Object[]> lista = service.listarProductosParaTabla();

            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[]{"ID", "Nombre", "Precio", "Existencia"}, 0
            );

            for (Object[] fila : lista) {
                modelo.addRow(fila);
            }

            tblProductos.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar productos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==========================
    // Actualizar tabla carrito
    // ==========================
    private void actualizarCarrito() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Cant.", "Precio", "Desc", "Subtotal"}, 0
        );

        for (CarritoVenta.Item item : CarritoVenta.getItems()) {
            modelo.addRow(new Object[]{
                    item.idProducto,
                    item.nombre,
                    item.cantidad,
                    item.precioUnitario,
                    item.descuento,
                    item.getSubtotal()
            });
        }

        tblCarrito.setModel(modelo);
        recalcularTotales();  // 👈 cada vez que se actualiza la tabla, recalculamos IVA y total
    }

    // ==========================
    // Recalcular Subtotal / IVA / Total
    // ==========================
    private void recalcularTotales() {
        double subtotal = 0.0;

        for (CarritoVenta.Item item : CarritoVenta.getItems()) {
            subtotal += item.getSubtotal();
        }

        double iva = subtotal * 0.16;
        double total = subtotal + iva;

        lblSubtotal.setText("Subtotal: $" + df.format(subtotal));
        lblIva.setText("IVA (16%): $" + df.format(iva));
        lblTotal.setText("Total: $" + df.format(total));
    }

    private void agregarAlCarrito() {
        int fila = tblProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto.");
            return;
        }

        int idProd = (int) tblProductos.getValueAt(fila, 0);
        String nombre = tblProductos.getValueAt(fila, 1).toString();
        double precio = Double.parseDouble(tblProductos.getValueAt(fila, 2).toString());

        double desc;
        try {
            desc = Double.parseDouble(txtDescuento.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Descuento inválido. Usa un número.");
            return;
        }

        int cantidad = 1;

        CarritoVenta.agregar(new CarritoVenta.Item(idProd, nombre, cantidad, precio, desc));
        actualizarCarrito(); // ya recalcula totales
    }

    private void quitarDelCarrito() {
        int fila = tblCarrito.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un item del carrito.");
            return;
        }

        CarritoVenta.getItems().remove(fila);
        actualizarCarrito(); // ya recalcula totales
    }

    // ==========================
    // Registrar venta
    // ==========================
    private void registrarVenta() {
        if (CarritoVenta.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.");
            return;
        }

        // 1) Leer y validar ID de usuario
        int idUsuario;
        try {
            idUsuario = Integer.parseInt(txtIdUsuario.getText().trim());
            if (idUsuario <= 0) {
                JOptionPane.showMessageDialog(this, "El ID de usuario debe ser mayor a 0.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID de usuario inválido. Usa un número entero.");
            return;
        }

        try {
            VentaDTO venta = new VentaDTO();

            // Cliente
            ClienteItem sel = (ClienteItem) cmbClientes.getSelectedItem();
            if (sel != null) {
                venta.setIdCliente(sel.idCliente);
                if (sel.idCliente == 0) {
                    venta.setNombreCliente(null);  // Público general
                } else {
                    venta.setNombreCliente(sel.nombre);
                }
            }

            venta.setTipoPago(cmbPago.getSelectedItem().toString());
            venta.setEstado("COMPLETADA");
            venta.setObservaciones("Venta registrada en módulo usuario");

            // ID USUARIO introducido manualmente
            venta.setIdUsuario(idUsuario);

            // Items
            for (CarritoVenta.Item c : CarritoVenta.getItems()) {
                VentaItemDTO v = new VentaItemDTO(
                        c.idProducto,
                        c.nombre,
                        c.cantidad,
                        c.precioUnitario,
                        c.descuento,
                        c.getSubtotal()
                );
                venta.getItems().add(v);
            }

            String url = RMIConfig.url("VentaService");
            VentaService service = (VentaService) Naming.lookup(url);

            int idVenta = service.registrarVenta(venta);

            if (idVenta > 0) {
                // Recalculamos totales igual que en la interfaz
                double subtotal = 0.0;
                for (CarritoVenta.Item item : CarritoVenta.getItems()) {
                    subtotal += item.getSubtotal();
                }
                double iva = subtotal * 0.16;
                double total = subtotal + iva;

                JOptionPane.showMessageDialog(this,
                        "Venta registrada correctamente.\n" +
                                "Folio: " + idVenta + "\n" +
                                "Subtotal: $" + df.format(subtotal) + "\n" +
                                "IVA (16%): $" + df.format(iva) + "\n" +
                                "Total: $" + df.format(total));

                CarritoVenta.limpiar();
                actualizarCarrito(); // limpia tabla y resetea totales

            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo registrar la venta.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al registrar venta: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
