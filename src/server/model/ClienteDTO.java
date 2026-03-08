// server/model/ClienteDTO.java
package server.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ClienteDTO implements Serializable {

    private int idCliente;
    private String nombre;
    private String direccion;
    private String telefono;
    private String correo;
    private String tipoCliente; // PUBLICO_GENERAL / CONTRATISTA / MAYORISTA
    private int activo;         // 1 = ACTIVO, 0 = INACTIVO

    private Timestamp fechaCreacion;
    private Timestamp fechaBaja;
    private Integer idUsuarioBaja;

    public ClienteDTO() {}

    public ClienteDTO(int idCliente, String nombre, String direccion,
                      String telefono, String correo, String tipoCliente,
                      int activo, Timestamp fechaCreacion,
                      Timestamp fechaBaja, Integer idUsuarioBaja) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correo = correo;
        this.tipoCliente = tipoCliente;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.fechaBaja = fechaBaja;
        this.idUsuarioBaja = idUsuarioBaja;
    }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTipoCliente() { return tipoCliente; }
    public void setTipoCliente(String tipoCliente) { this.tipoCliente = tipoCliente; }

    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }

    public Timestamp getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Timestamp getFechaBaja() { return fechaBaja; }
    public void setFechaBaja(Timestamp fechaBaja) { this.fechaBaja = fechaBaja; }

    public Integer getIdUsuarioBaja() { return idUsuarioBaja; }
    public void setIdUsuarioBaja(Integer idUsuarioBaja) { this.idUsuarioBaja = idUsuarioBaja; }
}
