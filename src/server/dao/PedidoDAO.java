package server.dao;

import server.model.PedidoDTO;
import server.model.PedidoItemDTO;
import server.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    /**
     * Inserta un pedido y su detalle en la base de datos.
     * Devuelve el id_pedido generado, o -1 si algo falla.
     */
    public int insertarPedido(PedidoDTO pedido) {
        String sqlPedido = "INSERT INTO Pedidos (nombre_cliente, total, estado, observaciones) " +
                           "VALUES (?, ?, 'PENDIENTE', ?)";

        String sqlDetalle = "INSERT INTO Detalle_Pedido " +
                            "(id_pedido, id_producto, cantidad, precio_unitario, subtotal) " +
                            "VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement psPedido = null;
        PreparedStatement psDetalle = null;
        ResultSet rsKeys = null;

        try {
            conn = ConexionBD.getConnection();
            conn.setAutoCommit(false); // iniciar transacción

            // 1. Insertar encabezado
            psPedido = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
            psPedido.setString(1, pedido.getNombreCliente());
            psPedido.setDouble(2, pedido.getTotal());
            psPedido.setString(3, pedido.getObservaciones());
            psPedido.executeUpdate();

            rsKeys = psPedido.getGeneratedKeys();
            int idPedidoGenerado = -1;
            if (rsKeys.next()) {
                idPedidoGenerado = rsKeys.getInt(1);
            } else {
                throw new SQLException("No se pudo obtener el id del pedido.");
            }

            // 2. Insertar detalle
            psDetalle = conn.prepareStatement(sqlDetalle);
            for (PedidoItemDTO item : pedido.getItems()) {
                psDetalle.setInt(1, idPedidoGenerado);
                psDetalle.setInt(2, item.getIdProducto());
                psDetalle.setInt(3, item.getCantidad());
                psDetalle.setDouble(4, item.getPrecioUnitario());
                psDetalle.setDouble(5, item.getSubtotal());
                psDetalle.addBatch();
            }
            psDetalle.executeBatch();

            conn.commit(); // confirmar transacción
            return idPedidoGenerado;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return -1;
        } finally {
            try { if (rsKeys != null) rsKeys.close(); } catch (Exception ignored) {}
            try { if (psDetalle != null) psDetalle.close(); } catch (Exception ignored) {}
            try { if (psPedido != null) psPedido.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (Exception ignored) {}
        }
    }

    /**
     * Lista pedidos según estado (opcional) y nombre cliente (opcional).
     * estado puede ser: "PENDIENTE", "ATENDIDO", "CANCELADO" o null/"" para todos.
     */
    public List<PedidoDTO> listarPedidos(String estado, String filtroNombreCliente) {
        List<PedidoDTO> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT id_pedido, fecha, nombre_cliente, total, estado, observaciones " +
                "FROM Pedidos WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (estado != null && !estado.trim().isEmpty() && !"TODOS".equalsIgnoreCase(estado)) {
            sql.append(" AND estado = ? ");
            params.add(estado);
        }

        if (filtroNombreCliente != null && !filtroNombreCliente.trim().isEmpty()) {
            sql.append(" AND nombre_cliente LIKE ? ");
            params.add("%" + filtroNombreCliente.trim() + "%");
        }

        sql.append(" ORDER BY fecha DESC");

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Setear parámetros dinámicos
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PedidoDTO dto = new PedidoDTO();
                    dto.setIdPedido(rs.getInt("id_pedido"));
                    dto.setFecha(rs.getTimestamp("fecha"));
                    dto.setNombreCliente(rs.getString("nombre_cliente"));
                    dto.setTotal(rs.getDouble("total"));
                    dto.setEstado(rs.getString("estado"));
                    dto.setObservaciones(rs.getString("observaciones"));
                    lista.add(dto);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error en PedidoDAO.listarPedidos: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Obtiene el detalle (productos) de un pedido.
     */
    public List<PedidoItemDTO> obtenerDetallePedido(int idPedido) {
        List<PedidoItemDTO> items = new ArrayList<>();

        String sql = "SELECT dp.id_producto, p.nombre, dp.cantidad, dp.precio_unitario " +
                     "FROM Detalle_Pedido dp " +
                     "JOIN Productos p ON dp.id_producto = p.id_producto " +
                     "WHERE dp.id_pedido = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idPedido);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idProd = rs.getInt("id_producto");
                    String nombre = rs.getString("nombre");
                    int cantidad = rs.getInt("cantidad");
                    double precio = rs.getDouble("precio_unitario");

                    PedidoItemDTO item = new PedidoItemDTO(idProd, nombre, cantidad, precio);
                    items.add(item);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error en PedidoDAO.obtenerDetallePedido: " + e.getMessage());
        }

        return items;
    }

    /**
     * Cambia el estado de un pedido.
     * nuevoEstado: 'PENDIENTE', 'ATENDIDO' o 'CANCELADO'.
     */
    public boolean actualizarEstadoPedido(int idPedido, String nuevoEstado) {
        String sql = "UPDATE Pedidos SET estado = ? WHERE id_pedido = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idPedido);

            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error en PedidoDAO.actualizarEstadoPedido: " + e.getMessage());
            return false;
        }
    }
}
