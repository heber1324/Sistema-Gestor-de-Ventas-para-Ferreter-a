package client.ui;

import server.model.VentaDTO;
import server.rmi.VentaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.rmi.Naming;
import java.text.SimpleDateFormat;
import java.util.List;
import client.util.RMIConfig;

public class VentasFrame extends JFrame {

    private JTable tabla;
    private JComboBox<String> cmbEstado;
    private JTextField txtCliente;
    private JButton btnFiltrar;
    private JButton btnVerDetalle;
    private JButton btnCancelar;
    private JButton btnCerrar;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public VentasFrame() {
        setTitle("Gestión de ventas");
        setSize(800, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel lblTitulo = new JLabel("Ventas registradas");
        lblTitulo.setBounds(320, 10, 200, 25);
        add(lblTitulo);

        JLabel lblEstado = new JLabel("Estado:");
        lblEstado.setBounds(20, 50, 60, 25);
        add(lblEstado);

        cmbEstado = new JComboBox<>(new String[]{"TODOS", "COMPLETADA", "CANCELADA"});
        cmbEstado.setBounds(80, 50, 130, 25);
        add(cmbEstado);

        JLabel lblCliente = new JLabel("Cliente:");
        lblCliente.setBounds(240, 50, 60, 25);
        add(lblCliente);

        txtCliente = new JTextField();
        txtCliente.setBounds(300, 50, 200, 25);
        add(txtCliente);

        btnFiltrar = new JButton("Filtrar");
        btnFiltrar.setBounds(520, 50, 100, 25);
        add(btnFiltrar);

        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 90, 750, 240);
        add(scroll);

        btnVerDetalle = new JButton("Ver detalle");
        btnVerDetalle.setBounds(20, 340, 130, 25);
        add(btnVerDetalle);

        btnCancelar = new JButton("Cancelar venta");
        btnCancelar.setBounds(170, 340, 150, 25);
        add(btnCancelar);

        btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(670, 340, 100, 25);
        add(btnCerrar);

        btnFiltrar.addActionListener(e -> cargarVentas());
        btnVerDetalle.addActionListener(e -> verDetalle());
        btnCancelar.addActionListener(e -> cancelarVenta());
        btnCerrar.addActionListener(e -> dispose());

        cargarVentas();
    }

    private void cargarVentas() {
        String estado = cmbEstado.getSelectedItem().toString();
        String filtroCliente = txtCliente.getText().trim();

        try {
            String url = RMIConfig.url("VentaService");
            VentaService service = (VentaService) Naming.lookup(url);

            List<VentaDTO> ventas = service.listarVentas(estado, filtroCliente);

            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[]{"ID", "Fecha", "Cliente", "Total", "Tipo pago", "Estado", "Observaciones"},
                    0
            );

            for (VentaDTO v : ventas) {
                String fechaStr = (v.getFecha() != null) ? sdf.format(v.getFecha()) : "";
                modelo.addRow(new Object[]{
                        v.getIdVenta(),
                        fechaStr,
                        v.getNombreCliente(),
                        v.getTotal(),
                        v.getTipoPago(),
                        v.getEstado(),
                        v.getObservaciones()
                });
            }

            tabla.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar ventas: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private Integer getIdVentaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una venta de la tabla.",
                    "Sin selección",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return (Integer) tabla.getValueAt(fila, 0);
    }

    private String getEstadoVentaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            return null;
        }
        Object val = tabla.getValueAt(fila, 5);
        return (val != null) ? val.toString() : null;
    }

    private void verDetalle() {
        Integer idVenta = getIdVentaSeleccionada();
        if (idVenta == null) return;

        VentaDetalleDialog dialog = new VentaDetalleDialog(this, idVenta);
        dialog.setVisible(true);
    }

    private void cancelarVenta() {
        Integer idVenta = getIdVentaSeleccionada();
        if (idVenta == null) return;

        String estado = getEstadoVentaSeleccionada();
        if (!"COMPLETADA".equalsIgnoreCase(estado)) {
            JOptionPane.showMessageDialog(this,
                    "Solo se pueden cancelar ventas en estado COMPLETADA.",
                    "Operación no permitida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int op = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que deseas cancelar esta venta?",
                "Confirmar cancelación",
                JOptionPane.YES_NO_OPTION
        );
        if (op != JOptionPane.YES_OPTION) return;

        // Pedir autorización de administrador
        AutorizacionAdminDialog auth = new AutorizacionAdminDialog(this);
        auth.setVisible(true);

        if (!auth.isAutorizado()) {
            JOptionPane.showMessageDialog(this,
                    "No se autorizó la cancelación.",
                    "Cancelación denegada",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String url = RMIConfig.url("VentaService");
            VentaService service = (VentaService) Naming.lookup(url);

            boolean ok = service.cancelarVenta(idVenta);

            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Venta cancelada correctamente.\n" +
                        "El inventario fue ajustado de regreso.");
                cargarVentas();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo cancelar la venta.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cancelar venta: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
