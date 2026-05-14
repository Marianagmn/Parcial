package modelo;

import java.util.List;

/**
 * Clase modelo para Rol
 */
public class Rol {
    private int id;
    private String nombre;
    private String descripcion;
    private List<Permiso> permisos;
    
    public Rol() {
    }
    
    public Rol(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
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
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public List<Permiso> getPermisos() {
        return permisos;
    }
    
    public void setPermisos(List<Permiso> permisos) {
        this.permisos = permisos;
    }
    
    /**
     * Verifica si el rol tiene un permiso específico
     * @param permisoId ID del permiso a verificar
     * @return true si el rol tiene el permiso, false en caso contrario
     */
    public boolean tienePermiso(int permisoId) {
        if (permisos == null) {
            return false;
        }
        for (Permiso permiso : permisos) {
            if (permiso.getId() == permisoId) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "Rol{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
