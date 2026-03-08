// server/dao/UsuarioDAO.java
package server.dao;

import server.model.UsuarioDTO;
import server.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // ========== LOGIN ==========
    public UsuarioDTO login(String usuario, String passwordPlano) {
        String sql = "SELECT id_usuario, nombre, usuario, password_hash, rol, activo, " +
                     "       fecha_creacion, fecha_baja, id_usuario_baja " +
                     "FROM usuarios " +
                     "WHERE usuario = ? AND password_hash = ? AND activo = 1";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, passwordPlano);   // por ahora comparas texto plano

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error en UsuarioDAO.login: " + e.getMessage());
        }

        return null;
    }

    // ========== LISTAR PARA ADMIN ==========
    public List<UsuarioDTO> listarUsuarios(String filtroNombreOUsuario) {
        List<UsuarioDTO> lista = new ArrayList<>();

        String sql = "SELECT id_usuario, nombre, usuario, password_hash, rol, activo, " +
                     "       fecha_creacion, fecha_baja, id_usuario_baja " +
                     "FROM usuarios WHERE 1=1";

        if (filtroNombreOUsuario != null && !filtroNombreOUsuario.trim().isEmpty()) {
            sql += " AND (nombre LIKE ? OR usuario LIKE ?)";
        }

        sql += " ORDER BY nombre ASC";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (filtroNombreOUsuario != null && !filtroNombreOUsuario.trim().isEmpty()) {
                String like = "%" + filtroNombreOUsuario.trim() + "%";
                ps.setString(1, like);
                ps.setString(2, like);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearUsuario(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error en UsuarioDAO.listarUsuarios: " + e.getMessage());
        }

        return lista;
    }

    public UsuarioDTO obtenerUsuarioPorId(int idUsuario) {
        String sql = "SELECT id_usuario, nombre, usuario, password_hash, rol, activo, " +
                     "       fecha_creacion, fecha_baja, id_usuario_baja " +
                     "FROM usuarios WHERE id_usuario = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error en UsuarioDAO.obtenerUsuarioPorId: " + e.getMessage());
        }

        return null;
    }

    // ========== INSERTAR ==========
    public int insertarUsuario(UsuarioDTO u) {
        String sql = "INSERT INTO usuarios " +
                     "(nombre, usuario, password_hash, rol, activo) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getNombre());
            ps.setString(2, u.getUsuario());
            ps.setString(3, u.getPasswordHash());
            ps.setString(4, u.getRol());
            ps.setInt(5, u.getActivo());  // normalmente 1

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);  // id_usuario generado
                }
            }

        } catch (SQLException e) {
            System.out.println("Error en UsuarioDAO.insertarUsuario: " + e.getMessage());
        }

        return -1;
    }

    // ========== ACTUALIZAR ==========
    public boolean actualizarUsuario(UsuarioDTO u) {
        // Si passwordHash viene null o vacío, NO tocamos la contraseña
        boolean cambiarPassword = (u.getPasswordHash() != null &&
                                   !u.getPasswordHash().trim().isEmpty());

        String sqlConPwd = "UPDATE usuarios " +
                "SET nombre = ?, usuario = ?, password_hash = ?, rol = ?, activo = ? " +
                "WHERE id_usuario = ?";

        String sqlSinPwd = "UPDATE usuarios " +
                "SET nombre = ?, usuario = ?, rol = ?, activo = ? " +
                "WHERE id_usuario = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(cambiarPassword ? sqlConPwd : sqlSinPwd)) {

            int idx = 1;
            ps.setString(idx++, u.getNombre());
            ps.setString(idx++, u.getUsuario());

            if (cambiarPassword) {
                ps.setString(idx++, u.getPasswordHash());
            }

            ps.setString(idx++, u.getRol());
            ps.setInt(idx++, u.getActivo());
            ps.setInt(idx, u.getIdUsuario());

            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error en UsuarioDAO.actualizarUsuario: " + e.getMessage());
            return false;
        }
    }

    // ========== CAMBIAR ESTADO (BAJA LÓGICA / REACTIVAR) ==========
    public boolean cambiarEstadoUsuario(int idUsuario, int nuevoActivo) {
        String sqlActivar =
                "UPDATE usuarios " +
                "SET activo = 1, fecha_baja = NULL, id_usuario_baja = NULL " +
                "WHERE id_usuario = ?";

        String sqlDesactivar =
                "UPDATE usuarios " +
                "SET activo = 0, fecha_baja = NOW(), id_usuario_baja = NULL " +
                "WHERE id_usuario = ?";

        String sql = (nuevoActivo == 1) ? sqlActivar : sqlDesactivar;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error en UsuarioDAO.cambiarEstadoUsuario: " + e.getMessage());
            return false;
        }
    }

    // ========== helper para mapear ResultSet → DTO ==========
    private UsuarioDTO mapearUsuario(ResultSet rs) throws SQLException {
        UsuarioDTO u = new UsuarioDTO();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setNombre(rs.getString("nombre"));
        u.setUsuario(rs.getString("usuario"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRol(rs.getString("rol"));
        u.setActivo(rs.getInt("activo"));
        u.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        u.setFechaBaja(rs.getTimestamp("fecha_baja"));

        int idUsrBaja = rs.getInt("id_usuario_baja");
        if (rs.wasNull()) {
            u.setIdUsuarioBaja(null);
        } else {
            u.setIdUsuarioBaja(idUsrBaja);
        }

        return u;
    }
}
