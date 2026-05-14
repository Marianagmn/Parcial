package modelo;

import interfaces.RoleDAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de RoleDAO para gestión de roles y permisos
 */
public class RoleDAOImpl implements RoleDAO {
    
    @Override
    public Rol obtenerPorId(int id) {
        String sql = "SELECT * FROM roles WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Rol rol = new Rol();
                rol.setId(rs.getInt("id"));
                rol.setNombre(rs.getString("nombre"));
                rol.setDescripcion(rs.getString("descripcion"));
                rol.setPermisos(obtenerPermisosPorRol(id));
                return rol;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Rol> obtenerTodos() {
        List<Rol> roles = new ArrayList<>();
        String sql = "SELECT * FROM roles ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Rol rol = new Rol();
                rol.setId(rs.getInt("id"));
                rol.setNombre(rs.getString("nombre"));
                rol.setDescripcion(rs.getString("descripcion"));
                rol.setPermisos(obtenerPermisosPorRol(rol.getId()));
                roles.add(rol);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }
    
    @Override
    public boolean crear(Rol rol) {
        String sql = "INSERT INTO roles (nombre, descripcion) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, rol.getNombre());
            stmt.setString(2, rol.getDescripcion());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    rol.setId(rs.getInt(1));
                    // Agregar permisos si se proporcionaron
                    if (rol.getPermisos() != null) {
                        for (Permiso permiso : rol.getPermisos()) {
                            agregarPermiso(rol.getId(), permiso.getId());
                        }
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean actualizar(Rol rol) {
        String sql = "UPDATE roles SET nombre = ?, descripcion = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rol.getNombre());
            stmt.setString(2, rol.getDescripcion());
            stmt.setInt(3, rol.getId());
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean eliminar(int id) {
        // Primero eliminar los permisos asociados
        String deletePermisos = "DELETE FROM rol_permiso WHERE rol_id = ?";
        String deleteRol = "DELETE FROM roles WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt1 = conn.prepareStatement(deletePermisos);
                 PreparedStatement stmt2 = conn.prepareStatement(deleteRol)) {
                stmt1.setInt(1, id);
                stmt1.executeUpdate();
                
                stmt2.setInt(1, id);
                int affectedRows = stmt2.executeUpdate();
                
                conn.commit();
                return affectedRows > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean agregarPermiso(int rolId, int permisoId) {
        String sql = "INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rolId);
            stmt.setInt(2, permisoId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean eliminarPermiso(int rolId, int permisoId) {
        String sql = "DELETE FROM rol_permiso WHERE rol_id = ? AND permiso_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rolId);
            stmt.setInt(2, permisoId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public List<Permiso> obtenerPermisosPorRol(int rolId) {
        List<Permiso> permisos = new ArrayList<>();
        String sql = "SELECT p.* FROM permisos p " +
                     "INNER JOIN rol_permiso rp ON p.id = rp.permiso_id " +
                     "WHERE rp.rol_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rolId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Permiso permiso = new Permiso();
                permiso.setId(rs.getInt("id"));
                permiso.setNombre(rs.getString("nombre"));
                permiso.setDescripcion(rs.getString("descripcion"));
                permisos.add(permiso);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return permisos;
    }
    
    @Override
    public boolean asignarRolAUsuario(int usuarioId, int rolId) {
        String sql = "UPDATE usuarios SET rol_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rolId);
            stmt.setInt(2, usuarioId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public List<Rol> obtenerRolesDisponibles() {
        return obtenerTodos();
    }
}
