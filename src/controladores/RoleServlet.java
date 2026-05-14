package controladores;

import modelo.Rol;
import modelo.Permiso;
import modelo.RoleDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Servlet para manejar la gestión de roles y permisos (solo para superadmin)
 */
@WebServlet("/roles")
public class RoleServlet extends HttpServlet {
    
    private RoleDAOImpl roleDAO;
    
    @Override
    public void init() throws ServletException {
        roleDAO = new RoleDAOImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        // Verificar que sea superadmin (rol ADMIN)
        String rol = (String) session.getAttribute("usuarioRol");
        if (!"ADMIN".equals(rol)) {
            response.sendRedirect("dashboard.jsp");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("editar".equals(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                Rol rolObj = roleDAO.obtenerPorId(Integer.parseInt(idStr));
                request.setAttribute("rol", rolObj);
                request.setAttribute("modoEdicion", true);
            }
            request.getRequestDispatcher("roles.jsp").forward(request, response);
        } else {
            // Listar todos los roles
            List<Rol> roles = roleDAO.obtenerTodos();
            request.setAttribute("roles", roles);
            request.getRequestDispatcher("roles.jsp").forward(request, response);
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
        
        // Verificar que sea superadmin
        String rol = (String) session.getAttribute("usuarioRol");
        if (!"ADMIN".equals(rol)) {
            response.sendRedirect("dashboard.jsp");
            return;
        }
        
        Integer usuarioIdActual = (Integer) session.getAttribute("usuarioId");
        String action = request.getParameter("action");
        
        if ("crear".equals(action)) {
            crearRol(request, response, usuarioIdActual);
        } else if ("actualizar".equals(action)) {
            actualizarRol(request, response, usuarioIdActual);
        } else if ("eliminar".equals(action)) {
            eliminarRol(request, response, usuarioIdActual);
        } else if ("agregarPermiso".equals(action)) {
            agregarPermiso(request, response, usuarioIdActual);
        } else if ("eliminarPermiso".equals(action)) {
            eliminarPermiso(request, response, usuarioIdActual);
        }
    }
    
    private void crearRol(HttpServletRequest request, HttpServletResponse response, int usuarioIdActual) 
            throws ServletException, IOException {
        
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");
        
        Rol nuevoRol = new Rol();
        nuevoRol.setNombre(nombre);
        nuevoRol.setDescripcion(descripcion);
        
        if (roleDAO.crear(nuevoRol)) {
            registrarActividad(usuarioIdActual, "CREAR_ROL", "Creó rol: " + nombre);
            response.sendRedirect("roles");
        } else {
            request.setAttribute("error", "Error al crear el rol");
            request.getRequestDispatcher("roles.jsp").forward(request, response);
        }
    }
    
    private void actualizarRol(HttpServletRequest request, HttpServletResponse response, int usuarioIdActual) 
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect("roles");
            return;
        }
        
        int id = Integer.parseInt(idStr);
        Rol rol = roleDAO.obtenerPorId(id);
        
        if (rol == null) {
            response.sendRedirect("roles");
            return;
        }
        
        rol.setNombre(request.getParameter("nombre"));
        rol.setDescripcion(request.getParameter("descripcion"));
        
        if (roleDAO.actualizar(rol)) {
            registrarActividad(usuarioIdActual, "ACTUALIZAR_ROL", "Actualizó rol: " + rol.getNombre());
            response.sendRedirect("roles");
        } else {
            request.setAttribute("error", "Error al actualizar el rol");
            request.setAttribute("rol", rol);
            request.setAttribute("modoEdicion", true);
            request.getRequestDispatcher("roles.jsp").forward(request, response);
        }
    }
    
    private void eliminarRol(HttpServletRequest request, HttpServletResponse response, int usuarioIdActual) 
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect("roles");
            return;
        }
        
        int id = Integer.parseInt(idStr);
        
        // No permitir eliminar roles básicos (ADMIN y USER)
        if (id == 1 || id == 2) {
            request.setAttribute("error", "No puedes eliminar los roles básicos del sistema");
            response.sendRedirect("roles");
            return;
        }
        
        Rol rol = roleDAO.obtenerPorId(id);
        if (rol != null) {
            String nombreRol = rol.getNombre();
            if (roleDAO.eliminar(id)) {
                registrarActividad(usuarioIdActual, "ELIMINAR_ROL", "Eliminó rol: " + nombreRol);
            }
        }
        
        response.sendRedirect("roles");
    }
    
    private void agregarPermiso(HttpServletRequest request, HttpServletResponse response, int usuarioIdActual) 
            throws ServletException, IOException {
        
        String rolIdStr = request.getParameter("rolId");
        String permisoIdStr = request.getParameter("permisoId");
        
        if (rolIdStr != null && permisoIdStr != null) {
            int rolId = Integer.parseInt(rolIdStr);
            int permisoId = Integer.parseInt(permisoIdStr);
            
            if (roleDAO.agregarPermiso(rolId, permisoId)) {
                Rol rol = roleDAO.obtenerPorId(rolId);
                registrarActividad(usuarioIdActual, "ASIGNAR_PERMISO", "Asignó permiso a rol: " + rol.getNombre());
            }
        }
        
        response.sendRedirect("roles");
    }
    
    private void eliminarPermiso(HttpServletRequest request, HttpServletResponse response, int usuarioIdActual) 
            throws ServletException, IOException {
        
        String rolIdStr = request.getParameter("rolId");
        String permisoIdStr = request.getParameter("permisoId");
        
        if (rolIdStr != null && permisoIdStr != null) {
            int rolId = Integer.parseInt(rolIdStr);
            int permisoId = Integer.parseInt(permisoIdStr);
            
            if (roleDAO.eliminarPermiso(rolId, permisoId)) {
                Rol rol = roleDAO.obtenerPorId(rolId);
                registrarActividad(usuarioIdActual, "ELIMINAR_PERMISO", "Eliminó permiso de rol: " + rol.getNombre());
            }
        }
        
        response.sendRedirect("roles");
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
