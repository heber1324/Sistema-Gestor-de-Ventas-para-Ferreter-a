// client/ui/ClientesAdminFrame.java
package client.ui;

import server.model.ClienteDTO;
import server.rmi.ClienteService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.Naming;
import java.util.List;
import client.util.RMIConfig;

public class ClientesAdminFrame extends JFrame {

    private JTable tabla;
    private JTextField txtFiltroNombre;

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JComboBox<String> cmbTipoCliente;
    private JTextArea txtDireccion;
    private JComboBox<String> cmbEstado;

    private JButton btnBuscar;
    private JButton btnRefrescar;
    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnCambiarEstado;
    private JButton btnCerrar;

    public ClientesAdminFrame() {
        setTitle("Administración de clientes");
        setSize(950, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel lblTitulo = new JLabel("Gestión de clientes (altas / cambios / baja lógica)");
        lblTitulo.setBounds(260, 10, 450, 25);
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
        txtId.setBounds(660, 80, 80, 25);
        txtId.setEditable(false);
        add(txtId);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(580, 115, 80, 25);
        add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(660, 115, 250, 25);
        add(txtNombre);

        JLabel lblTel = new JLabel("Teléfono:");
        lblTel.setBounds(580, 150, 80, 25);
        add(lblTel);

        txtTelefono = new JTextField();
        txtTelefono.setBounds(660, 150, 250, 25);
        add(txtTelefono);

        JLabel lblCorreo = new JLabel("Correo:");
        lblCorreo.setBounds(580, 185, 80, 25);
        add(lblCorreo);

        txtCorreo = new JTextField();
        txtCorreo.setBounds(660, 185, 250, 25);
        add(txtCorreo);

        JLabel lblTipo = new JLabel("Tipo:");
        lblTipo.setBounds(580, 220, 80, 25);
        add(lblTipo);

        cmbTipoCliente = new JComboBox<>(new String[]{
                "PUBLICO_GENERAL",
                "CONTRATISTA",
                "MAYORISTA"
        });
        cmbTipoCliente.setBounds(660, 220, 150, 25);
        add(cmbTipoCliente);

        JLabel lblEstado = new JLabel("Estado:");
        lblEstado.setBounds(580, 255, 80, 25);
        add(lblEstado);

        cmbEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO"});
        cmbEstado.setBounds(660, 255, 120, 25);
        add(cmbEstado);

        JLabel lblDir = new JLabel("Dirección:");
        lblDir.setBounds(580, 290, 80, 25);
        add(lblDir);

        txtDireccion = new JTextArea();
        JScrollPane scrollDir = new JScrollPane(txtDireccion);
        scrollDir.setBounds(580, 315, 330, 90);
        add(scrollDir);

        btnNuevo = new JButton("Nuevo");
        btnNuevo.setBounds(580, 420, 90, 30);
        add(btnNuevo);

        btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(680, 420, 100, 30);
        add(btnGuardar);

        btnCambiarEstado = new JButton("Activar/Desactivar");
        btnCambiarEstado.setBounds(790, 420, 140, 30);
        add(btnCambiarEstado);

        btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(20, 440, 100, 30);
        add(btnCerrar);

        // Eventos
        btnBuscar.addActionListener(e -> cargarClientes());
        btnRefrescar.addActionListener(e -> {
            txtFiltroNombre.setText("");
            cargarClientes();
        });
        btnNuevo.addActionListener(e -> limpiarCampos());
        btnGuardar.addActionListener(e -> guardarCliente());
        btnCambiarEstado.addActionListener(e -> cambiarEstadoCliente());
        btnCerrar.addActionListener(e -> dispose());

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tabla.getSelectedRow();
                if (fila >= 0) {
                    int idCli = (int) tabla.getValueAt(fila, 0);
                    cargarClienteEnFormulario(idCli);
                }
            }
        });

        cargarClientes();
    }

    private void cargarClientes() {
        String filtro = txtFiltroNombre.getText().trim();
        try {
            String url = RMIConfig.url("ClienteService");
            ClienteService service = (ClienteService) Naming.lookup(url);

            List<ClienteDTO> lista = service.listarClientesAdmin(filtro);

            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[]{"ID", "Nombre", "Teléfono", "Correo", "Tipo", "Estado"},
                    0
            );

            for (ClienteDTO c : lista) {
                String estadoStr = (c.getActivo() == 1) ? "ACTIVO" : "INACTIVO";
                modelo.addRow(new Object[]{
                        c.getIdCliente(),
                        c.getNombre(),
                        c.getTelefono(),
                        c.getCorreo(),
                        c.getTipoCliente(),
                        estadoStr
                });
            }

            tabla.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar clientes: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cargarClienteEnFormulario(int idCliente) {
        try {
            String url = RMIConfig.url("ClienteService");
            ClienteService service = (ClienteService) Naming.lookup(url);

            ClienteDTO c = service.obtenerClientePorId(idCliente);
            if (c == null) {
                JOptionPane.showMessageDialog(this,
                        "No se encontró el cliente.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            txtId.setText(String.valueOf(c.getIdCliente()));
            txtNombre.setText(c.getNombre());
            txtTelefono.setText(c.getTelefono());
            txtCorreo.setText(c.getCorreo());
            txtDireccion.setText(c.getDireccion() != null ? c.getDireccion() : "");
            cmbTipoCliente.setSelectedItem(c.getTipoCliente());
            cmbEstado.setSelectedItem(c.getActivo() == 1 ? "ACTIVO" : "INACTIVO");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar cliente: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtDireccion.setText("");
        cmbTipoCliente.setSelectedItem("PUBLICO_GENERAL");
        cmbEstado.setSelectedItem("ACTIVO");
        tabla.clearSelection();
    }

    private void guardarCliente() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El nombre es obligatorio.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            ClienteDTO c = new ClienteDTO();

            String idStr = txtId.getText().trim();
            int idCliente = 0;
            if (!idStr.isEmpty()) {
                idCliente = Integer.parseInt(idStr);
                c.setIdCliente(idCliente);
            }

            c.setNombre(nombre);
            c.setTelefono(txtTelefono.getText().trim());
            c.setCorreo(txtCorreo.getText().trim());
            c.setDireccion(txtDireccion.getText().trim());
            c.setTipoCliente((String) cmbTipoCliente.getSelectedItem());
            c.setActivo("ACTIVO".equals(cmbEstado.getSelectedItem()) ? 1 : 0);

            String url = RMIConfig.url("ClienteService");
            ClienteService service = (ClienteService) Naming.lookup(url);

            if (idCliente == 0) {
                int nuevoId = service.insertarCliente(c);
                if (nuevoId > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Cliente registrado con ID " + nuevoId);
                    limpiarCampos();
                    cargarClientes();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo registrar el cliente.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                boolean ok = service.actualizarCliente(c);
                if (ok) {
                    JOptionPane.showMessageDialog(this,
                            "Cliente actualizado correctamente.");
                    cargarClientes();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo actualizar el cliente.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "ID inválido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar cliente: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cambiarEstadoCliente() {
        String idStr = txtId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un cliente de la tabla.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCliente = Integer.parseInt(idStr);
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
            String url = RMIConfig.url("ClienteService");
            ClienteService service = (ClienteService) Naming.lookup(url);

            boolean ok = service.cambiarEstadoCliente(idCliente, nuevoActivo);
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Estado actualizado a " + nuevoEstadoStr);
                cmbEstado.setSelectedItem(nuevoEstadoStr);
                cargarClientes();
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
