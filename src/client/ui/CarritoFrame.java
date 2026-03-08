package client.ui;

import client.util.Carrito;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import client.util.Carrito;
import server.model.PedidoDTO;
import server.model.PedidoItemDTO;
import server.rmi.PedidoService;
import client.util.PdfPedidoUtil;
import java.io.File;
import javax.swing.JFileChooser;
import java.rmi.Naming;
import client.util.RMIConfig;


public class CarritoFrame extends JFrame {

    private JTable tabla;
    private JButton btnEliminar, btnVaciar, btnConfirmar, btnCerrar;

    public CarritoFrame() {
        setTitle("Carrito de compras");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(null);

        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 20, 550, 230);
        add(scroll);

        btnEliminar = new JButton("Eliminar producto");
        btnEliminar.setBounds(20, 270, 160, 30);
        add(btnEliminar);

        btnVaciar = new JButton("Vaciar carrito");
        btnVaciar.setBounds(200, 270, 140, 30);
        add(btnVaciar);

        btnConfirmar = new JButton("Confirmar pedido");
        btnConfirmar.setBounds(360, 270, 160, 30);
        add(btnConfirmar);

        btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(240, 320, 120, 30);
        add(btnCerrar);

        cargarTabla();

        btnEliminar.addActionListener(e -> eliminarProducto());
        btnVaciar.addActionListener(e -> vaciarCarrito());
        btnConfirmar.addActionListener(e -> confirmarPedido());
        btnCerrar.addActionListener(e -> dispose());
    }

    private void cargarTabla() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Cantidad", "Precio", "Subtotal"}, 0);

        for (Carrito.Item item : Carrito.getItems()) {
            model.addRow(new Object[]{
                    item.id,
                    item.nombre,
                    item.cantidad,
                    item.precio,
                    item.getSubtotal()
            });
        }

        tabla.setModel(model);
    }

    private void eliminarProducto() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para eliminar.");
            return;
        }

        int id = (int) tabla.getValueAt(fila, 0);
        Carrito.eliminar(id);
        cargarTabla();
    }

    private void vaciarCarrito() {
        Carrito.vaciar();
        cargarTabla();
    }

private void confirmarPedido() {
    if (Carrito.getItems().isEmpty()) {
        JOptionPane.showMessageDialog(this, "El carrito está vacío.");
        return;
    }

    // Pedir nombre del cliente
    String nombreCliente = JOptionPane.showInputDialog(
            this,
            "Nombre del cliente (o público general):",
            "Público general"
    );

    if (nombreCliente == null || nombreCliente.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Debes escribir un nombre para el pedido.");
        return;
    }

    try {
        // 1. Construir el DTO del pedido
        PedidoDTO pedido = new PedidoDTO();
        pedido.setNombreCliente(nombreCliente.trim());
        pedido.setObservaciones("Pedido generado desde modo cliente");
        pedido.setTotal(Carrito.total());  // asumiendo que ya tienes este método

        for (Carrito.Item itemCarrito : Carrito.getItems()) {
            PedidoItemDTO itemDTO = new PedidoItemDTO(
                    itemCarrito.id,
                    itemCarrito.nombre,
                    itemCarrito.cantidad,
                    itemCarrito.precio
            );
            pedido.addItem(itemDTO);
        }

        // 2. Llamar al servicio RMI
        String url = RMIConfig.url("PedidoService");
        PedidoService service = (PedidoService) Naming.lookup(url);

        int idPedido = service.registrarPedido(pedido);

        if (idPedido > 0) {

            // ✅ En lugar de solo un mensaje, mostramos uno con botón "Descargar PDF"
            double subtotal = Carrito.total();      // lo que hoy usas como total
            double iva = subtotal * 0.16;
            double total = subtotal + iva;

            Object[] opciones = {"Descargar PDF", "Cerrar"};
            int opcion = JOptionPane.showOptionDialog(
                    this,
                    """
                    Pedido registrado correctamente.
                    Folio: """ + idPedido +
                    "\nSubtotal: $" + String.format("%.2f", subtotal) +
                    "\nIVA (16%): $" + String.format("%.2f", iva) +
                    "\nTotal: $" + String.format("%.2f", total),
                    "Pedido registrado",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );


            if (opcion == 0) { // "Descargar PDF"
                try {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setDialogTitle("Guardar comprobante de pedido en PDF");
                    chooser.setSelectedFile(new File("pedido_" + idPedido + ".pdf"));

                    int resp = chooser.showSaveDialog(this);
                    if (resp == JFileChooser.APPROVE_OPTION) {
                        File destino = chooser.getSelectedFile();

                        // Llamar a la utilidad de PDF
                        PdfPedidoUtil.generarPdfPedido(
                                idPedido,
                                nombreCliente,
                                Carrito.getItems(),
                                pedido.getTotal(),
                                destino
                        );

                        JOptionPane.showMessageDialog(this,
                                "PDF generado correctamente:\n" + destino.getAbsolutePath(),
                                "PDF creado",
                                JOptionPane.INFORMATION_MESSAGE);
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error al generar el PDF: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }

            // Vaciar carrito al final
            Carrito.vaciar();
            cargarTabla();

        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo registrar el pedido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "Error al enviar el pedido al servidor: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}


}
