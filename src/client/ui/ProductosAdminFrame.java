// client/ui/ProductosAdminFrame.java
package client.ui;

import server.model.ProductoDTO;
import server.rmi.ProductoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.Naming;
import java.util.List;
import client.util.RMIConfig;

public class ProductosAdminFrame extends JFrame {

    private JTable tabla;
    private JTextField txtFiltroNombre;

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtIdCategoria;
    private JTextField txtPrecio;
    private JTextField txtExistencia;
    private JTextArea txtDescripcion;
    private JComboBox<String> cmbEstado;

    private JButton btnBuscar;
    private JButton btnRefrescar;
    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnCambiarEstado;
    private JButton btnCerrar;

    public ProductosAdminFrame() {
        setTitle("Administración de productos");
        setSize(900, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel lblTitulo = new JLabel("Gestión de productos (altas / cambios / baja lógica)");
        lblTitulo.setBounds(250, 10, 400, 25);
        add(lblTitulo);

        JLabel lblFiltro = new JLabel("Nombre contiene:");
        lblFiltro.setBounds(20, 45, 120, 25);
        add(lblFiltro);

        txtFiltroNombre = new JTextField();
        txtFiltroNombre.setBounds(130, 45, 200, 25);
        add(txtFiltroNombre);

        btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(340, 45, 90, 25);
        add(btnBuscar);

        btnRefrescar = new JButton("Mostrar todos");
        btnRefrescar.setBounds(440, 45, 130, 25);
        add(btnRefrescar);

        // Tabla
        tabla = new JTable();
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBounds(20, 80, 540, 350);
        add(scrollTabla);

        // Panel derecho
        JLabel lblId = new JLabel("ID:");
        lblId.setBounds(580, 80, 80, 25);
        add(lblId);

        txtId = new JTextField();
        txtId.setBounds(650, 80, 80, 25);
        txtId.setEditable(false);
        add(txtId);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(580, 115, 80, 25);
        add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(650, 115, 200, 25);
        add(txtNombre);

        JLabel lblIdCat = new JLabel("ID Cat.:");
        lblIdCat.setBounds(580, 150, 80, 25);
        add(lblIdCat);

        txtIdCategoria = new JTextField();
        txtIdCategoria.setBounds(650, 150, 80, 25);
        add(txtIdCategoria);

        JLabel lblPrecio = new JLabel("Precio:");
        lblPrecio.setBounds(580, 185, 80, 25);
        add(lblPrecio);

        txtPrecio = new JTextField();
        txtPrecio.setBounds(650, 185, 100, 25);
        add(txtPrecio);

        JLabel lblExistencia = new JLabel("Existencia:");
        lblExistencia.setBounds(580, 220, 80, 25);
        add(lblExistencia);

        txtExistencia = new JTextField();
        txtExistencia.setBounds(650, 220, 100, 25);
        add(txtExistencia);

        JLabel lblEstado = new JLabel("Estado:");
        lblEstado.setBounds(580, 255, 80, 25);
        add(lblEstado);

        cmbEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO"});
        cmbEstado.setBounds(650, 255, 120, 25);
        add(cmbEstado);

        JLabel lblDesc = new JLabel("Descripción:");
        lblDesc.setBounds(580, 290, 80, 25);
        add(lblDesc);

        txtDescripcion = new JTextArea();
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setBounds(580, 315, 270, 90);
        add(scrollDesc);

        btnNuevo = new JButton("Nuevo");
        btnNuevo.setBounds(580, 420, 80, 30);
        add(btnNuevo);

        btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(670, 420, 90, 30);
        add(btnGuardar);

        btnCambiarEstado = new JButton("Activar/Desactivar");
        btnCambiarEstado.setBounds(770, 420, 120, 30);
        add(btnCambiarEstado);

        btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(20, 440, 100, 30);
        add(btnCerrar);

        // Eventos
        btnBuscar.addActionListener(e -> cargarProductos());
        btnRefrescar.addActionListener(e -> {
            txtFiltroNombre.setText("");
            cargarProductos();
        });
        btnNuevo.addActionListener(e -> limpiarCampos());
        btnGuardar.addActionListener(e -> guardarProducto());
        btnCambiarEstado.addActionListener(e -> cambiarEstadoProducto());
        btnCerrar.addActionListener(e -> dispose());

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tabla.getSelectedRow();
                if (fila >= 0) {
                    int idProd = (int) tabla.getValueAt(fila, 0);
                    cargarProductoEnFormulario(idProd);
                }
            }
        });

        cargarProductos();
    }

    private void cargarProductos() {
        String filtro = txtFiltroNombre.getText().trim();
        try {
            String url = RMIConfig.url("ProductoService");
            ProductoService service = (ProductoService) Naming.lookup(url);

            List<ProductoDTO> lista = service.listarProductosAdmin(filtro);

            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[]{"ID", "Nombre", "Categoría", "Precio", "Existencia", "Estado"},
                    0
            );

            for (ProductoDTO p : lista) {
                String estadoStr = (p.getActivo() == 1) ? "ACTIVO" : "INACTIVO";
                modelo.addRow(new Object[]{
                        p.getIdProducto(),
                        p.getNombre(),
                        p.getCategoria(),         // nombre de la categoría
                        p.getPrecioVenta(),
                        p.getExistenciaActual(),
                        estadoStr
                });
            }

            tabla.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar productos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cargarProductoEnFormulario(int idProducto) {
        try {
            String url = RMIConfig.url("ProductoService");
            ProductoService service = (ProductoService) Naming.lookup(url);

            ProductoDTO p = service.obtenerProductoPorId(idProducto);
            if (p == null) {
                JOptionPane.showMessageDialog(this,
                        "No se encontró el producto.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            txtId.setText(String.valueOf(p.getIdProducto()));
            txtNombre.setText(p.getNombre());
            txtDescripcion.setText(p.getDescripcion() != null ? p.getDescripcion() : "");
            txtIdCategoria.setText(String.valueOf(p.getIdCategoria()));
            txtPrecio.setText(String.valueOf(p.getPrecioVenta()));
            txtExistencia.setText(String.valueOf(p.getExistenciaActual()));
            cmbEstado.setSelectedItem(p.getActivo() == 1 ? "ACTIVO" : "INACTIVO");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar producto: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtIdCategoria.setText("");
        txtPrecio.setText("");
        txtExistencia.setText("");
        cmbEstado.setSelectedItem("ACTIVO");
        tabla.clearSelection();
    }

    private void guardarProducto() {
        String nombre = txtNombre.getText().trim();
        String idCatStr = txtIdCategoria.getText().trim();
        String precioStr = txtPrecio.getText().trim();
        String existenciaStr = txtExistencia.getText().trim();

        if (nombre.isEmpty() || idCatStr.isEmpty() || precioStr.isEmpty() || existenciaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nombre, ID categoría, precio y existencia son obligatorios.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idCategoria = Integer.parseInt(idCatStr);
            double precio = Double.parseDouble(precioStr);
            int existencia = Integer.parseInt(existenciaStr);

            ProductoDTO p = new ProductoDTO();
            String idStr = txtId.getText().trim();
            int idProducto = 0;
            if (!idStr.isEmpty()) {
                idProducto = Integer.parseInt(idStr);
                p.setIdProducto(idProducto);
            }

            p.setNombre(nombre);
            p.setDescripcion(txtDescripcion.getText().trim());
            p.setIdCategoria(idCategoria);
            p.setPrecioVenta(precio);
            p.setExistenciaActual(existencia);
            p.setActivo("ACTIVO".equals(cmbEstado.getSelectedItem()) ? 1 : 0);

            String url = RMIConfig.url("ProductoService");
            ProductoService service = (ProductoService) Naming.lookup(url);

            if (idProducto == 0) {
                int nuevoId = service.insertarProducto(p);
                if (nuevoId > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Producto registrado con ID " + nuevoId);
                    limpiarCampos();
                    cargarProductos();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo registrar el producto.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                boolean ok = service.actualizarProducto(p);
                if (ok) {
                    JOptionPane.showMessageDialog(this,
                            "Producto actualizado correctamente.");
                    cargarProductos();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo actualizar el producto.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "ID categoría, precio y existencia deben ser numéricos.",
                    "Datos inválidos",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar producto: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cambiarEstadoProducto() {
        String idStr = txtId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un producto de la tabla.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idProducto = Integer.parseInt(idStr);
        String estadoActual = (String) cmbEstado.getSelectedItem();
        int nuevoActivo = "ACTIVO".equalsIgnoreCase(estadoActual) ? 0 : 1;
        String nuevoEstadoStr = (nuevoActivo == 1) ? "ACTIVO" : "INACTIVO";

        int resp = JOptionPane.showConfirmDialog(
                this,
                "¿Cambiar estado de " + estadoActual + " a " + nuevoEstadoStr + "?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );
        if (resp != JOptionPane.YES_OPTION) return;

        try {
            String url = RMIConfig.url("ProductoService");
            ProductoService service = (ProductoService) Naming.lookup(url);

            boolean ok = service.cambiarEstadoProducto(idProducto, nuevoActivo);
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Estado actualizado a " + nuevoEstadoStr);
                cmbEstado.setSelectedItem(nuevoEstadoStr);
                cargarProductos();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo cambiar el estado.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cambiar estado: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
