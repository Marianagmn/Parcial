package interfaces;

import modelo.Contacto;
import java.util.List;

/**
 * Interfaz para operaciones CRUD de Contacto
 */
public interface ContactDAO {
    
    /**
     * Obtiene un contacto por su ID
     * @param id ID del contacto
     * @return Contacto encontrado
     */
    Contacto obtenerPorId(int id);
    
    /**
     * Obtiene todos los contactos de un usuario
     * @param usuarioId ID del usuario
     * @return Lista de contactos del usuario
     */
    List<Contacto> obtenerPorUsuario(int usuarioId);
    
    /**
     * Busca contactos por nombre, teléfono, email o grupo
     * @param usuarioId ID del usuario
     * @param termino término de búsqueda
     * @return Lista de contactos que coinciden con la búsqueda
     */
    List<Contacto> buscar(int usuarioId, String termino);
    
    /**
     * Obtiene contactos por grupo
     * @param usuarioId ID del usuario
     * @param grupo nombre del grupo
     * @return Lista de contactos del grupo
     */
    List<Contacto> obtenerPorGrupo(int usuarioId, String grupo);
    
    /**
     * Crea un nuevo contacto
     * @param contacto Contacto a crear
     * @return true si se creó exitosamente
     */
    boolean crear(Contacto contacto);
    
    /**
     * Actualiza un contacto existente
     * @param contacto Contacto a actualizar
     * @return true si se actualizó exitosamente
     */
    boolean actualizar(Contacto contacto);
    
    /**
     * Elimina un contacto por su ID
     * @param id ID del contacto a eliminar
     * @return true si se eliminó exitosamente
     */
    boolean eliminar(int id);
    
    /**
     * Cuenta el total de contactos de un usuario
     * @param usuarioId ID del usuario
     * @return número total de contactos
     */
    int contarPorUsuario(int usuarioId);
}
