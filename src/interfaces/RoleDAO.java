package interfaces;

import modelo.Rol;
import modelo.Permiso;
import java.util.List;

/**
 * Interfaz para operaciones CRUD de roles
 */
public interface RoleDAO {
    
    // Operaciones básicas de Rol
    Rol obtenerPorId(int id);
    List<Rol> obtenerTodos();
    boolean crear(Rol rol);
    boolean actualizar(Rol rol);
    boolean eliminar(int id);
    
    // Operaciones de permisos
    boolean agregarPermiso(int rolId, int permisoId);
    boolean eliminarPermiso(int rolId, int permisoId);
    List<Permiso> obtenerPermisosPorRol(int rolId);
    
    // Operaciones de asignación
    boolean asignarRolAUsuario(int usuarioId, int rolId);
    List<Rol> obtenerRolesDisponibles();
}
