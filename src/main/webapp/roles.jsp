<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="modelo.Rol" %>
<%@ page import="modelo.Permiso" %>
<%
    // Verificar sesión activa y rol ADMIN (superadmin)
    if (session.getAttribute("usuarioId") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    String usuarioRol = (String) session.getAttribute("usuarioRol");
    if (!"ADMIN".equals(usuarioRol)) {
        response.sendRedirect("dashboard.jsp");
        return;
    }
    
    String usuarioNombre = (String) session.getAttribute("usuarioNombre");
    Integer usuarioIdActual = (Integer) session.getAttribute("usuarioId");
    String error = (String) request.getAttribute("error");
    
    // Obtener roles y permisos
    List<Rol> roles = (List<Rol>) request.getAttribute("roles");
    Rol rolEdicion = (Rol) request.getAttribute("rol");
    boolean modoEdicion = (request.getAttribute("modoEdicion") != null);
%>
<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Roles - Agenda de Contactos</title>
    <link rel="stylesheet" href="styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        /* Role card styles */
        .role-card {
            background: white;
            border: 1px solid #e5e7eb;
            border-radius: 0.5rem;
            padding: 1.5rem;
            margin-bottom: 1rem;
            transition: all 0.2s ease;
        }
        .role-card:hover {
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            border-color: #d1d5db;
        }
        .role-card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1rem;
        }
        .role-card-title {
            font-size: 1.125rem;
            font-weight: 600;
            color: #1f2937;
        }
        .role-card-description {
            color: #6b7280;
            font-size: 0.875rem;
            margin-bottom: 1rem;
        }
        .permisos-list {
            display: flex;
            flex-wrap: wrap;
            gap: 0.5rem;
            margin-top: 0.75rem;
        }
        .permiso-badge {
            background: #f3f4f6;
            color: #374151;
            padding: 0.25rem 0.75rem;
            border-radius: 999px;
            font-size: 0.75rem;
            font-weight: 500;
        }
        .role-actions {
            display: flex;
            gap: 0.5rem;
            margin-top: 1rem;
        }
        .btn-sm {
            padding: 0.375rem 0.75rem;
            font-size: 0.875rem;
        }
        
        /* Permission selection */
        .permiso-checkbox-wrapper {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            padding: 0.5rem;
            border: 1px solid #e5e7eb;
            border-radius: 0.375rem;
            cursor: pointer;
            transition: all 0.2s ease;
        }
        .permiso-checkbox-wrapper:hover {
            background: #f9fafb;
        }
        .permiso-checkbox-wrapper input[type="checkbox"] {
            width: 1rem;
            height: 1rem;
            cursor: pointer;
        }
        .permisos-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 0.75rem;
            margin-top: 0.5rem;
        }
        
        /* Role badge colors */
        .role-badge {
            display: inline-flex;
            align-items: center;
            gap: 0.35rem;
            padding: 0.25rem 0.75rem;
            border-radius: 999px;
            font-size: 0.75rem;
            font-weight: 600;
            text-transform: uppercase;
        }
        .role-badge--admin {
            background: linear-gradient(135deg, #2563eb, #3b82f6);
            color: #fff;
        }
        .role-badge--user {
            background: linear-gradient(135deg, #059669, #10b981);
            color: #fff;
        }
        .role-badge--custom {
            background: linear-gradient(135deg, #7c3aed, #a855f7);
            color: #fff;
        }
    </style>
</head>

<body class="dashboard-body">

    <!-- Sidebar -->
    <aside class="sidebar" id="sidebar">
        <div class="sidebar-header">
            <span class="icon">📖</span>
            <h2>ConfigContacts</h2>
        </div>
        <nav class="sidebar-nav">
            <a href="contactos" class="nav-item" title="Ver todos tus contactos guardados">📋 Lista de Contactos</a>
            <a href="contactos?action=editar" class="nav-item" title="Añadir un nuevo contacto a la agenda">➕ Añadir Contacto</a>
            <a href="contactos?action=buscar" class="nav-item" title="Buscar contactos por nombre o grupo">🔍 Buscar</a>
            <a href="usuarios" class="nav-item" title="Gestionar usuarios del sistema">👥 Gestionar Usuarios</a>
            <a href="roles" class="nav-item active" title="Gestionar roles y permisos del sistema">🔐 Gestionar Roles</a>
        </nav>
        <div class="sidebar-footer">
            <a href="logout" class="btn-logout" title="Cerrar la sesión actual de forma segura">
                🚪 Cerrar Sesión
            </a>
        </div>
    </aside>

    <!-- Main Content Area -->
    <main class="main-content">
        <header class="top-header shadow-sm">
            <button class="menu-toggle" id="menu-toggle" title="Abrir y cerrar el menú principal">☰ Menú</button>
            <div class="user-info">
                <span>Hola, <strong id="user-display-name"><%=usuarioNombre%></strong></span>
                <div class="avatar shadow-sm" title="Tu foto de perfil"><%=usuarioNombre != null ? String.valueOf(usuarioNombre.charAt(0)).toUpperCase() : "U"%></div>
            </div>
        </header>

        <section class="content-area">

            <div class="section-header">
                <h2>Gestión de Roles y Permisos</h2>
                <p class="text-secondary">Crea y administra roles del sistema con sus respectivos permisos.</p>
            </div>

            <% if (error != null && !error.isEmpty()) { %>
            <div class="alert debug-alert" style="display:block; color: #dc2626; font-size: 0.875rem; margin-bottom: 1rem; padding: 0.75rem; background: #fef2f2; border-radius: 0.375rem;">
                <%=error%>
            </div>
            <% } %>

            <!-- Create/Edit Role Form -->
            <div class="card shadow-md" style="margin-bottom: 2rem;">
                <h3><%= modoEdicion ? "Editar Rol" : "Crear Nuevo Rol" %></h3>
                <form action="roles" method="post">
                    <input type="hidden" name="action" value="<%= modoEdicion ? "actualizar" : "crear" %>">
                    <% if (modoEdicion && rolEdicion != null) { %>
                    <input type="hidden" name="id" value="<%=rolEdicion.getId()%>">
                    <% } %>
                    
                    <div class="form-group row-group">
                        <div class="form-col">
                            <label for="role-name">Nombre del Rol <span class="required">*</span></label>
                            <input type="text" id="role-name" name="nombre" required placeholder="Ej. MODERADOR" 
                                   value="<%= modoEdicion && rolEdicion != null ? rolEdicion.getNombre() : "" %>">
                        </div>
                        <div class="form-col">
                            <label for="role-description">Descripción</label>
                            <input type="text" id="role-description" name="descripcion" placeholder="Ej. Moderador del sistema" 
                                   value="<%= modoEdicion && rolEdicion != null ? rolEdicion.getDescripcion() : "" %>">
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label>Permisos del Rol</label>
                        <div class="permisos-grid">
                            <label class="permiso-checkbox-wrapper">
                                <input type="checkbox" name="permisos" value="1" <%= modoEdicion && rolEdicion != null && rolEdicion.tienePermiso(1) ? "checked" : "" %>>
                                <span>CREAR_CONTACTOS</span>
                            </label>
                            <label class="permiso-checkbox-wrapper">
                                <input type="checkbox" name="permisos" value="2" <%= modoEdicion && rolEdicion != null && rolEdicion.tienePermiso(2) ? "checked" : "" %>>
                                <span>EDITAR_CONTACTOS</span>
                            </label>
                            <label class="permiso-checkbox-wrapper">
                                <input type="checkbox" name="permisos" value="3" <%= modoEdicion && rolEdicion != null && rolEdicion.tienePermiso(3) ? "checked" : "" %>>
                                <span>ELIMINAR_CONTACTOS</span>
                            </label>
                            <label class="permiso-checkbox-wrapper">
                                <input type="checkbox" name="permisos" value="4" <%= modoEdicion && rolEdicion != null && rolEdicion.tienePermiso(4) ? "checked" : "" %>>
                                <span>VER_CONTACTOS</span>
                            </label>
                            <label class="permiso-checkbox-wrapper">
                                <input type="checkbox" name="permisos" value="5" <%= modoEdicion && rolEdicion != null && rolEdicion.tienePermiso(5) ? "checked" : "" %>>
                                <span>GESTIONAR_USUARIOS</span>
                            </label>
                            <label class="permiso-checkbox-wrapper">
                                <input type="checkbox" name="permisos" value="6" <%= modoEdicion && rolEdicion != null && rolEdicion.tienePermiso(6) ? "checked" : "" %>>
                                <span>GESTIONAR_ROLES</span>
                            </label>
                        </div>
                    </div>
                    
                    <div class="form-actions">
                        <% if (modoEdicion) { %>
                        <a href="roles" class="btn-secondary">Cancelar</a>
                        <% } %>
                        <button type="submit" class="btn-primary"><%= modoEdicion ? "Actualizar Rol" : "Crear Rol" %></button>
                    </div>
                </form>
            </div>

            <!-- Roles List -->
            <div class="card shadow-md">
                <h3 style="margin-bottom: 1rem;">Roles del Sistema</h3>
                <% 
                if (roles != null && !roles.isEmpty()) {
                    for (Rol rol : roles) {
                        boolean esRolBasico = (rol.getId() == 1 || rol.getId() == 2);
                        String badgeClass = "role-badge--custom";
                        if ("ADMIN".equals(rol.getNombre())) badgeClass = "role-badge--admin";
                        else if ("USER".equals(rol.getNombre())) badgeClass = "role-badge--user";
                %>
                <div class="role-card">
                    <div class="role-card-header">
                        <div>
                            <span class="role-badge <%=badgeClass%>"><%=rol.getNombre()%></span>
                            <div class="role-card-title" style="margin-top: 0.5rem;"><%=rol.getNombre()%></div>
                            <div class="role-card-description"><%=rol.getDescripcion() != null ? rol.getDescripcion() : "Sin descripción"%></div>
                        </div>
                        <div class="role-actions">
                            <a href="roles?action=editar&id=<%=rol.getId()%>" class="btn btn-primary-outline btn-sm" title="Editar rol">Editar</a>
                            <% if (!esRolBasico) { %>
                            <a href="roles?action=eliminar&id=<%=rol.getId()%>" class="btn btn-danger btn-sm" title="Eliminar rol" onclick="return confirm('¿Estás seguro de eliminar el rol <%=rol.getNombre()%>? Esta acción no se puede deshacer.');">Eliminar</a>
                            <% } %>
                        </div>
                    </div>
                    <div>
                        <strong>Permisos:</strong>
                        <% if (rol.getPermisos() != null && !rol.getPermisos().isEmpty()) { %>
                        <div class="permisos-list">
                            <% for (Permiso permiso : rol.getPermisos()) { %>
                            <span class="permiso-badge"><%=permiso.getNombre()%></span>
                            <% } %>
                        </div>
                        <% } else { %>
                        <span style="color: #6b7280; font-size: 0.875rem;">Sin permisos asignados</span>
                        <% } %>
                    </div>
                </div>
                <% 
                    }
                } else {
                %>
                <p style="color: #6b7280; text-align: center; padding: 2rem;">No hay roles creados</p>
                <% } %>
            </div>

        </section>
    </main>

    <script>
        document.addEventListener('DOMContentLoaded', () => {
            // Mobile menu toggle
            const menuBtn = document.getElementById('menu-toggle');
            if(menuBtn) {
                menuBtn.addEventListener('click', () => {
                    document.getElementById('sidebar').classList.toggle('open');
                });
            }
        });
    </script>
</body>

</html>
