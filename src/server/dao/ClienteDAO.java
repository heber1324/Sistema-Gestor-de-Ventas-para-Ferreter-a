// server/dao/ClienteDAO.java
package server.dao;

import server.model.ClienteDTO;
import server.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    /**
     * Lista clientes activos (activo = 1).
     * Útil para combos, pedidos, ventas, etc.
     */
    // server/dao/ClienteDAO.java

public List<ClienteDTO> listarClientes() {
List<ClienteDTO> lista = new ArrayList<>();

        String sql = "SELECT id_cliente, nombre, direccion, telefono, correo, " +
                     "       tipo_cliente, activo, fecha_creacion, fecha_baja, id_usuario_baja " +
                     "FROM clientes " +
                     "WHERE activo = 1";

    return listarClientes(null);
}
    
    public List<ClienteDTO> listarClientes(String filtroNombre) {
        List<ClienteDTO> lista = new ArrayList<>();

        String sql = "SELECT id_cliente, nombre, direccion, telefono, correo, " +
                     "       tipo_cliente, activo, fecha_creacion, fecha_baja, id_usuario_baja " +
                     "FROM clientes " +
                     "WHERE activo = 1";

        if (filtroNombre != null && !filtroNombre.trim().isEmpty()) {
            sql += " AND nombre LIKE ?";
        }

        sql += " ORDER BY nombre ASC";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (filtroNombre != null && !filtroNombre.trim().isEmpty()) {
                ps.setString(1, "%" + filtroNombre.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ClienteDTO c = mapearCliente(rs);
                    lista.add(c);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error en ClienteDAO.listarClientes: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Lista clientes para administración (incluye activos e inactivos).
     */
    public List<ClienteDTO> listarClientesAdmin(String filtroNombre) {
        List<ClienteDTO> lista = new ArrayList<>();

        String sql = "SELECT id_cliente, nombre, direccion, telefono, correo, " +
                     "       tipo_cliente, activo, fecha_creacion, fecha_baja, id_usuario_baja " +
                     "FROM clientes WHERE 1=1";

        if (filtroNombre != null && !filtroNombre.trim().isEmpty()) {
            sql += " AND nombre LIKE ?";
        }

        sql += " ORDER BY nombre ASC";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (filtroNombre != null && !filtroNombre.trim().isEmpty()) {
                ps.setString(1, "%" + filtroNombre.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ClienteDTO c = mapearCliente(rs);
                    lista.add(c);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error en ClienteDAO.listarClientesAdmin: " + e.getMessage());
        }

        return lista;
    }

    public ClienteDTO obtenerClientePorId(int idCliente) {
        String sql = "SELECT id_cliente, nombre, direccion, telefono, correo, " +
                     "       tipo_cliente, activo, fecha_creacion, fecha_baja, id_usuario_baja " +
                     "FROM clientes WHERE id_cliente = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error en ClienteDAO.obtenerClientePorId: " + e.getMessage());
        }

        return null;
    }

    public int insertarCliente(ClienteDTO c) {
        String sql = "INSERT INTO clientes " +
                     "(nombre, direccion, telefono, correo, tipo_cliente, activo) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDireccion());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getCorreo());
            ps.setString(5, c.getTipoCliente());
            ps.setInt(6, c.getActivo());  // normalmente 1 para nuevos

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // id_cliente generado
                }
            }

        } catch (SQLException e) {
            System.out.println("Error en ClienteDAO.insertarCliente: " + e.getMessage());
        }

        return -1;
    }

    public boolean actualizarCliente(ClienteDTO c) {
        String sql = "UPDATE clientes " +
                     "SET nombre = ?, direccion = ?, telefono = ?, correo = ?, " +
                     "    tipo_cliente = ?, activo = ? " +
                     "WHERE id_cliente = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDireccion());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getCorreo());
            ps.setString(5, c.getTipoCliente());
            ps.setInt(6, c.getActivo());
            ps.setInt(7, c.getIdCliente());

            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error en ClienteDAO.actualizarCliente: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cambia el estado del cliente:
     * - Si nuevoActivo = 1 → se reactiva: activo=1, fecha_baja=NULL, id_usuario_baja=NULL
     * - Si nuevoActivo = 0 → se da de baja lógica: activo=0, fecha_baja=NOW(), id_usuario_baja=NULL (por ahora)
     */
    public boolean cambiarEstadoCliente(int idCliente, int nuevoActivo) {
        String sqlActivar =
                "UPDATE clientes " +
                "SET activo = 1, fecha_baja = NULL, id_usuario_baja = NULL " +
                "WHERE id_cliente = ?";

        String sqlDesactivar =
                "UPDATE clientes " +
                "SET activo = 0, fecha_baja = NOW(), id_usuario_baja = NULL " +
                "WHERE id_cliente = ?";

        String sql = (nuevoActivo == 1) ? sqlActivar : sqlDesactivar;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error en ClienteDAO.cambiarEstadoCliente: " + e.getMessage());
            return false;
        }
    }

    // ==== Helper para evitar repetir código ====
    private ClienteDTO mapearCliente(ResultSet rs) throws SQLException {
        ClienteDTO c = new ClienteDTO();
        c.setIdCliente(rs.getInt("id_cliente"));
        c.setNombre(rs.getString("nombre"));
        c.setDireccion(rs.getString("direccion"));
        c.setTelefono(rs.getString("telefono"));
        c.setCorreo(rs.getString("correo"));
        c.setTipoCliente(rs.getString("tipo_cliente"));
        c.setActivo(rs.getInt("activo"));
        c.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        c.setFechaBaja(rs.getTimestamp("fecha_baja"));

        int idUsrBaja = rs.getInt("id_usuario_baja");
        if (rs.wasNull()) {
            c.setIdUsuarioBaja(null);
        } else {
            c.setIdUsuarioBaja(idUsrBaja);
        }

        return c;
    }
}
