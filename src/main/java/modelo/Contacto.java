package modelo;

import java.sql.Timestamp;

/**
 * Clase modelo para Contacto
 */
public class Contacto {
    private int id;
    private int usuarioId;
    private String nombre;
    private String telefono;
    private String email;
    private String grupo;
    private Timestamp fechaCreacion;
    private Timestamp fechaActualizacion;
    
    public Contacto() {
    }
    
    public Contacto(int id, int usuarioId, String nombre, String telefono, String email, String grupo) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.grupo = grupo;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getGrupo() {
        return grupo;
    }
    
    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }
    
    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public Timestamp getFechaActualizacion() {
        return fechaActualizacion;
    }
    
    public void setFechaActualizacion(Timestamp fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
    
    @Override
    public String toString() {
        return "Contacto{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", nombre='" + nombre + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", grupo='" + grupo + '\'' +
                '}';
    }
}
