package server.model;

import java.io.Serializable;

public class ProductoDTO implements Serializable {

    private int idProducto;
    private String nombre;
    private String categoria;      // nombre de la categoría
    private double precioVenta;
    private int existenciaActual;

    // campos extra que usamos en admin
    private int idCategoria;
    private String descripcion;
    private int activo;            // 1 = ACTIVO, 0 = INACTIVO

    public ProductoDTO() {}

    // Puedes tener también un constructor largo si quieres
    public ProductoDTO(int idProducto,
                       String nombre,
                       String categoria,
                       double precioVenta,
                       int existenciaActual,
                       int idCategoria,
                       String descripcion,
                       int activo) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precioVenta = precioVenta;
        this.existenciaActual = existenciaActual;
        this.idCategoria = idCategoria;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    // Getters y setters
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }

    public int getExistenciaActual() { return existenciaActual; }
    public void setExistenciaActual(int existenciaActual) { this.existenciaActual = existenciaActual; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }
}
