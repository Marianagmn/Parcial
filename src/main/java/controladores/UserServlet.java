package controladores;

import modelo.Usuario;
import modelo.UserDAOImpl;
import modelo.DatabaseConnection;
import modelo.Rol;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * Servlet para manejar la gestión de usuarios (solo para administradores).
 * URL: /usuarios
 */
@WebServlet("/usuarios")
public class UserServlet extends HttpServlet {
    
    private UserDAOImpl userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAOImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        // Verificar que sea administrador
        String rol = (String) session.getAttribute("usuarioRol");
        if (!"ADMIN".equals(rol)) {
            response.sendRedirect("contactos");
            return;
        }
        
        Integer usuarioIdActual = (Integer) session.getAttribute("usuarioId");
        String action = request.getParameter("action");
        
        if ("editar".equals(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                Usuario usuario = userDAO.obtenerPorId(Integer.parseInt(idStr));
                request.setAttribute("usuario", usuario);
                request.setAttribute("modoEdicion", true);
            }
            List<Usuario> usuarios = userDAO.obtenerTodos();
            request.setAttribute("usuarios", usuarios);
            request.getRequestDispatcher("usuarios.jsp").forward(request, response);

        } else if ("eliminar".equals(action)) {
            // Eliminar usuario vía GET (enlace de la tabla)
            String idStr = request.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                // No permitir eliminar el propio usuario
                if (id != usuarioIdActual) {
                    Usuario usuario = userDAO.obtenerPorId(id);
                    if (usuario != null) {
                        String username = usuario.getUsername();
                        if (userDAO.eliminar(id)) {
                            registrarActividad(usuarioIdActual, "ELIMINAR_USUARIO",
                                    "Eliminó usuario: " + username);
                        }
                    }
                }
            }
            response.sendRedirect("usuarios");

        } else if ("asignarRol".equals(action)) {
            // Asignar rol vía GET (enlace de la tabla)
            String idStr = request.getParameter("id");
            String rolIdStr = request.getParameter("rolId");
            if (idStr != null && rolIdStr != null) {
                int usuarioId = Integer.parseInt(idStr);
                int rolId = Integer.parseInt(rolIdStr);
                if (userDAO.asignarRol(usuarioId, rolId)) {
                    Usuario usuario = userDAO.obtenerPorId(usuarioId);
                    registrarActividad(usuarioIdActual, "ASIGNAR_ROL",
                            "Asignó rol a usuario: " + (usuario != null ? usuario.getUsername() : "ID " + usuarioId));
                }
            }
            response.sendRedirect("usuarios");

        } else {
            // Listar todos los usuarios
            List<Usuario> usuarios = userDAO.obtenerTodos();
            request.setAttribute("usuarios", usuarios);
            request.getRequestDispatcher("usuarios.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        // Verificar que sea administrador
        String rol = (String) session.getAttribute("usuarioRol");
        if (!"ADMIN".equals(rol)) {
            response.sendRedirect("contactos");
            return;
        }
        
        Integer usuarioIdActual = (Integer) session.getAttribute("usuarioId");
        String action = request.getParameter("action");
        
        if ("crear".equals(action)) {
            crearUsuario(request, response, usuarioIdActual);
        } else if ("actualizar".equals(action)) {
            actualizarUsuario(request, response, usuarioIdActual);
        }
    }
    
    private void crearUsuario(HttpServletRequest request, HttpServletResponse response, int usuarioIdActual) 
            throws ServletException, IOException {
        
        String nombre = request.getParameter("nombre");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        int rolId = Integer.parseInt(request.getParameter("rolId"));
        
        // Validar que el username no exista
        if (userDAO.existeUsername(username)) {
            request.setAttribute("error", "El nombre de usuario ya existe");
            List<Usuario> usuarios = userDAO.obtenerTodos();
            request.setAttribute("usuarios", usuarios);
            request.getRequestDispatcher("usuarios.jsp").forward(request, response);
            return;
        }
        
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setUsername(username);
        nuevoUsuario.setPassword(password);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setRolId(rolId);
        nuevoUsuario.setActivo(true);
        
        if (userDAO.crear(nuevoUsuario)) {
            registrarActividad(usuarioIdActual, "CREAR_USUARIO", "Creó usuario: " + username);
            response.sendRedirect("usuarios");
        } else {
            request.setAttribute("error", "Error al crear el usuario");
            List<Usuario> usuarios = userDAO.obtenerTodos();
            request.setAttribute("usuarios", usuarios);
            request.getRequestDispatcher("usuarios.jsp").forward(request, response);
        }
    }
    
    private void actualizarUsuario(HttpServletRequest request, HttpServletResponse response, int usuarioIdActual) 
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect("usuarios");
            return;
        }
        
        int id = Integer.parseInt(idStr);
        Usuario usuario = userDAO.obtenerPorId(id);
        
        if (usuario == null) {
            response.sendRedirect("usuarios");
            return;
        }
        
        usuario.setNombre(request.getParameter("nombre"));
        usuario.setEmail(request.getParameter("email"));
        usuario.setActivo(Boolean.parseBoolean(request.getParameter("activo")));
        
        if (userDAO.actualizar(usuario)) {
            registrarActividad(usuarioIdActual, "ACTUALIZAR_USUARIO", "Actualizó usuario: " + usuario.getUsername());
            response.sendRedirect("usuarios");
        } else {
            request.setAttribute("error", "Error al actualizar el usuario");
            request.setAttribute("usuario", usuario);
            request.setAttribute("modoEdicion", true);
            List<Usuario> usuarios = userDAO.obtenerTodos();
            request.setAttribute("usuarios", usuarios);
            request.getRequestDispatcher("usuarios.jsp").forward(request, response);
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
            System.err.println("[UserServlet] Error al registrar actividad: " + e.getMessage());
        }
    }
}
