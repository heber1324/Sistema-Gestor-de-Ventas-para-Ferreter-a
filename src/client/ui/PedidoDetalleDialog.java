package client.ui;

import server.model.PedidoItemDTO;
import server.rmi.PedidoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.Naming;
import java.util.List;
import client.util.RMIConfig;

public class PedidoDetalleDialog extends JDialog {

    private JTable tabla;
    private JButton btnCerrar;
    private int idPedido;

    public PedidoDetalleDialog(Frame parent, int idPedido) {
        super(parent, "Detalle del pedido #" + idPedido, true);
        this.idPedido = idPedido;

        setSize(600, 350);
        setLocationRelativeTo(parent);
        setLayout(null);

        JLabel lblTitulo = new JLabel("Detalle del pedido #" + idPedido);
        lblTitulo.setBounds(200, 10, 250, 25);
        add(lblTitulo);

        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 50, 550, 220);
        add(scroll);

        btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(250, 280, 100, 25);
        add(btnCerrar);

        btnCerrar.addActionListener(e -> dispose());

        cargarDetalle();
    }

    private void cargarDetalle() {
        try {
            String url = RMIConfig.url("PedidoService");
            PedidoService service = (PedidoService) Naming.lookup(url);

            List<PedidoItemDTO> items = service.obtenerDetallePedido(idPedido);

            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[]{"ID Producto", "Nombre", "Cantidad", "Precio", "Subtotal"}, 0
            );

            for (PedidoItemDTO item : items) {
                modelo.addRow(new Object[]{
                        item.getIdProducto(),
                        item.getNombreProducto(),
                        item.getCantidad(),
                        item.getPrecioUnitario(),
                        item.getSubtotal()
                });
            }

            tabla.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar detalle: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
