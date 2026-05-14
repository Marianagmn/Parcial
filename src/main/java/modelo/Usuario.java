package modelo;

import java.sql.Timestamp;

/**
 * Clase modelo para Usuario
 */
public class Usuario {
    private int id;
    private String nombre;
    private String username;
    private String password;
    private String email;
    private int rolId;
    private Timestamp fechaCreacion;
    private boolean activo;
    private Rol rol;
    
    public Usuario() {
    }
    
    public Usuario(int id, String nombre, String username, String password, String email, int rolId) {
        this.id = id;
        this.nombre = nombre;
        this.username = username;
        this.password = password;
        this.email = email;
        this.rolId = rolId;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public int getRolId() {
        return rolId;
    }
    
    public void setRolId(int rolId) {
        this.rolId = rolId;
    }
    
    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    public Rol getRol() {
        return rol;
    }
    
    public void setRol(Rol rol) {
        this.rol = rol;
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", rolId=" + rolId +
                '}';
    }
}
