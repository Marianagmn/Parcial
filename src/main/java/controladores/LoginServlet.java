package controladores;

import modelo.Usuario;
import modelo.UserDAOImpl;
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
 * Servlet para manejar el login y registro de usuarios.
 * URL: /login
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    
    private UserDAOImpl userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAOImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Verificar si ya hay una sesión activa
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuario") != null) {
            response.sendRedirect("contactos");
            return;
        }
        
        // Verificar si es modo registro
        String register = request.getParameter("register");
        request.setAttribute("modoRegistro", "true".equals(register));
        
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("registro".equals(action)) {
            procesarRegistro(request, response);
        } else {
            procesarLogin(request, response);
        }
    }
    
    private void procesarLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // Validación básica
        if (username == null || password == null 
                || username.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "Por favor completa todos los campos.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        
        Usuario usuario = userDAO.autenticar(username.trim(), password.trim());
        
        if (usuario != null) {
            // Invalidar sesión anterior para evitar fijación de sesión
            HttpSession sesionVieja = request.getSession(false);
            if (sesionVieja != null) {
                sesionVieja.invalidate();
            }
            
            HttpSession session = request.getSession(true);
            session.setAttribute("usuario", usuario);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("usuarioNombre", usuario.getNombre());
            session.setAttribute("usuarioRol", usuario.getRol() != null ? usuario.getRol().getNombre() : "USER");
            
            // Registrar actividad de login
            registrarActividad(usuario.getId(), "LOGIN", "Usuario inició sesión");
            
            response.sendRedirect("contactos");
        } else {
            request.setAttribute("error", "Credenciales incorrectas. Verifica tu usuario y contraseña.");
            request.setAttribute("username", username);
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
    
    private void procesarRegistro(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String nombre = request.getParameter("nombre");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        
        // Validación básica
        if (nombre == null || username == null || password == null
                || nombre.trim().isEmpty() || username.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "Por favor completa todos los campos obligatorios.");
            request.setAttribute("modoRegistro", true);
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        
        // Validar que el username no exista
        if (userDAO.existeUsername(username.trim())) {
            request.setAttribute("error", "El nombre de usuario ya existe");
            request.setAttribute("nombre", nombre);
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            request.setAttribute("modoRegistro", true);
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        
        // Crear nuevo usuario con rol USER por defecto
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre.trim());
        nuevoUsuario.setUsername(username.trim());
        nuevoUsuario.setPassword(password);
        nuevoUsuario.setEmail(email != null ? email.trim() : "");
        nuevoUsuario.setRolId(2); // Rol USER por defecto
        nuevoUsuario.setActivo(true);
        
        if (userDAO.crear(nuevoUsuario)) {
            // Invalidar sesión anterior
            HttpSession sesionVieja = request.getSession(false);
            if (sesionVieja != null) {
                sesionVieja.invalidate();
            }
            
            // Auto-login después del registro
            HttpSession session = request.getSession(true);
            session.setAttribute("usuario", nuevoUsuario);
            session.setAttribute("usuarioId", nuevoUsuario.getId());
            session.setAttribute("usuarioNombre", nuevoUsuario.getNombre());
            session.setAttribute("usuarioRol", "USER");
            
            // Registrar actividad de registro
            registrarActividad(nuevoUsuario.getId(), "REGISTRO", "Usuario se registró en el sistema");
            
            response.sendRedirect("contactos");
        } else {
            request.setAttribute("error", "Error al crear el usuario. Intenta de nuevo.");
            request.setAttribute("nombre", nombre);
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            request.setAttribute("modoRegistro", true);
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
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
            System.err.println("[LoginServlet] Error al registrar actividad: " + e.getMessage());
        }
    }
}
