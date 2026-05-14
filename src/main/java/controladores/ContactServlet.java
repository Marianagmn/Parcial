package controladores;

import modelo.Contacto;
import modelo.ContactDAOImpl;
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
import java.util.List;

/**
 * Servlet para manejar operaciones CRUD de contactos.
 * URL: /contactos
 */
@WebServlet("/contactos")
public class ContactServlet extends HttpServlet {
    
    private ContactDAOImpl contactDAO;
    
    @Override
    public void init() throws ServletException {
        contactDAO = new ContactDAOImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");
        String action = request.getParameter("action");
        
        if ("buscar".equals(action)) {
            String termino = request.getParameter("termino");
            List<Contacto> contactos = contactDAO.buscar(usuarioId, termino);
            request.setAttribute("contactos", contactos);
            request.setAttribute("terminoBusqueda", termino);
            // Activar vista de búsqueda
            request.setAttribute("vistaActiva", "buscar");
            request.getRequestDispatcher("dashboard.jsp").forward(request, response);

        } else if ("editar".equals(action)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                Contacto contacto = contactDAO.obtenerPorId(Integer.parseInt(idStr));
                if (contacto != null && contacto.getUsuarioId() == usuarioId) {
                    request.setAttribute("contacto", contacto);
                    request.setAttribute("modoEdicion", true);
                }
            }
            // Activar vista de formulario
            request.setAttribute("vistaActiva", "editar");
            request.getRequestDispatcher("dashboard.jsp").forward(request, response);

        } else if ("eliminar".equals(action)) {
            // Eliminar contacto vía GET (enlace del dashboard)
            String idStr = request.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                Contacto contacto = contactDAO.obtenerPorId(id);
                if (contacto != null && contacto.getUsuarioId() == usuarioId) {
                    String nombreContacto = contacto.getNombre();
                    if (contactDAO.eliminar(id)) {
                        registrarActividad(usuarioId, "ELIMINAR_CONTACTO",
                                "Eliminó contacto: " + nombreContacto);
                    }
                }
            }
            response.sendRedirect("contactos");

        } else {
            // Listar todos los contactos
            List<Contacto> contactos = contactDAO.obtenerPorUsuario(usuarioId);
            request.setAttribute("contactos", contactos);
            request.getRequestDispatcher("dashboard.jsp").forward(request, response);
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
        
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");
        String action = request.getParameter("action");
        
        if ("crear".equals(action)) {
            crearContacto(request, response, usuarioId);
        } else if ("actualizar".equals(action)) {
            actualizarContacto(request, response, usuarioId);
        }
    }
    
    private void crearContacto(HttpServletRequest request, HttpServletResponse response, int usuarioId) 
            throws ServletException, IOException {
        
        Contacto contacto = new Contacto();
        contacto.setUsuarioId(usuarioId);
        contacto.setNombre(request.getParameter("nombre"));
        contacto.setTelefono(request.getParameter("telefono"));
        contacto.setEmail(request.getParameter("email"));
        contacto.setGrupo(request.getParameter("grupo"));
        
        if (contactDAO.crear(contacto)) {
            registrarActividad(usuarioId, "CREAR_CONTACTO", "Creó contacto: " + contacto.getNombre());
            response.sendRedirect("contactos");
        } else {
            request.setAttribute("error", "Error al crear el contacto");
            request.getRequestDispatcher("dashboard.jsp").forward(request, response);
        }
    }
    
    private void actualizarContacto(HttpServletRequest request, HttpServletResponse response, int usuarioId) 
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect("contactos");
            return;
        }
        
        int id = Integer.parseInt(idStr);
        Contacto contacto = contactDAO.obtenerPorId(id);
        
        if (contacto == null || contacto.getUsuarioId() != usuarioId) {
            response.sendRedirect("contactos");
            return;
        }
        
        contacto.setNombre(request.getParameter("nombre"));
        contacto.setTelefono(request.getParameter("telefono"));
        contacto.setEmail(request.getParameter("email"));
        contacto.setGrupo(request.getParameter("grupo"));
        
        if (contactDAO.actualizar(contacto)) {
            registrarActividad(usuarioId, "ACTUALIZAR_CONTACTO", "Actualizó contacto: " + contacto.getNombre());
            response.sendRedirect("contactos");
        } else {
            request.setAttribute("error", "Error al actualizar el contacto");
            request.setAttribute("contacto", contacto);
            request.setAttribute("modoEdicion", true);
            request.getRequestDispatcher("dashboard.jsp").forward(request, response);
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
            System.err.println("[ContactServlet] Error al registrar actividad: " + e.getMessage());
        }
    }
}
