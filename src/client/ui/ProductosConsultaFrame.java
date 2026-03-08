package client.ui;

import client.util.Carrito;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import server.model.ProductoDTO;
import server.rmi.ProductoService;
import java.rmi.Naming;
import java.util.List;
import client.util.RMIConfig;


public class ProductosConsultaFrame extends JFrame {

    private JTable tblProductos;
    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JButton btnCerrar;
    private JButton btnAgregar;


public ProductosConsultaFrame() {
    setTitle("Catálogo de productos");
    setSize(600, 350);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLayout(null);

    JLabel lblTitulo = new JLabel("Catálogo de productos (solo consulta)");
    lblTitulo.setBounds(150, 10, 300, 25);
    add(lblTitulo);

    JLabel lblBuscar = new JLabel("Buscar producto:");
    lblBuscar.setBounds(20, 50, 110, 25);
    add(lblBuscar);

    txtBuscar = new JTextField();
    txtBuscar.setBounds(130, 50, 250, 25);
    add(txtBuscar);

    btnAgregar = new JButton("Agregar al carrito");
    btnAgregar.setBounds(20, 280, 160, 25);
    add(btnAgregar);
    btnAgregar.addActionListener(e -> agregarAlCarrito());

    btnBuscar = new JButton("Buscar");
    btnBuscar.setBounds(400, 50, 100, 25);
    add(btnBuscar);

    tblProductos = new JTable();
    JScrollPane scroll = new JScrollPane(tblProductos);
    scroll.setBounds(20, 90, 540, 180);
    add(scroll);

    btnCerrar = new JButton("Cerrar");
    btnCerrar.setBounds(240, 280, 100, 25);
    add(btnCerrar);

    // 🔹 Aquí cambiamos a carga real desde BD
    cargarProductosDesdeServidor(null);

    btnBuscar.addActionListener(e -> buscarRemoto());
    btnCerrar.addActionListener(e -> dispose());
}


private void cargarProductosDesdeServidor(String filtro) {
    try {
        String url = RMIConfig.url("ProductoService");
        ProductoService service = (ProductoService) Naming.lookup(url);

        List<ProductoDTO> productos = service.listarProductos(filtro);

        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Categoría", "Precio", "Existencia"},
                0
        );

        for (ProductoDTO p : productos) {
            modelo.addRow(new Object[]{
                    p.getIdProducto(),
                    p.getNombre(),
                    p.getCategoria(),
                    p.getPrecioVenta(),
                    p.getExistenciaActual()
            });
        }

        tblProductos.setModel(modelo);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "Error al cargar productos desde el servidor: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}


    /**
     * Búsqueda sencilla solo sobre los datos cargados en la tabla de ejemplo.
     */
private void buscarRemoto() {
    String texto = txtBuscar.getText().trim();
    // Simplemente volvemos a cargar desde el servidor con el filtro
    cargarProductosDesdeServidor(texto);
}


private void agregarAlCarrito() {
    int fila = tblProductos.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this, "Selecciona un producto.");
        return;
    }

    int id = (int) tblProductos.getValueAt(fila, 0);
    String nombre = tblProductos.getValueAt(fila, 1).toString();
    double precio = Double.parseDouble(tblProductos.getValueAt(fila, 3).toString());

    // Cantidad fija 1 por ahora (luego agregamos selector)
    Carrito.Item item = new Carrito.Item(id, nombre, 1, precio);

    Carrito.agregar(item);

    JOptionPane.showMessageDialog(this, "Producto agregado al carrito.");
}

}
