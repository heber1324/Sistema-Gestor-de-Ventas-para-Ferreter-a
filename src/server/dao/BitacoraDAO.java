// server/dao/BitacoraDAO.java
package server.dao;

import server.model.BitacoraOperacionDTO;
import server.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BitacoraDAO {

    /**
     * Registra una operación en la bitácora.
     *
     * @param idUsuario      usuario que realiza la operación
     * @param tablaAfectada  nombre lógico de la tabla ('Ventas', 'Productos', etc.)
     * @param idRegistro     id del registro afectado (ej. id_venta)
     * @param operacion      'ALTA','MODIFICACION','BAJA_LOGICA','CANCELACION'
     * @param descripcion    texto breve explicando qué se hizo
     */
    public void registrarOperacion(int idUsuario,
                                   String tablaAfectada,
                                   int idRegistro,
                                   String operacion,
                                   String descripcion) {

        String sql = "INSERT INTO bitacora_operaciones " +
                     "(fecha, id_usuario, tabla_afectada, id_registro, operacion, descripcion) " +
                     "VALUES (NOW(), ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setString(2, tablaAfectada);
            ps.setInt(3, idRegistro);
            ps.setString(4, operacion);
            ps.setString(5, descripcion);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error en BitacoraDAO.registrarOperacion: " + e.getMessage());
        } 
        
    }
    /**
     * Lista solo operaciones donde tabla_afectada = 'Ventas',
     * con filtros opcionales de fecha y usuario.
     *
     * @param fechaInicio  'YYYY-MM-DD' o null
     * @param fechaFin     'YYYY-MM-DD' o null
     * @param filtroUsuario parte del nombre del usuario (optional)
     */
    public List<BitacoraOperacionDTO> listarVentasEnBitacora(
            String fechaInicio,
            String fechaFin,
            String filtroUsuario) {

        List<BitacoraOperacionDTO> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT b.id_evento, b.fecha, b.id_usuario, " +
                "       u.nombre AS nombre_usuario, " +
                "       b.tabla_afectada, b.id_registro, b.operacion, b.descripcion " +
                "FROM bitacora_operaciones b " +
                "JOIN usuarios u ON b.id_usuario = u.id_usuario " +
                "WHERE b.tabla_afectada = 'Ventas' "
        );

        List<Object> params = new ArrayList<>();

        if (fechaInicio != null && !fechaInicio.trim().isEmpty()) {
            sql.append(" AND b.fecha >= ? ");
            params.add(Timestamp.valueOf(fechaInicio.trim() + " 00:00:00"));
        }

        if (fechaFin != null && !fechaFin.trim().isEmpty()) {
            sql.append(" AND b.fecha <= ? ");
            params.add(Timestamp.valueOf(fechaFin.trim() + " 23:59:59"));
        }

        if (filtroUsuario != null && !filtroUsuario.trim().isEmpty()) {
            sql.append(" AND u.nombre LIKE ? ");
            params.add("%" + filtroUsuario.trim() + "%");
        }

        sql.append(" ORDER BY b.fecha DESC");

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BitacoraOperacionDTO dto = new BitacoraOperacionDTO();
                    dto.setIdEvento(rs.getInt("id_evento"));
                    dto.setFecha(rs.getTimestamp("fecha"));
                    dto.setIdUsuario(rs.getInt("id_usuario"));
                    dto.setNombreUsuario(rs.getString("nombre_usuario"));
                    dto.setTablaAfectada(rs.getString("tabla_afectada"));
                    dto.setIdRegistro(rs.getInt("id_registro"));
                    dto.setOperacion(rs.getString("operacion"));
                    dto.setDescripcion(rs.getString("descripcion"));
                    lista.add(dto);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error en BitacoraDAO.listarVentasEnBitacora: " + e.getMessage());
        }

        return lista;
    }
}
