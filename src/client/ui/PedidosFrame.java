package client.ui;

import server.model.PedidoDTO;
import server.rmi.PedidoService;
import server.rmi.VentaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.rmi.Naming;
import java.text.SimpleDateFormat;
import java.util.List;
import client.util.RMIConfig;

public class PedidosFrame extends JFrame {

    private JTable tabla;
    private JComboBox<String> cmbEstado;
    private JTextField txtCliente;
    private JButton btnBuscar;
    private JButton btnVerDetalle;
    private JButton btnCancelar;
    private JButton btnCerrar;
    private JTextField txtIdUsuario;
    private JButton btnAutorizarVenta;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public PedidosFrame() {
        setTitle("Gestión de pedidos");
        setSize(750, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel lblTitulo = new JLabel("Pedidos registrados (lado usuario)");
        lblTitulo.setBounds(250, 10, 300, 25);
        add(lblTitulo);

        JLabel lblEstado = new JLabel("Estado:");
        lblEstado.setBounds(20, 50, 50, 25);
        add(lblEstado);

        cmbEstado = new JComboBox<>(new String[]{"TODOS", "PENDIENTE", "ATENDIDO", "CANCELADO"});
        cmbEstado.setBounds(80, 50, 120, 25);
        add(cmbEstado);

        JLabel lblCliente = new JLabel("Cliente:");
        lblCliente.setBounds(230, 50, 60, 25);
        add(lblCliente);

        txtCliente = new JTextField();
        txtCliente.setBounds(290, 50, 200, 25);
        add(txtCliente);

        btnBuscar = new JButton("Filtrar");
        btnBuscar.setBounds(510, 50, 100, 25);
        add(btnBuscar);

        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 90, 700, 220);
        add(scroll);

        // BOTONES ABAJO (sin que se encimen)
        btnVerDetalle = new JButton("Ver detalle");
        btnVerDetalle.setBounds(20, 330, 120, 25);
        add(btnVerDetalle);

        btnAutorizarVenta = new JButton("Autorizar venta");
        btnAutorizarVenta.setBounds(160, 330, 150, 25);
        add(btnAutorizarVenta);

        btnCancelar = new JButton("Cancelar pedido");
        btnCancelar.setBounds(330, 330, 150, 25);
        add(btnCancelar);

        // ID USUARIO (quien autoriza)
        JLabel lblIdUsuario = new JLabel("ID Usuario:");
        lblIdUsuario.setBounds(500, 330, 70, 25);
        add(lblIdUsuario);

        txtIdUsuario = new JTextField("1"); // por defecto 1, puedes cambiarlo
        txtIdUsuario.setBounds(570, 330, 40, 25);
        add(txtIdUsuario);

        btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(620, 330, 100, 25);
        add(btnCerrar);

        // Acciones
        btnBuscar.addActionListener(e -> cargarPedidos());
        btnVerDetalle.addActionListener(e -> verDetalle());
        btnAutorizarVenta.addActionListener(e -> autorizarVentaDePedido());
        btnCancelar.addActionListener(e -> cancelarPedido());
        btnCerrar.addActionListener(e -> dispose());

        // Carga inicial (todos)
        cargarPedidos();
    }

    private void cargarPedidos() {
        String estadoSel = cmbEstado.getSelectedItem().toString();
        String filtroCliente = txtCliente.getText().trim();

        try {
            String url = RMIConfig.url("PedidoService");
            PedidoService service = (PedidoService) Naming.lookup(url);

            List<PedidoDTO> pedidos = service.listarPedidos(estadoSel, filtroCliente);

            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[]{"ID", "Fecha", "Cliente", "Total", "Estado", "Observaciones"},
                    0
            );

            for (PedidoDTO p : pedidos) {
                String fechaStr = (p.getFecha() != null) ? sdf.format(p.getFecha()) : "";
                modelo.addRow(new Object[]{
                        p.getIdPedido(),
                        fechaStr,
                        p.getNombreCliente(),
                        p.getTotal(),
                        p.getEstado(),
                        p.getObservaciones()
                });
            }

            tabla.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar pedidos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void autorizarVentaDePedido() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un pedido primero.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idPedido = (int) tabla.getValueAt(fila, 0);

        // Leer id_usuario desde la caja
        int idUsuario;
        try {
            idUsuario = Integer.parseInt(txtIdUsuario.getText().trim());
            if (idUsuario <= 0) {
                JOptionPane.showMessageDialog(this,
                        "El ID de usuario debe ser mayor a 0.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "ID de usuario inválido. Usa un número entero.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int resp = JOptionPane.showConfirmDialog(
                this,
                "¿Convertir el pedido " + idPedido + " en una venta COMPLETADA?",
                "Confirmar autorización",
                JOptionPane.YES_NO_OPTION
        );

        if (resp != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            String url = RMIConfig.url("VentaService");
            VentaService ventaService = (VentaService) Naming.lookup(url);

            int idVenta = ventaService.registrarVentaDesdePedido(idPedido, idUsuario);

            if (idVenta > 0) {
                JOptionPane.showMessageDialog(this,
                        "Pedido " + idPedido + " convertido en venta.\n" +
                                "Folio de venta: " + idVenta,
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                cargarPedidos();

            } else if (idVenta == -2) {
                JOptionPane.showMessageDialog(this,
                        "El pedido ya no está en estado PENDIENTE.\n" +
                                "No se pudo autorizar la venta.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Ocurrió un problema al generar la venta desde el pedido.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al autorizar venta: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private Integer getIdPedidoSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un pedido de la tabla.",
                    "Sin selección",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return (Integer) tabla.getValueAt(fila, 0);
    }

    private String getEstadoPedidoSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            return null;
        }
        Object val = tabla.getValueAt(fila, 4);
        return (val != null) ? val.toString() : null;
    }

    private void verDetalle() {
        Integer idPedido = getIdPedidoSeleccionado();
        if (idPedido == null) return;

        PedidoDetalleDialog dialog = new PedidoDetalleDialog(this, idPedido);
        dialog.setVisible(true);
    }

    private void cancelarPedido() {
        Integer idPedido = getIdPedidoSeleccionado();
        if (idPedido == null) return;

        String estado = getEstadoPedidoSeleccionado();
        if (!"PENDIENTE".equalsIgnoreCase(estado)) {
            JOptionPane.showMessageDialog(this,
                    "Solo se pueden cancelar pedidos en estado PENDIENTE.",
                    "Operación no permitida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int op = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que deseas cancelar este pedido?",
                "Confirmar cancelación",
                JOptionPane.YES_NO_OPTION
        );
        if (op != JOptionPane.YES_OPTION) {
            return;
        }

        // Pedimos autorización de admin
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
            String url = RMIConfig.url("PedidoService");
            PedidoService service = (PedidoService) Naming.lookup(url);

            boolean ok = service.actualizarEstadoPedido(idPedido, "CANCELADO");

            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Pedido cancelado correctamente.");
                cargarPedidos();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo cancelar el pedido.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cancelar pedido: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
