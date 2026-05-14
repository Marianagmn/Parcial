package controladores;

import modelo.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Servlet para manejar el cierre de sesión.
 * URL: /logout
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        cerrarSesion(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        cerrarSesion(request, response);
    }
    
    private void cerrarSesion(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            Integer usuarioId = (Integer) session.getAttribute("usuarioId");
            
            // Registrar actividad de logout ANTES de invalidar la sesión
            if (usuarioId != null) {
                registrarActividad(usuarioId, "LOGOUT", "Usuario cerró sesión");
            }
            
            session.invalidate();
        }
        
        // Evitar que el navegador cachee la página protegida
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.sendRedirect("login.jsp");
    }
    
    private void registrarActividad(int usuarioId, String accion, String descripcion) {
        String sql = "INSERT INTO actividades (usuario_id, accion, descripcion) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setString(2, accion);
            stmt.setString(3, descripcion);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("[LogoutServlet] Error al registrar actividad: " + e.getMessage());
        }
    }
}
