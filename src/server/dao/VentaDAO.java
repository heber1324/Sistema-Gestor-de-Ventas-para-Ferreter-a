package server.dao;

import server.model.VentaDTO;
import server.model.VentaItemDTO;
import server.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    
public int insertarVenta(VentaDTO venta) {
    String sqlVenta = "INSERT INTO Ventas " +
            "(fecha, id_cliente, id_usuario, tipo_pago, total, descuento_total, estado, observaciones) " +
            "VALUES (NOW(), ?, ?, ?, ?, ?, ?, ?)";

    String sqlDetalle = "INSERT INTO Detalle_Venta " +
            "(id_venta, id_producto, cantidad, precio_unitario, descuento, subtotal) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    String sqlSelStock = "SELECT existencia_actual FROM Productos WHERE id_producto = ?";
    String sqlUpdStock = "UPDATE Productos SET existencia_actual = ? WHERE id_producto = ?";
    String sqlMov = "INSERT INTO Movimientos_Inventario " +
            "(id_producto, tipo, fecha, cantidad, stock_anterior, stock_nuevo, referencia, id_usuario) " +
            "VALUES (?, 'VENTA', NOW(), ?, ?, ?, ?, ?)";

    Connection conn = null;

    try {
        conn = ConexionBD.getConnection();
        conn.setAutoCommit(false);

        // DEBUG para verificar lo que llega
        System.out.println("DEBUG insertarVenta() llamado. idCliente=" +
                venta.getIdCliente() + ", idUsuario=" + venta.getIdUsuario());

        // 1) Calcular total, descuentos e IVA
        double totalBruto = 0.0;
        double subtotal = 0.0;   // antes lo llamábamos totalNeto

        for (VentaItemDTO item : venta.getItems()) {
            double bruto = item.getCantidad() * item.getPrecioUnitario();
            double neto = item.getSubtotal(); // ya trae descuento aplicado
            totalBruto += bruto;
            subtotal += neto;
        }

        double descuentoTotal = totalBruto - subtotal;
        if (descuentoTotal < 0) {
            descuentoTotal = 0;
        }

        // IVA 16% sobre el subtotal (neto, ya con descuento)
        double iva = subtotal * 0.16;
        double totalConIva = subtotal + iva;

        try (PreparedStatement psVenta = conn.prepareStatement(
                sqlVenta, Statement.RETURN_GENERATED_KEYS)) {

            // ====== PARAM 1: id_cliente (puede ser NULL) ======
            if (venta.getIdCliente() <= 0) {
                psVenta.setNull(1, Types.INTEGER);
            } else {
                psVenta.setInt(1, venta.getIdCliente());
            }

            // ====== PARAM 2: id_usuario (NO PUEDE SER NULL) ======
            int idUsuario = venta.getIdUsuario();
            psVenta.setInt(2, idUsuario);

            // ====== PARAM 3: tipo_pago ======
            psVenta.setString(3, venta.getTipoPago());

            // ====== PARAM 4: total (ya con IVA)
            psVenta.setDouble(4, totalConIva);

            // ====== PARAM 5: descuento_total ======
            psVenta.setDouble(5, descuentoTotal);

            // ====== PARAM 6: estado ======
            psVenta.setString(6, venta.getEstado() != null ? venta.getEstado() : "COMPLETADA");

            // ====== PARAM 7: observaciones ======
            String obs = venta.getObservaciones();
            if (obs == null || obs.trim().isEmpty()) {
                String nombre = (venta.getNombreCliente() == null || venta.getNombreCliente().trim().isEmpty())
                        ? "Público general"
                        : venta.getNombreCliente();
                obs = "Venta registrada desde módulo usuario. Cliente: " + nombre +
                        String.format(" (Subtotal: %.2f, IVA: %.2f, Total: %.2f)", subtotal, iva, totalConIva);
            }
            psVenta.setString(7, obs);

            // EJECUTAR INSERT DE VENTA
            psVenta.executeUpdate();

            int idVentaGenerada = -1;
            try (ResultSet rs = psVenta.getGeneratedKeys()) {
                if (rs.next()) {
                    idVentaGenerada = rs.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el id de la venta.");
                }
            }

            // 3) DETALLE + INVENTARIO
            try (PreparedStatement psDet = conn.prepareStatement(sqlDetalle);
                 PreparedStatement psSel = conn.prepareStatement(sqlSelStock);
                 PreparedStatement psUpd = conn.prepareStatement(sqlUpdStock);
                 PreparedStatement psMov = conn.prepareStatement(sqlMov)) {

                for (VentaItemDTO item : venta.getItems()) {
                    int idProd = item.getIdProducto();
                    int cant = item.getCantidad();

                    // Detalle_Venta
                    psDet.setInt(1, idVentaGenerada);
                    psDet.setInt(2, idProd);
                    psDet.setInt(3, cant);
                    psDet.setDouble(4, item.getPrecioUnitario());
                    psDet.setDouble(5, item.getDescuento());
                    psDet.setDouble(6, item.getSubtotal());
                    psDet.addBatch();

                    // Stock anterior
                    int stockAnterior = 0;
                    psSel.setInt(1, idProd);
                    try (ResultSet rs = psSel.executeQuery()) {
                        if (rs.next()) {
                            stockAnterior = rs.getInt("existencia_actual");
                        }
                    }

                    int stockNuevo = stockAnterior - cant;

                    // Actualizar producto
                    psUpd.setInt(1, stockNuevo);
                    psUpd.setInt(2, idProd);
                    psUpd.executeUpdate();

                    // Movimiento inventario
                    psMov.setInt(1, idProd);
                    psMov.setInt(2, -cant);            // sale del inventario
                    psMov.setInt(3, stockAnterior);
                    psMov.setInt(4, stockNuevo);
                    psMov.setString(5, "V" + idVentaGenerada);
                    psMov.setInt(6, venta.getIdUsuario());  // id_usuario
                    psMov.addBatch();
                }

                psDet.executeBatch();
                psMov.executeBatch();
            }

            conn.commit();

            // Registrar en bitácora con total CON IVA
            try {
                BitacoraDAO bitDAO = new BitacoraDAO();
                String desc = String.format(
                        "Venta registrada. Subtotal: %.2f, IVA: %.2f, Total: %.2f",
                        subtotal, iva, totalConIva
                );
                bitDAO.registrarOperacion(
                        venta.getIdUsuario(),   // usuario que hizo la venta
                        "Ventas",
                        idVentaGenerada,        // id_registro
                        "ALTA",                 // operacion
                        desc
                );
            } catch (Exception ex) {
                System.out.println("No se pudo registrar en bitácora: " + ex.getMessage());
            }

            return idVentaGenerada;

        }

    } catch (SQLException e) {
        System.out.println("Error en VentaDAO.insertarVenta: " + e.getMessage());
        if (conn != null) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
        return -1;
    } finally {
        if (conn != null) {
            try { conn.setAutoCommit(true); conn.close(); } catch (Exception ignored) {}
        }
    }
}



public int registrarVentaDesdePedido(int idPedido, int idUsuario) {
    String sqlPedido = "SELECT id_cliente, estado FROM Pedidos WHERE id_pedido = ?";
    String sqlDetallePedido = "SELECT id_producto, cantidad, precio_unitario, subtotal " +
                              "FROM Detalle_Pedido WHERE id_pedido = ?";

    String sqlVenta = "INSERT INTO Ventas " +
            "(fecha, id_cliente, id_usuario, tipo_pago, total, descuento_total, estado, observaciones) " +
            "VALUES (NOW(), ?, ?, ?, ?, ?, ?, ?)";

    String sqlDetalleVenta = "INSERT INTO Detalle_Venta " +
            "(id_venta, id_producto, cantidad, precio_unitario, descuento, subtotal) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    String sqlSelStock = "SELECT existencia_actual FROM Productos WHERE id_producto = ?";
    String sqlUpdStock = "UPDATE Productos SET existencia_actual = ? WHERE id_producto = ?";
    String sqlMov = "INSERT INTO Movimientos_Inventario " +
            "(id_producto, tipo, fecha, cantidad, stock_anterior, stock_nuevo, referencia, id_usuario) " +
            "VALUES (?, 'VENTA', NOW(), ?, ?, ?, ?, ?)";

    String sqlUpdatePedido = "UPDATE Pedidos SET estado = 'ATENDIDO' WHERE id_pedido = ?";

    Connection conn = null;

    try {
        conn = ConexionBD.getConnection();
        conn.setAutoCommit(false);

        // 1) Leer encabezado del pedido
        int idCliente = 0;
        String estado = null;

        try (PreparedStatement psPed = conn.prepareStatement(sqlPedido)) {
            psPed.setInt(1, idPedido);
            try (ResultSet rs = psPed.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("No se encontró el pedido con id_pedido=" + idPedido);
                }
                idCliente = rs.getInt("id_cliente");
                if (rs.wasNull()) {
                    idCliente = 0;
                }
                estado = rs.getString("estado");
            }
        }

        if (estado == null || !"PENDIENTE".equalsIgnoreCase(estado)) {
            conn.rollback();
            return -2; // código especial: pedido no pendiente
        }

        // 2) Leer detalle del pedido
        List<VentaItemDTO> items = new ArrayList<>();
        double totalBruto = 0.0;
        double subtotal = 0.0;

        try (PreparedStatement psDetPed = conn.prepareStatement(sqlDetallePedido)) {
            psDetPed.setInt(1, idPedido);
            try (ResultSet rs = psDetPed.executeQuery()) {
                while (rs.next()) {
                    int idProd = rs.getInt("id_producto");
                    int cant = rs.getInt("cantidad");
                    double precio = rs.getDouble("precio_unitario");
                    double subtotalLinea = rs.getDouble("subtotal");

                    double desc = (cant * precio) - subtotalLinea;
                    if (desc < 0) {
                        desc = 0;
                    }

                    VentaItemDTO item = new VentaItemDTO(
                            idProd,
                            null,
                            cant,
                            precio,
                            desc,
                            subtotalLinea
                    );
                    items.add(item);

                    totalBruto += cant * precio;
                    subtotal += subtotalLinea;
                }
            }
        }

        if (items.isEmpty()) {
            throw new SQLException("El pedido no tiene detalle (Detalle_Pedido vacío).");
        }

        double descuentoTotal = totalBruto - subtotal;
        if (descuentoTotal < 0) {
            descuentoTotal = 0;
        }

        if (idUsuario <= 0) {
            idUsuario = 1; // por seguridad, usa 1 como admin
        }

        // IVA sobre el subtotal
        double iva = subtotal * 0.16;
        double totalConIva = subtotal + iva;

        // 3) Insertar en Ventas
        int idVentaGenerada;

        try (PreparedStatement psVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {

            // param 1: id_cliente (puede ser NULL)
            if (idCliente <= 0) {
                psVenta.setNull(1, Types.INTEGER);
            } else {
                psVenta.setInt(1, idCliente);
            }

            // param 2: id_usuario
            psVenta.setInt(2, idUsuario);

            // param 3: tipo_pago
            psVenta.setString(3, "EFECTIVO");

            // param 4: total (con IVA)
            psVenta.setDouble(4, totalConIva);

            // param 5: descuento_total
            psVenta.setDouble(5, descuentoTotal);

            // param 6: estado
            psVenta.setString(6, "COMPLETADA");

            // param 7: observaciones
            String obs = String.format(
                    "Venta generada desde pedido %d. Subtotal: %.2f, IVA: %.2f, Total: %.2f",
                    idPedido, subtotal, iva, totalConIva
            );
            psVenta.setString(7, obs);

            psVenta.executeUpdate();

            try (ResultSet rs = psVenta.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new SQLException("No se pudo obtener id_venta generado desde el pedido.");
                }
                idVentaGenerada = rs.getInt(1);
            }
        }

        // 4) Insertar detalle y ajustar inventario
        try (PreparedStatement psDetV = conn.prepareStatement(sqlDetalleVenta);
             PreparedStatement psSel = conn.prepareStatement(sqlSelStock);
             PreparedStatement psUpd = conn.prepareStatement(sqlUpdStock);
             PreparedStatement psMov = conn.prepareStatement(sqlMov)) {

            for (VentaItemDTO item : items) {
                int idProd = item.getIdProducto();
                int cant = item.getCantidad();

                // Detalle_Venta
                psDetV.setInt(1, idVentaGenerada);
                psDetV.setInt(2, idProd);
                psDetV.setInt(3, cant);
                psDetV.setDouble(4, item.getPrecioUnitario());
                psDetV.setDouble(5, item.getDescuento());
                psDetV.setDouble(6, item.getSubtotal());
                psDetV.addBatch();

                // Stock anterior
                int stockAnterior = 0;
                psSel.setInt(1, idProd);
                try (ResultSet rs = psSel.executeQuery()) {
                    if (rs.next()) {
                        stockAnterior = rs.getInt("existencia_actual");
                    }
                }

                int stockNuevo = stockAnterior - cant;

                // Actualizar producto
                psUpd.setInt(1, stockNuevo);
                psUpd.setInt(2, idProd);
                psUpd.executeUpdate();

                // Movimiento inventario
                psMov.setInt(1, idProd);
                psMov.setInt(2, -cant);
                psMov.setInt(3, stockAnterior);
                psMov.setInt(4, stockNuevo);
                psMov.setString(5, "P" + idPedido + "->V" + idVentaGenerada);
                psMov.setInt(6, idUsuario);
                psMov.addBatch();
            }

            psDetV.executeBatch();
            psMov.executeBatch();
        }

        // 5) Actualizar estado del pedido
        try (PreparedStatement psUp = conn.prepareStatement(sqlUpdatePedido)) {
            psUp.setInt(1, idPedido);
            psUp.executeUpdate();
        }

        conn.commit();

        // Registrar en bitácora con IVA
        try {
            BitacoraDAO bitDAO = new BitacoraDAO();
            String desc = String.format(
                    "Venta generada desde pedido %d. Subtotal: %.2f, IVA: %.2f, Total: %.2f",
                    idPedido, subtotal, iva, totalConIva
            );
            bitDAO.registrarOperacion(
                    idUsuario,
                    "Ventas",
                    idVentaGenerada,
                    "ALTA",
                    desc
            );
        } catch (Exception ex) {
            System.out.println("No se pudo registrar en bitácora: " + ex.getMessage());
        }

        return idVentaGenerada;

    } catch (SQLException e) {
        System.out.println("Error en VentaDAO.registrarVentaDesdePedido: " + e.getMessage());
        if (conn != null) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
        return -1;
    } finally {
        if (conn != null) {
            try { conn.setAutoCommit(true); conn.close(); } catch (Exception ignored) {}
        }
    }
}

    /**
     * Lista ventas, opcionalmente filtradas por estado y nombre de cliente.
     * estado: "COMPLETADA", "CANCELADA" o "TODOS"/null para todas.
     */
    public List<VentaDTO> listarVentas(String estado, String filtroCliente) {
        List<VentaDTO> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT v.id_venta, v.fecha, " +
                "       COALESCE(c.nombre, 'Público general') AS cliente, " +
                "       v.total, v.tipo_pago, v.estado, v.observaciones " +
                "FROM Ventas v " +
                "LEFT JOIN Clientes c ON v.id_cliente = c.id_cliente " +
                "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (estado != null && !estado.trim().isEmpty() && !"TODOS".equalsIgnoreCase(estado)) {
            sql.append(" AND v.estado = ? ");
            params.add(estado);
        }

        if (filtroCliente != null && !filtroCliente.trim().isEmpty()) {
            sql.append(" AND c.nombre LIKE ? ");
            params.add("%" + filtroCliente.trim() + "%");
        }

        sql.append(" ORDER BY v.fecha DESC");

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    VentaDTO dto = new VentaDTO();
                    dto.setIdVenta(rs.getInt("id_venta"));
                    dto.setFecha(rs.getTimestamp("fecha"));
                    dto.setNombreCliente(rs.getString("cliente"));
                    dto.setTotal(rs.getDouble("total"));
                    dto.setTipoPago(rs.getString("tipo_pago"));
                    dto.setEstado(rs.getString("estado"));
                    dto.setObservaciones(rs.getString("observaciones"));
                    lista.add(dto);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error en VentaDAO.listarVentas: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Obtiene el detalle de una venta.
     */
    public List<VentaItemDTO> obtenerDetalleVenta(int idVenta) {
        List<VentaItemDTO> items = new ArrayList<>();

        String sql = "SELECT dv.id_producto, p.nombre, dv.cantidad, " +
                     "       dv.precio_unitario, dv.descuento, dv.subtotal " +
                     "FROM Detalle_Venta dv " +
                     "JOIN Productos p ON dv.id_producto = p.id_producto " +
                     "WHERE dv.id_venta = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idVenta);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idProd = rs.getInt("id_producto");
                    String nombre = rs.getString("nombre");
                    int cantidad = rs.getInt("cantidad");
                    double precio = rs.getDouble("precio_unitario");
                    double desc = rs.getDouble("descuento");
                    double subtotal = rs.getDouble("subtotal");

                    VentaItemDTO item = new VentaItemDTO(idProd, nombre, cantidad, precio, desc, subtotal);
                    items.add(item);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error en VentaDAO.obtenerDetalleVenta: " + e.getMessage());
        }

        return items;
    }

    /**
     * Cancela una venta:
     * - Cambia estado a CANCELADA.
     * - Regresa el stock de cada producto (existencia_actual + cantidad).
     * - Inserta movimientos de inventario tipo 'VENTA_CANCELADA'.
     *
     * IMPORTANTE: se asume que al registrar la venta original ya se había
     * descontado inventario. Aquí lo devolvemos.
     */
    public boolean cancelarVenta(int idVenta) {
        String sqlDetalle = "SELECT dv.id_producto, dv.cantidad " +
                            "FROM Detalle_Venta dv WHERE dv.id_venta = ?";
        String sqlProd = "SELECT existencia_actual FROM Productos WHERE id_producto = ?";
        String sqlUpdateProd = "UPDATE Productos SET existencia_actual = ? WHERE id_producto = ?";
        String sqlMov = "INSERT INTO Movimientos_Inventario " +
        "(id_producto, tipo, fecha, cantidad, stock_anterior, stock_nuevo, referencia, id_usuario) " +
        "VALUES (?, 'VENTA_CANCELADA', NOW(), ?, ?, ?, ?, ?)";


        String sqlUpdateVenta = "UPDATE Ventas SET estado = 'CANCELADA' WHERE id_venta = ?";

        Connection conn = null;

        try {
            conn = ConexionBD.getConnection();
            conn.setAutoCommit(false);

            // 1. Obtener detalle de la venta
            List<int[]> detalle = new ArrayList<>(); // [id_producto, cantidad]
            try (PreparedStatement psDet = conn.prepareStatement(sqlDetalle)) {
                psDet.setInt(1, idVenta);
                try (ResultSet rs = psDet.executeQuery()) {
                    while (rs.next()) {
                        int idProd = rs.getInt("id_producto");
                        int cant = rs.getInt("cantidad");
                        detalle.add(new int[]{idProd, cant});
                    }
                }
            }

            // 2. Por cada producto, regresar stock y registrar movimiento
            try (PreparedStatement psProd = conn.prepareStatement(sqlProd);
                 PreparedStatement psUpdProd = conn.prepareStatement(sqlUpdateProd);
                 PreparedStatement psMov = conn.prepareStatement(sqlMov)) {

                for (int[] par : detalle) {
                    int idProd = par[0];
                    int cant = par[1];

                    // stock anterior
                    int stockAnterior = 0;
                    psProd.setInt(1, idProd);
                    try (ResultSet rs = psProd.executeQuery()) {
                        if (rs.next()) {
                            stockAnterior = rs.getInt("existencia_actual");
                        }
                    }

                    int stockNuevo = stockAnterior + cant;

                    // actualizar producto
                    psUpdProd.setInt(1, stockNuevo);
                    psUpdProd.setInt(2, idProd);
                    psUpdProd.executeUpdate();

                    // movimiento inventario (cantidad positiva porque entra de nuevo)
                    psMov.setInt(1, idProd);
                    psMov.setInt(2, cant);             // entra al inventario de nuevo
                    psMov.setInt(3, stockAnterior);
                    psMov.setInt(4, stockNuevo);
                    psMov.setString(5, "V" + idVenta);
                    psMov.setInt(6, 1);
                    psMov.addBatch();

                }
                psMov.executeBatch();
            }

            // 3. Actualizar estado de la venta
            try (PreparedStatement psVenta = conn.prepareStatement(sqlUpdateVenta)) {
                psVenta.setInt(1, idVenta);
                int filas = psVenta.executeUpdate();
                if (filas == 0) {
                    throw new SQLException("No se encontró la venta para cancelar.");
                }
            }

            conn.commit();
            try {
    BitacoraDAO bitDAO = new BitacoraDAO();
    String desc = "Venta cancelada. ID venta: " + idVenta;
    // De momento puedes usar id_usuario fijo o luego pasar el real
    bitDAO.registrarOperacion(
            1,              // TODO: cambiar cuando tengas el usuario que cancela
            "Ventas",
            idVenta,
            "CANCELACION",
            desc
    );
} catch (Exception ex) {
    System.out.println("No se pudo registrar en bitácora: " + ex.getMessage());
}

            return true;

        } catch (SQLException e) {
            System.out.println("Error en VentaDAO.cancelarVenta: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (Exception ignored) {}
            }
        }
    }
}
