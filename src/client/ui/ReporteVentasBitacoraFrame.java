// client/ui/ReporteVentasBitacoraFrame.java
package client.ui;

import server.model.BitacoraOperacionDTO;
import server.rmi.BitacoraService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.rmi.Naming;
import java.text.SimpleDateFormat;
import java.util.List;
import client.util.RMIConfig;

public class ReporteVentasBitacoraFrame extends JFrame {

    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JTextField txtUsuario;
    private JButton btnFiltrar;
    private JButton btnLimpiar;
    private JButton btnCerrar;
    private JButton btnVerVenta;   // 👈 nuevo botón

    private JTable tabla;
    private JLabel lblResumen;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public ReporteVentasBitacoraFrame() {
        setTitle("Reporte de ventas (bitácora)");
        setSize(950, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel lblTitulo = new JLabel("Registro de operaciones sobre Ventas (bitacora_operaciones)");
        lblTitulo.setBounds(200, 10, 550, 25);
        add(lblTitulo);

        JLabel lblFI = new JLabel("Fecha inicio (yyyy-MM-dd):");
        lblFI.setBounds(20, 50, 180, 25);
        add(lblFI);

        txtFechaInicio = new JTextField();
        txtFechaInicio.setBounds(200, 50, 120, 25);
        add(txtFechaInicio);

        JLabel lblFF = new JLabel("Fecha fin (yyyy-MM-dd):");
        lblFF.setBounds(340, 50, 160, 25);
        add(lblFF);

        txtFechaFin = new JTextField();
        txtFechaFin.setBounds(500, 50, 120, 25);
        add(txtFechaFin);

        JLabel lblUsu = new JLabel("Usuario contiene:");
        lblUsu.setBounds(640, 50, 120, 25);
        add(lblUsu);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(760, 50, 150, 25);
        add(txtUsuario);

        btnFiltrar = new JButton("Filtrar");
        btnFiltrar.setBounds(20, 80, 100, 25);
        add(btnFiltrar);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(130, 80, 100, 25);
        add(btnLimpiar);

        // 👇 nuevo botón
        btnVerVenta = new JButton("Ver venta");
        btnVerVenta.setBounds(240, 80, 120, 25);
        add(btnVerVenta);

        btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(810, 420, 90, 30);
        add(btnCerrar);

        tabla = new JTable();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 120, 900, 260);
        add(scroll);

        lblResumen = new JLabel("Registros: 0");
        lblResumen.setBounds(20, 390, 400, 25);
        add(lblResumen);

        // Eventos
        btnFiltrar.addActionListener(e -> cargarBitacora());
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        btnCerrar.addActionListener(e -> dispose());
        btnVerVenta.addActionListener(e -> verVentaSeleccionada()); // 👈 acción nueva

        // Carga inicial (todo)
        cargarBitacora();
    }

    private void limpiarFiltros() {
        txtFechaInicio.setText("");
        txtFechaFin.setText("");
        txtUsuario.setText("");
        cargarBitacora();
    }

    private void cargarBitacora() {
        String fi = txtFechaInicio.getText().trim();
        String ff = txtFechaFin.getText().trim();
        String usu = txtUsuario.getText().trim();

        try {
            String url = RMIConfig.url("BitacoraService");
            BitacoraService service = (BitacoraService) Naming.lookup(url);

            List<server.model.BitacoraOperacionDTO> lista =
                    service.listarVentasEnBitacora(
                            fi.isEmpty() ? null : fi,
                            ff.isEmpty() ? null : ff,
                            usu.isEmpty() ? null : usu
                    );

            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[]{
                            "ID evento",
                            "Fecha/Hora",
                            "Usuario",
                            "Operación",
                            "ID venta",
                            "Descripción"
                    },
                    0
            );

            for (BitacoraOperacionDTO b : lista) {
                String fechaStr = (b.getFecha() != null)
                        ? sdf.format(b.getFecha())
                        : "";
                modelo.addRow(new Object[]{
                        b.getIdEvento(),
                        fechaStr,
                        b.getNombreUsuario(),
                        b.getOperacion(),      // ALTA, MODIFICACION, CANCELACION, etc.
                        b.getIdRegistro(),     // este es el id_venta
                        b.getDescripcion()
                });
            }

            tabla.setModel(modelo);
            lblResumen.setText("Registros: " + lista.size());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar bitácora: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // ============= NUEVO MÉTODO =============
    /**
     * Abre una ventana con el detalle de la venta seleccionada,
     * usando el ID venta que viene en la columna "ID venta".
     */
    private void verVentaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un registro de la tabla.",
                    "Sin selección",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object valorIdVenta = tabla.getValueAt(fila, 4); // columna "ID venta"
        if (valorIdVenta == null) {
            JOptionPane.showMessageDialog(this,
                    "El registro seleccionado no tiene un ID de venta válido.",
                    "Dato inválido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idVenta;
        try {
            if (valorIdVenta instanceof Integer) {
                idVenta = (Integer) valorIdVenta;
            } else {
                idVenta = Integer.parseInt(valorIdVenta.toString());
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "ID de venta inválido: " + valorIdVenta,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Aquí reutilizamos el mismo diálogo que ya usas en el módulo de Ventas
        try {
            VentaDetalleDialog dialog = new VentaDetalleDialog(this, idVenta);
            dialog.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo abrir el detalle de la venta: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
