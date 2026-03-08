package server.dao;

import server.model.ProductoDTO;
import server.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    /**
     * Lista productos activos. Si filtroNombre no es null/ vacío,
     * filtra por nombre que contenga ese texto.
     */
public List<ProductoDTO> listarProductos(String filtroNombre) {
    List<ProductoDTO> lista = new ArrayList<>();

    String sql = "SELECT p.id_producto, p.nombre, p.descripcion, " +
                 "       p.id_categoria, c.nombre AS categoria, " +
                 "       p.precio_venta, p.existencia_actual, p.activo " +
                 "FROM Productos p " +
                 "JOIN Categorias c ON p.id_categoria = c.id_categoria " +
                 "WHERE p.activo = 1";

    if (filtroNombre != null && !filtroNombre.trim().isEmpty()) {
        sql += " AND p.nombre LIKE ?";
    }

    try (Connection conn = ConexionBD.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        if (filtroNombre != null && !filtroNombre.trim().isEmpty()) {
            ps.setString(1, "%" + filtroNombre.trim() + "%");
        }

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id_producto");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                int idCategoria = rs.getInt("id_categoria");
                String categoria = rs.getString("categoria");
                double precio = rs.getDouble("precio_venta");
                int existencia = rs.getInt("existencia_actual");
                int activo = rs.getInt("activo");

                // Usa el constructor que tú tengas definido
                ProductoDTO dto = new ProductoDTO(
                        id,
                        nombre,
                        categoria,   // nombre de la categoría
                        precio,
                        existencia,
                        idCategoria,
                        descripcion,
                        activo
                );
                lista.add(dto);
            }
        }

    } catch (SQLException e) {
        System.out.println("Error en ProductoDAO.listarProductos: " + e.getMessage());
    }

    return lista;
}

public List<ProductoDTO> listarProductosAdmin(String filtroNombre) {
    List<ProductoDTO> lista = new ArrayList<>();

    String sql = "SELECT p.id_producto, p.nombre, p.descripcion, " +
                 "       p.id_categoria, c.nombre AS categoria, " +
                 "       p.precio_venta, p.existencia_actual, p.activo " +
                 "FROM Productos p " +
                 "JOIN Categorias c ON p.id_categoria = c.id_categoria " +
                 "WHERE 1=1";

    if (filtroNombre != null && !filtroNombre.trim().isEmpty()) {
        sql += " AND p.nombre LIKE ?";
    }

    sql += " ORDER BY p.nombre ASC";

    try (Connection conn = ConexionBD.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        if (filtroNombre != null && !filtroNombre.trim().isEmpty()) {
            ps.setString(1, "%" + filtroNombre.trim() + "%");
        }

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id_producto");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                int idCategoria = rs.getInt("id_categoria");
                String categoria = rs.getString("categoria");
                double precio = rs.getDouble("precio_venta");
                int existencia = rs.getInt("existencia_actual");
                int activo = rs.getInt("activo");

                ProductoDTO dto = new ProductoDTO(
                        id,
                        nombre,
                        categoria,   // nombre de la categoría
                        precio,
                        existencia,
                        idCategoria,
                        descripcion,
                        activo
                );
                lista.add(dto);
            }
        }

    } catch (SQLException e) {
        System.out.println("Error en ProductoDAO.listarProductosAdmin: " + e.getMessage());
    }

    return lista;
}

public ProductoDTO obtenerProductoPorId(int idProducto) {
    String sql = "SELECT p.id_producto, p.nombre, p.descripcion, " +
                 "       p.id_categoria, c.nombre AS categoria, " +
                 "       p.precio_venta, p.existencia_actual, p.activo " +
                 "FROM Productos p " +
                 "JOIN Categorias c ON p.id_categoria = c.id_categoria " +
                 "WHERE p.id_producto = ?";

    try (Connection conn = ConexionBD.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, idProducto);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int id = rs.getInt("id_producto");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                int idCategoria = rs.getInt("id_categoria");
                String categoria = rs.getString("categoria");
                double precio = rs.getDouble("precio_venta");
                int existencia = rs.getInt("existencia_actual");
                int activo = rs.getInt("activo");

                return new ProductoDTO(
                        id,
                        nombre,
                        categoria,
                        precio,
                        existencia,
                        idCategoria,
                        descripcion,
                        activo
                );
            }
        }

    } catch (SQLException e) {
        System.out.println("Error en ProductoDAO.obtenerProductoPorId: " + e.getMessage());
    }

    return null;
}

public int insertarProducto(ProductoDTO p) {
    String sql = "INSERT INTO Productos " +
                 "(nombre, descripcion, id_categoria, precio_venta, existencia_actual, activo) " +
                 "VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = ConexionBD.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        ps.setString(1, p.getNombre());
        ps.setString(2, p.getDescripcion());
        ps.setInt(3, p.getIdCategoria());
        ps.setDouble(4, p.getPrecioVenta());
        ps.setInt(5, p.getExistenciaActual());
        ps.setInt(6, p.getActivo()); // columna activo es INT (0/1)

        ps.executeUpdate();

        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1); // id_producto generado
            }
        }

    } catch (SQLException e) {
        System.out.println("Error en ProductoDAO.insertarProducto: " + e.getMessage());
    }

    return -1;
}

public boolean actualizarProducto(ProductoDTO p) {
    String sql = "UPDATE Productos " +
                 "SET nombre = ?, descripcion = ?, id_categoria = ?, " +
                 "    precio_venta = ?, existencia_actual = ?, activo = ? " +
                 "WHERE id_producto = ?";

    try (Connection conn = ConexionBD.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, p.getNombre());
        ps.setString(2, p.getDescripcion());
        ps.setInt(3, p.getIdCategoria());
        ps.setDouble(4, p.getPrecioVenta());
        ps.setInt(5, p.getExistenciaActual());
        ps.setInt(6, p.getActivo());
        ps.setInt(7, p.getIdProducto());

        int filas = ps.executeUpdate();
        return filas > 0;

    } catch (SQLException e) {
        System.out.println("Error en ProductoDAO.actualizarProducto: " + e.getMessage());
        return false;
    }
}

public boolean cambiarEstadoProducto(int idProducto, int nuevoActivo) {
    String sql = "UPDATE Productos SET activo = ? WHERE id_producto = ?";

    try (Connection conn = ConexionBD.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, nuevoActivo); // 1 = activo, 0 = inactivo
        ps.setInt(2, idProducto);

        int filas = ps.executeUpdate();
        return filas > 0;

    } catch (SQLException e) {
        System.out.println("Error en ProductoDAO.cambiarEstadoProducto: " + e.getMessage());
        return false;
    }
}


    public List<Object[]> listarProductosParaTabla() {
    List<Object[]> lista = new ArrayList<>();

    String sql = "SELECT id_producto, nombre, precio_venta, existencia_actual FROM Productos ORDER BY nombre";

    try (Connection conn = ConexionBD.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Object[] fila = new Object[]{
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    rs.getDouble("precio_venta"),
                    rs.getInt("existencia_actual")
            };
            lista.add(fila);
        }

    } catch (SQLException e) {
        System.out.println("Error en listarProductosParaTabla(): " + e.getMessage());
    }

    return lista;
}

}
