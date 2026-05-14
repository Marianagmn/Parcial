package modelo;

import interfaces.UserDAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de UserDAO para operaciones de base de datos
 */
public class UserDAOImpl implements UserDAO {
    
    @Override
    public Usuario autenticar(String username, String password) {
        String sql = "SELECT u.*, r.nombre as rol_nombre, r.descripcion as rol_descripcion " +
                     "FROM usuarios u " +
                     "LEFT JOIN roles r ON u.rol_id = r.id " +
                     "WHERE u.username = ? AND u.password = ? AND u.activo = true";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Usuario usuario = mapearUsuario(rs);
                // Cargar el rol
                if (rs.getString("rol_nombre") != null) {
                    Rol rol = new Rol();
                    rol.setId(rs.getInt("rol_id"));
                    rol.setNombre(rs.getString("rol_nombre"));
                    rol.setDescripcion(rs.getString("rol_descripcion"));
                    usuario.setRol(rol);
                }
                return usuario;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public Usuario obtenerPorId(int id) {
        String sql = "SELECT u.*, r.nombre as rol_nombre, r.descripcion as rol_descripcion " +
                     "FROM usuarios u " +
                     "LEFT JOIN roles r ON u.rol_id = r.id " +
                     "WHERE u.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Usuario usuario = mapearUsuario(rs);
                if (rs.getString("rol_nombre") != null) {
                    Rol rol = new Rol();
                    rol.setId(rs.getInt("rol_id"));
                    rol.setNombre(rs.getString("rol_nombre"));
                    rol.setDescripcion(rs.getString("rol_descripcion"));
                    usuario.setRol(rol);
                }
                return usuario;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public Usuario obtenerPorUsername(String username) {
        String sql = "SELECT u.*, r.nombre as rol_nombre, r.descripcion as rol_descripcion " +
                     "FROM usuarios u " +
                     "LEFT JOIN roles r ON u.rol_id = r.id " +
                     "WHERE u.username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Usuario usuario = mapearUsuario(rs);
                if (rs.getString("rol_nombre") != null) {
                    Rol rol = new Rol();
                    rol.setId(rs.getInt("rol_id"));
                    rol.setNombre(rs.getString("rol_nombre"));
                    rol.setDescripcion(rs.getString("rol_descripcion"));
                    usuario.setRol(rol);
                }
                return usuario;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT u.*, r.nombre as rol_nombre, r.descripcion as rol_descripcion " +
                     "FROM usuarios u " +
                     "LEFT JOIN roles r ON u.rol_id = r.id " +
                     "ORDER BY u.fecha_creacion DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Usuario usuario = mapearUsuario(rs);
                if (rs.getString("rol_nombre") != null) {
                    Rol rol = new Rol();
                    rol.setId(rs.getInt("rol_id"));
                    rol.setNombre(rs.getString("rol_nombre"));
                    rol.setDescripcion(rs.getString("rol_descripcion"));
                    usuario.setRol(rol);
                }
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }
    
    @Override
    public boolean crear(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, username, password, email, rol_id) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getUsername());
            stmt.setString(3, usuario.getPassword());
            stmt.setString(4, usuario.getEmail());
            stmt.setInt(5, usuario.getRolId());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre = ?, username = ?, password = ?, email = ?, rol_id = ?, activo = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getUsername());
            stmt.setString(3, usuario.getPassword());
            stmt.setString(4, usuario.getEmail());
            stmt.setInt(5, usuario.getRolId());
            stmt.setBoolean(6, usuario.isActivo());
            stmt.setInt(7, usuario.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean existeUsername(String username) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean asignarRol(int usuarioId, int rolId) {
        String sql = "UPDATE usuarios SET rol_id = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, rolId);
            stmt.setInt(2, usuarioId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setUsername(rs.getString("username"));
        usuario.setPassword(rs.getString("password"));
        usuario.setEmail(rs.getString("email"));
        usuario.setRolId(rs.getInt("rol_id"));
        usuario.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        usuario.setActivo(rs.getBoolean("activo"));
        return usuario;
    }
}
