package interfaces;

import modelo.Usuario;
import java.util.List;

/**
 * Interfaz para operaciones CRUD de Usuario
 */
public interface UserDAO {
    
    /**
     * Autentica un usuario por username y password
     * @param username nombre de usuario
     * @param password contraseña
     * @return Usuario si las credenciales son correctas, null en caso contrario
     */
    Usuario autenticar(String username, String password);
    
    /**
     * Obtiene un usuario por su ID
     * @param id ID del usuario
     * @return Usuario encontrado
     */
    Usuario obtenerPorId(int id);
    
    /**
     * Obtiene un usuario por su username
     * @param username nombre de usuario
     * @return Usuario encontrado
     */
    Usuario obtenerPorUsername(String username);
    
    /**
     * Obtiene todos los usuarios
     * @return Lista de todos los usuarios
     */
    List<Usuario> obtenerTodos();
    
    /**
     * Crea un nuevo usuario
     * @param usuario Usuario a crear
     * @return true si se creó exitosamente
     */
    boolean crear(Usuario usuario);
    
    /**
     * Actualiza un usuario existente
     * @param usuario Usuario a actualizar
     * @return true si se actualizó exitosamente
     */
    boolean actualizar(Usuario usuario);
    
    /**
     * Elimina un usuario por su ID
     * @param id ID del usuario a eliminar
     * @return true si se eliminó exitosamente
     */
    boolean eliminar(int id);
    
    /**
     * Verifica si un username ya existe
     * @param username nombre de usuario a verificar
     * @return true si el username ya existe
     */
    boolean existeUsername(String username);
    
    /**
     * Asigna un rol a un usuario
     * @param usuarioId ID del usuario
     * @param rolId ID del rol
     * @return true si se asignó exitosamente
     */
    boolean asignarRol(int usuarioId, int rolId);
}
