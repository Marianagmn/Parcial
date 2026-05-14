package modelo;

import interfaces.ContactDAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de ContactDAO para operaciones de base de datos
 */
public class ContactDAOImpl implements ContactDAO {
    
    @Override
    public Contacto obtenerPorId(int id) {
        String sql = "SELECT * FROM contactos WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapearContacto(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<Contacto> obtenerPorUsuario(int usuarioId) {
        List<Contacto> contactos = new ArrayList<>();
        String sql = "SELECT * FROM contactos WHERE usuario_id = ? ORDER BY fecha_creacion DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                contactos.add(mapearContacto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contactos;
    }
    
    @Override
    public List<Contacto> buscar(int usuarioId, String termino) {
        List<Contacto> contactos = new ArrayList<>();
        String sql = "SELECT * FROM contactos WHERE usuario_id = ? AND " +
                     "(nombre LIKE ? OR telefono LIKE ? OR email LIKE ? OR grupo LIKE ?) " +
                     "ORDER BY fecha_creacion DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            String terminoBusqueda = "%" + termino.toLowerCase() + "%";
            stmt.setString(2, terminoBusqueda);
            stmt.setString(3, terminoBusqueda);
            stmt.setString(4, terminoBusqueda);
            stmt.setString(5, terminoBusqueda);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                contactos.add(mapearContacto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contactos;
    }
    
    @Override
    public List<Contacto> obtenerPorGrupo(int usuarioId, String grupo) {
        List<Contacto> contactos = new ArrayList<>();
        String sql = "SELECT * FROM contactos WHERE usuario_id = ? AND grupo = ? ORDER BY fecha_creacion DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            stmt.setString(2, grupo);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                contactos.add(mapearContacto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contactos;
    }
    
    @Override
    public boolean crear(Contacto contacto) {
        String sql = "INSERT INTO contactos (usuario_id, nombre, telefono, email, grupo) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, contacto.getUsuarioId());
            stmt.setString(2, contacto.getNombre());
            stmt.setString(3, contacto.getTelefono());
            stmt.setString(4, contacto.getEmail());
            stmt.setString(5, contacto.getGrupo());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    contacto.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean actualizar(Contacto contacto) {
        String sql = "UPDATE contactos SET nombre = ?, telefono = ?, email = ?, grupo = ? WHERE id = ? AND usuario_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, contacto.getNombre());
            stmt.setString(2, contacto.getTelefono());
            stmt.setString(3, contacto.getEmail());
            stmt.setString(4, contacto.getGrupo());
            stmt.setInt(5, contacto.getId());
            stmt.setInt(6, contacto.getUsuarioId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM contactos WHERE id = ?";
        
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
    public int contarPorUsuario(int usuarioId) {
        String sql = "SELECT COUNT(*) FROM contactos WHERE usuario_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private Contacto mapearContacto(ResultSet rs) throws SQLException {
        Contacto contacto = new Contacto();
        contacto.setId(rs.getInt("id"));
        contacto.setUsuarioId(rs.getInt("usuario_id"));
        contacto.setNombre(rs.getString("nombre"));
        contacto.setTelefono(rs.getString("telefono"));
        contacto.setEmail(rs.getString("email"));
        contacto.setGrupo(rs.getString("grupo"));
        contacto.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        contacto.setFechaActualizacion(rs.getTimestamp("fecha_actualizacion"));
        return contacto;
    }
}
