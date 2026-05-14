package controladores;

import modelo.Usuario;
import modelo.UserDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet para manejar el login y registro de usuarios
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
            response.sendRedirect("dashboard.jsp");
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
        
        Usuario usuario = userDAO.autenticar(username, password);
        
        if (usuario != null) {
            HttpSession session = request.getSession();
            session.setAttribute("usuario", usuario);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("usuarioNombre", usuario.getNombre());
            session.setAttribute("usuarioRol", usuario.getRol() != null ? usuario.getRol().getNombre() : "USER");
            
            // Registrar actividad de login
            registrarActividad(usuario.getId(), "LOGIN", "Usuario inició sesión");
            
            response.sendRedirect("dashboard.jsp");
        } else {
            request.setAttribute("error", "Credenciales incorrectas");
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
        
        // Validar que el username no exista
        if (userDAO.existeUsername(username)) {
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
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setUsername(username);
        nuevoUsuario.setPassword(password);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setRolId(2); // Rol USER por defecto
        nuevoUsuario.setActivo(true);
        
        if (userDAO.crear(nuevoUsuario)) {
            // Auto-login después del registro
            HttpSession session = request.getSession();
            session.setAttribute("usuario", nuevoUsuario);
            session.setAttribute("usuarioId", nuevoUsuario.getId());
            session.setAttribute("usuarioNombre", nuevoUsuario.getNombre());
            session.setAttribute("usuarioRol", "USER");
            
            // Registrar actividad de registro
            registrarActividad(nuevoUsuario.getId(), "REGISTRO", "Usuario se registró en el sistema");
            
            response.sendRedirect("dashboard.jsp");
        } else {
            request.setAttribute("error", "Error al crear el usuario");
            request.setAttribute("nombre", nombre);
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            request.setAttribute("modoRegistro", true);
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
    
    private void registrarActividad(int usuarioId, String accion, String descripcion) {
        try {
            java.sql.Connection conn = modelo.DatabaseConnection.getConnection();
            String sql = "INSERT INTO actividades (usuario_id, accion, descripcion) VALUES (?, ?, ?)";
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, usuarioId);
            stmt.setString(2, accion);
            stmt.setString(3, descripcion);
            stmt.executeUpdate();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
