// server/model/UsuarioDTO.java
package server.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class UsuarioDTO implements Serializable {

    private int idUsuario;
    private String nombre;
    private String usuario;
    private String passwordHash;   // columna password_hash en BD
    private String rol;            // ADMIN, CAJERO, INVITADO
    private int activo;            // 1 = activo, 0 = inactivo

    private Timestamp fechaCreacion;
    private Timestamp fechaBaja;
    private Integer idUsuarioBaja;

    public UsuarioDTO() {}

    // Constructor rápido (opcional)
    public UsuarioDTO(int idUsuario, String nombre, String usuario,
                      String rol, int activo) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.usuario = usuario;
        this.rol = rol;
        this.activo = activo;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }

    public Timestamp getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Timestamp getFechaBaja() { return fechaBaja; }
    public void setFechaBaja(Timestamp fechaBaja) { this.fechaBaja = fechaBaja; }

    public Integer getIdUsuarioBaja() { return idUsuarioBaja; }
    public void setIdUsuarioBaja(Integer idUsuarioBaja) { this.idUsuarioBaja = idUsuarioBaja; }
}
