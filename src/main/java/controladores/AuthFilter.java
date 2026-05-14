package controladores;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filtro para verificar la autenticación del usuario
 */
public class AuthFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialización del filtro
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        HttpSession session = httpRequest.getSession(false);
        
        // Verificar si el usuario está autenticado
        if (session == null || session.getAttribute("usuarioId") == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
            return;
        }
        
        // Si está autenticado, continuar con la cadena de filtros
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        // Limpieza del filtro
    }
}
