package client.ui;

import server.model.VentaItemDTO;
import server.rmi.VentaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.Naming;
import java.text.DecimalFormat;
import java.util.List;
import client.util.RMIConfig;

public class VentaDetalleDialog extends JDialog {

    private final int idVenta;
    private JTable tablaDetalle;
    private JLabel lblTitulo;
    private JLabel lblSubtotal;
    private JLabel lblIva;
    private JLabel lblTotal;
    private JButton btnCerrar;

    private DecimalFormat df = new DecimalFormat("#,##0.00");

    public VentaDetalleDialog(Frame owner, int idVenta) {
        super(owner, "Detalle de venta " + idVenta, true);
        this.idVenta = idVenta;

        setSize(700, 420);
        setLocationRelativeTo(owner);
        setLayout(null);

        lblTitulo = new JLabel("Detalle de la venta ID: " + idVenta);
        lblTitulo.setBounds(20, 10, 400, 25);
        add(lblTitulo);

        tablaDetalle = new JTable();
        JScrollPane scroll = new JScrollPane(tablaDetalle);
        scroll.setBounds(20, 50, 650, 260);
        add(scroll);

        lblSubtotal = new JLabel("Subtotal: $0.00");
        lblSubtotal.setBounds(20, 320, 200, 25);
        add(lblSubtotal);

        lblIva = new JLabel("IVA (16%): $0.00");
        lblIva.setBounds(240, 320, 200, 25);
        add(lblIva);

        lblTotal = new JLabel("Total: $0.00");
        lblTotal.setBounds(460, 320, 200, 25);
        add(lblTotal);

        btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(570, 350, 100, 25);
        add(btnCerrar);

        btnCerrar.addActionListener(e -> dispose());

        cargarDetalle();
    }

    private void cargarDetalle() {
        try {
            String url = RMIConfig.url("VentaService"); // cambia si tu servidor está en otra PC
            VentaService service = (VentaService) Naming.lookup(url);

            List<VentaItemDTO> items = service.obtenerDetalleVenta(idVenta);

            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[]{"ID Producto", "Nombre", "Cantidad", "Precio unitario", "Descuento", "Subtotal"},
                    0
            );

            double subtotal = 0.0;

            for (VentaItemDTO it : items) {
                modelo.addRow(new Object[]{
                        it.getIdProducto(),
                        it.getNombreProducto(),
                        it.getCantidad(),
                        it.getPrecioUnitario(),
                        it.getDescuento(),
                        it.getSubtotal()
                });
                subtotal += it.getSubtotal();
            }

            tablaDetalle.setModel(modelo);

            double iva = subtotal * 0.16;
            double total = subtotal + iva;

            lblSubtotal.setText("Subtotal: $" + df.format(subtotal));
            lblIva.setText("IVA (16%): $" + df.format(iva));
            lblTotal.setText("Total: $" + df.format(total));

            if (items.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "La venta no tiene detalles registrados (Detalle_Venta vacío).",
                        "Sin datos",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar detalle de la venta: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
