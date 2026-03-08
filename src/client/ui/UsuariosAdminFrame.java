package client.ui;

import server.model.UsuarioDTO;
import server.rmi.UsuarioService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.Naming;
import java.util.List;
import client.util.RMIConfig;

public class UsuariosAdminFrame extends JFrame {

    private JTable tabla;
    private JTextField txtFiltro;

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRol;
    private JComboBox<String> cmbEstado;

    private JButton btnBuscar;
    private JButton btnRefrescar;
    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnCambiarEstado;
    private JButton btnCerrar;

    public UsuariosAdminFrame() {
        setTitle("Administración de usuarios");
        setSize(950, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // ====== Filtro arriba ======
        JLabel lblTitulo = new JLabel("Gestión de usuarios (altas / cambios / baja lógica)");
        lblTitulo.setBounds(260, 10, 450, 25);
        add(lblTitulo);

        JLabel lblFiltro = new JLabel("Nombre / Usuario contiene:");
        lblFiltro.setBounds(20, 45, 180, 25);
        add(lblFiltro);

        txtFiltro = new JTextField();
        txtFiltro.setBounds(200, 45, 200, 25);
        add(txtFiltro);

        btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(410, 45, 90, 25);
        add(btnBuscar);

        btnRefrescar = new JButton("Mostrar todos");
        btnRefrescar.setBounds(510, 45, 130, 25);
        add(btnRefrescar);

        // ====== Tabla izquierda ======
        tabla = new JTable();
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBounds(20, 80, 540, 350);
        add(scrollTabla);

        // ====== Panel derecho (datos de usuario) ======
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

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setBounds(580, 150, 80, 25);
        add(lblUsuario);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(660, 150, 250, 25);
        add(txtUsuario);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setBounds(580, 185, 80, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(660, 185, 250, 25);
        add(txtPassword);

        JLabel lblRol = new JLabel("Rol:");
        lblRol.setBounds(580, 220, 80, 25);
        add(lblRol);

        cmbRol = new JComboBox<>(new String[]{
                "ADMIN",
                "CAJERO",
                "INVITADO"
        });
        cmbRol.setBounds(660, 220, 150, 25);
        add(cmbRol);

        JLabel lblEstado = new JLabel("Estado:");
        lblEstado.setBounds(580, 255, 80, 25);
        add(lblEstado);

        cmbEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO"});
        cmbEstado.setBounds(660, 255, 120, 25);
        add(cmbEstado);

        // ====== Botones inferiores ======
        btnNuevo = new JButton("Nuevo");
        btnNuevo.setBounds(580, 310, 90, 30);
        add(btnNuevo);

        btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(680, 310, 100, 30);
        add(btnGuardar);

        btnCambiarEstado = new JButton("Activar/Desactivar");
        btnCambiarEstado.setBounds(790, 310, 140, 30);
        add(btnCambiarEstado);

        btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(20, 440, 100, 30);
        add(btnCerrar);

        // ====== Eventos ======
        btnBuscar.addActionListener(e -> cargarUsuarios());
        btnRefrescar.addActionListener(e -> {
            txtFiltro.setText("");
            cargarUsuarios();
        });
        btnNuevo.addActionListener(e -> limpiarCampos());
        btnGuardar.addActionListener(e -> guardarUsuario());
        btnCambiarEstado.addActionListener(e -> cambiarEstadoUsuario());
        btnCerrar.addActionListener(e -> dispose());

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tabla.getSelectedRow();
                if (fila >= 0) {
                    int idUsr = (int) tabla.getValueAt(fila, 0);
                    cargarUsuarioEnFormulario(idUsr);
                }
            }
        });

        // Carga inicial
        cargarUsuarios();
    }

    // ================== LÓGICA ==================

    private void cargarUsuarios() {
        String filtro = txtFiltro.getText().trim();
        try {
            String url = RMIConfig.url("UsuarioService");
            UsuarioService service = (UsuarioService) Naming.lookup(url);

            List<UsuarioDTO> lista = service.listarUsuarios(filtro);

            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[]{"ID", "Nombre", "Usuario", "Rol", "Estado"},
                    0
            );

            for (UsuarioDTO u : lista) {
                String estadoStr = (u.getActivo() == 1) ? "ACTIVO" : "INACTIVO";
                modelo.addRow(new Object[]{
                        u.getIdUsuario(),
                        u.getNombre(),
                        u.getUsuario(),
                        u.getRol(),
                        estadoStr
                });
            }

            tabla.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar usuarios: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cargarUsuarioEnFormulario(int idUsuario) {
        try {
            String url = RMIConfig.url("UsuarioService");
            UsuarioService service = (UsuarioService) Naming.lookup(url);

            UsuarioDTO u = service.obtenerUsuarioPorId(idUsuario);
            if (u == null) {
                JOptionPane.showMessageDialog(this,
                        "No se encontró el usuario.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            txtId.setText(String.valueOf(u.getIdUsuario()));
            txtNombre.setText(u.getNombre());
            txtUsuario.setText(u.getUsuario());
            // Por seguridad no rellenamos la contraseña
            txtPassword.setText("");
            cmbRol.setSelectedItem(u.getRol());
            cmbEstado.setSelectedItem(u.getActivo() == 1 ? "ACTIVO" : "INACTIVO");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar usuario: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        txtUsuario.setText("");
        txtPassword.setText("");
        cmbRol.setSelectedItem("CAJERO");   // por ejemplo
        cmbEstado.setSelectedItem("ACTIVO");
        tabla.clearSelection();
    }

    private void guardarUsuario() {
        String nombre = txtNombre.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (nombre.isEmpty() || usuario.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nombre y Usuario son obligatorios.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            UsuarioDTO u = new UsuarioDTO();

            String idStr = txtId.getText().trim();
            int idUsuario = 0;
            if (!idStr.isEmpty()) {
                idUsuario = Integer.parseInt(idStr);
                u.setIdUsuario(idUsuario);
            }

            u.setNombre(nombre);
            u.setUsuario(usuario);
            u.setRol((String) cmbRol.getSelectedItem());
            u.setActivo("ACTIVO".equals(cmbEstado.getSelectedItem()) ? 1 : 0);

            // Para nuevos usuarios, la contraseña es obligatoria
            if (idUsuario == 0) {
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Para un usuario nuevo, la contraseña es obligatoria.",
                            "Datos incompletos",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                u.setPasswordHash(password);
            } else {
                // Para edición:
                // si password viene vacío, NO cambiamos contraseña
                // si viene con algo, se actualiza
                if (!password.isEmpty()) {
                    u.setPasswordHash(password);
                } else {
                    u.setPasswordHash("");  // el DAO entiende "" como "no cambiar"
                }
            }

            String url = RMIConfig.url("UsuarioService");
            UsuarioService service = (UsuarioService) Naming.lookup(url);

            if (idUsuario == 0) {
                int nuevoId = service.insertarUsuario(u);
                if (nuevoId > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Usuario registrado con ID " + nuevoId);
                    limpiarCampos();
                    cargarUsuarios();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo registrar el usuario.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                boolean ok = service.actualizarUsuario(u);
                if (ok) {
                    JOptionPane.showMessageDialog(this,
                            "Usuario actualizado correctamente.");
                    cargarUsuarios();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo actualizar el usuario.",
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
                    "Error al guardar usuario: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cambiarEstadoUsuario() {
        String idStr = txtId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un usuario de la tabla.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idUsuario = Integer.parseInt(idStr);
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
            String url = RMIConfig.url("UsuarioService");
            UsuarioService service = (UsuarioService) Naming.lookup(url);

            boolean ok = service.cambiarEstadoUsuario(idUsuario, nuevoActivo);
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Estado actualizado a " + nuevoEstadoStr);
                cmbEstado.setSelectedItem(nuevoEstadoStr);
                cargarUsuarios();
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
