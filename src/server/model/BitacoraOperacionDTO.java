// server/model/BitacoraOperacionDTO.java
package server.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class BitacoraOperacionDTO implements Serializable {

    private int idEvento;
    private Timestamp fecha;
    private int idUsuario;
    private String nombreUsuario;     // lo obtendremos con JOIN a usuarios
    private String tablaAfectada;     // 'Ventas', 'Productos', etc.
    private int idRegistro;           // ej. id_venta
    private String operacion;         // 'ALTA', 'MODIFICACION', 'BAJA_LOGICA', 'CANCELACION'
    private String descripcion;

    public int getIdEvento() { return idEvento; }
    public void setIdEvento(int idEvento) { this.idEvento = idEvento; }

    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getTablaAfectada() { return tablaAfectada; }
    public void setTablaAfectada(String tablaAfectada) { this.tablaAfectada = tablaAfectada; }

    public int getIdRegistro() { return idRegistro; }
    public void setIdRegistro(int idRegistro) { this.idRegistro = idRegistro; }

    public String getOperacion() { return operacion; }
    public void setOperacion(String operacion) { this.operacion = operacion; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
