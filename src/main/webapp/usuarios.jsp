<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="modelo.Usuario" %>
<%@ page import="modelo.Rol" %>
<%
    // Verificar sesión activa y rol ADMIN o SUPERADMIN
    if (session.getAttribute("usuarioId") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    String usuarioRol = (String) session.getAttribute("usuarioRol");
    if (!"ADMIN".equals(usuarioRol) && !"SUPERADMIN".equals(usuarioRol)) {
        response.sendRedirect("contactos");
        return;
    }
    
    String usuarioNombre = (String) session.getAttribute("usuarioNombre");
    Integer usuarioIdActual = (Integer) session.getAttribute("usuarioId");
    String error = (String) request.getAttribute("error");
    boolean esSuperAdmin = "SUPERADMIN".equals(usuarioRol);
    
    // Obtener roles disponibles
    List<Rol> roles = (List<Rol>) request.getAttribute("roles");
%>
<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Usuarios - Agenda de Contactos</title>
    <link rel="stylesheet" href="styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        /* === Role Management Styles === */
        .role-badge {
            display: inline-flex;
            align-items: center;
            gap: 0.35rem;
            padding: 0.25rem 0.75rem;
            border-radius: 999px;
            font-size: 0.75rem;
            font-weight: 600;
            letter-spacing: 0.025em;
            text-transform: uppercase;
        }
        .role-badge--superadmin {
            background: linear-gradient(135deg, #7c3aed, #a855f7);
            color: #fff;
            box-shadow: 0 2px 8px rgba(124, 58, 237, 0.3);
        }
        .role-badge--admin {
            background: linear-gradient(135deg, #2563eb, #3b82f6);
            color: #fff;
            box-shadow: 0 2px 8px rgba(37, 99, 235, 0.25);
        }
        .role-badge--user {
            background: linear-gradient(135deg, #059669, #10b981);
            color: #fff;
            box-shadow: 0 2px 8px rgba(5, 150, 105, 0.25);
        }
        .role-badge--default {
            background: #e5e7eb;
            color: #6b7280;
        }
        .role-badge .role-icon {
            font-size: 0.85rem;
        }

        /* Role assignment select */
        .role-select-wrapper {
            position: relative;
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
        }
        .role-select {
            appearance: none;
            -webkit-appearance: none;
            padding: 0.4rem 2rem 0.4rem 0.65rem;
            border: 2px solid var(--border-light);
            border-radius: var(--radius-sm);
            font-size: 0.8rem;
            font-weight: 500;
            background: var(--bg-surface);
            cursor: pointer;
            transition: all 0.2s ease;
            min-width: 140px;
        }
        .role-select:hover {
            border-color: var(--primary);
        }
        .role-select:focus {
            outline: none;
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.15);
        }
        .role-select-arrow {
            position: absolute;
            right: 0.5rem;
            top: 50%;
            transform: translateY(-50%);
            pointer-events: none;
            font-size: 0.65rem;
            color: var(--text-secondary);
        }

        /* Status badges */
        .status-badge {
            display: inline-flex;
            align-items: center;
            gap: 0.3rem;
            padding: 0.2rem 0.6rem;
            border-radius: 999px;
            font-size: 0.75rem;
            font-weight: 500;
        }
        .status-badge--active {
            background: #d1fae5;
            color: #065f46;
        }
        .status-badge--inactive {
            background: #fee2e2;
            color: #991b1b;
        }
        .status-dot {
            width: 6px;
            height: 6px;
            border-radius: 50%;
            display: inline-block;
        }
        .status-dot--active { background: #10b981; }
        .status-dot--inactive { background: #ef4444; }

        /* User table improvements */
        .users-table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0;
        }
        .users-table thead tr {
            background: linear-gradient(135deg, #f8fafc, #f1f5f9);
        }
        .users-table th {
            padding: 0.875rem 1rem;
            text-align: left;
            font-size: 0.75rem;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.05em;
            color: var(--text-secondary);
            border-bottom: 2px solid var(--border-light);
        }
        .users-table td {
            padding: 0.875rem 1rem;
            font-size: 0.875rem;
            border-bottom: 1px solid #f3f4f6;
            vertical-align: middle;
        }
        .users-table tbody tr {
            transition: background-color 0.15s ease;
        }
        .users-table tbody tr:hover {
            background-color: #f9fafb;
        }
        .users-table tbody tr:last-child td {
            border-bottom: none;
        }

        /* User info cell */
        .user-cell {
            display: flex;
            align-items: center;
            gap: 0.75rem;
        }
        .user-cell-avatar {
            width: 36px;
            height: 36px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 600;
            font-size: 0.875rem;
            color: #fff;
            flex-shrink: 0;
        }
        .user-cell-avatar--superadmin { background: linear-gradient(135deg, #7c3aed, #a855f7); }
        .user-cell-avatar--admin { background: linear-gradient(135deg, #2563eb, #3b82f6); }
        .user-cell-avatar--user { background: linear-gradient(135deg, #059669, #10b981); }
        .user-cell-avatar--default { background: #9ca3af; }

        .user-cell-info { display: flex; flex-direction: column; }
        .user-cell-name { font-weight: 600; color: var(--text-primary); }
        .user-cell-email { font-size: 0.75rem; color: var(--text-secondary); }

        /* Action buttons */
        .actions-cell {
            display: flex;
            gap: 0.5rem;
            align-items: center;
            flex-wrap: wrap;
        }

        /* Section info banner for superadmin */
        .superadmin-banner {
            display: flex;
            align-items: center;
            gap: 0.75rem;
            padding: 0.875rem 1.25rem;
            background: linear-gradient(135deg, #ede9fe, #f5f3ff);
            border: 1px solid #c4b5fd;
            border-radius: var(--radius-md);
            margin-bottom: 1.5rem;
            font-size: 0.875rem;
            color: #5b21b6;
        }
        .superadmin-banner .banner-icon {
            font-size: 1.25rem;
        }

        /* Confirmation toast */
        .toast {
            position: fixed;
            top: 1.5rem;
            right: 1.5rem;
            padding: 1rem 1.5rem;
            border-radius: var(--radius-md);
            background: #1f2937;
            color: #fff;
            font-size: 0.875rem;
            font-weight: 500;
            box-shadow: var(--shadow-lg);
            z-index: 9999;
            transform: translateX(120%);
            transition: transform 0.35s cubic-bezier(0.4, 0, 0.2, 1);
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        .toast.show {
            transform: translateX(0);
        }

        /* Self-user row highlight */
        .users-table tbody tr.current-user-row {
            background-color: #eff6ff;
        }
        .users-table tbody tr.current-user-row:hover {
            background-color: #dbeafe;
        }
        .self-badge {
            display: inline-flex;
            align-items: center;
            gap: 0.2rem;
            font-size: 0.65rem;
            font-weight: 600;
            color: var(--primary);
            background: #e0e7ff;
            padding: 0.1rem 0.4rem;
            border-radius: 999px;
            margin-left: 0.35rem;
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
            <a href="usuarios" class="nav-item active" title="Gestionar usuarios del sistema">👥 Gestionar Usuarios</a>
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

            <div id="view-users" class="view-section active">
                <div class="section-header">
                    <h2>Gestión de Usuarios</h2>
                    <p class="text-secondary">Administra los usuarios del sistema<%= esSuperAdmin ? " y asigna roles." : "."%></p>
                </div>

                <% if (esSuperAdmin) { %>
                <div class="superadmin-banner">
                    <span class="banner-icon">👑</span>
                    <span>Tienes privilegios de <strong>Super Administrador</strong>. Puedes asignar y cambiar roles de todos los usuarios.</span>
                </div>
                <% } %>

                <% if (error != null && !error.isEmpty()) { %>
                <div class="alert debug-alert" style="display:block; color: #dc2626; font-size: 0.875rem; margin-bottom: 1rem; padding: 0.75rem; background: #fef2f2; border-radius: 0.375rem;">
                    <%=error%>
                </div>
                <% } %>

                <!-- Create User Form -->
                <div class="card shadow-md" style="margin-bottom: 2rem;">
                    <h3>Crear Nuevo Usuario</h3>
                    <form action="usuarios" method="post">
                        <input type="hidden" name="action" value="crear">
                        <div class="form-group row-group">
                            <div class="form-col">
                                <label for="user-name">Nombre Completo <span class="required">*</span></label>
                                <input type="text" id="user-name" name="nombre" required placeholder="Ej. Juan Pérez">
                            </div>
                            <div class="form-col">
                                <label for="user-username">Usuario <span class="required">*</span></label>
                                <input type="text" id="user-username" name="username" required placeholder="Ej. juanperez">
                            </div>
                        </div>
                        <div class="form-group row-group">
                            <div class="form-col">
                                <label for="user-password">Contraseña <span class="required">*</span></label>
                                <input type="password" id="user-password" name="password" required placeholder="••••••••">
                            </div>
                            <div class="form-col">
                                <label for="user-email">Correo Electrónico</label>
                                <input type="email" id="user-email" name="email" placeholder="Ej. juan@email.com">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="user-role">Rol <span class="required">*</span></label>
                            <select id="user-role" name="rolId" required>
                                <% if (roles != null) {
                                    for (Rol r : roles) {
                                        // Solo SUPERADMIN puede crear otro SUPERADMIN
                                        if ("SUPERADMIN".equals(r.getNombre()) && !esSuperAdmin) continue;
                                %>
                                <option value="<%=r.getId()%>" <%="USER".equals(r.getNombre()) ? "selected" : ""%>>
                                    <%=r.getNombre()%> - <%=r.getDescripcion()%>
                                </option>
                                <% }} %>
                            </select>
                        </div>
                        <button type="submit" class="btn-primary" title="Crear nuevo usuario">Crear Usuario</button>
                    </form>
                </div>

                <!-- Users List -->
                <div class="card shadow-md">
                    <h3 style="margin-bottom: 1rem;">Lista de Usuarios</h3>
                    <div style="overflow-x: auto;">
                        <table class="users-table">
                            <thead>
                                <tr>
                                    <th>Usuario</th>
                                    <th>Username</th>
                                    <th>Rol Actual</th>
                                    <% if (esSuperAdmin) { %>
                                    <th>Cambiar Rol</th>
                                    <% } %>
                                    <th>Estado</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% 
                                List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios");
                                if (usuarios != null && !usuarios.isEmpty()) {
                                    for (Usuario usuario : usuarios) {
                                        String rolNombre = usuario.getRol() != null ? usuario.getRol().getNombre() : "Sin rol";
                                        boolean esActivo = usuario.isActivo();
                                        boolean esUsuarioActual = (usuario.getId() == usuarioIdActual);
                                        
                                        // Determine avatar style class
                                        String avatarClass = "user-cell-avatar--default";
                                        if ("SUPERADMIN".equals(rolNombre)) avatarClass = "user-cell-avatar--superadmin";
                                        else if ("ADMIN".equals(rolNombre)) avatarClass = "user-cell-avatar--admin";
                                        else if ("USER".equals(rolNombre)) avatarClass = "user-cell-avatar--user";
                                %>
                                <tr class="<%= esUsuarioActual ? "current-user-row" : "" %>">
                                    <td>
                                        <div class="user-cell">
                                            <div class="user-cell-avatar <%=avatarClass%>">
                                                <%=String.valueOf(usuario.getNombre().charAt(0)).toUpperCase()%>
                                            </div>
                                            <div class="user-cell-info">
                                                <span class="user-cell-name">
                                                    <%=usuario.getNombre()%>
                                                    <% if (esUsuarioActual) { %>
                                                    <span class="self-badge">👤 Tú</span>
                                                    <% } %>
                                                </span>
                                                <span class="user-cell-email"><%=usuario.getEmail() != null ? usuario.getEmail() : "Sin email"%></span>
                                            </div>
                                        </div>
                                    </td>
                                    <td><%=usuario.getUsername()%></td>
                                    <td>
                                        <% if ("SUPERADMIN".equals(rolNombre)) { %>
                                            <span class="role-badge role-badge--superadmin"><span class="role-icon">👑</span> SuperAdmin</span>
                                        <% } else if ("ADMIN".equals(rolNombre)) { %>
                                            <span class="role-badge role-badge--admin"><span class="role-icon">🛡️</span> Admin</span>
                                        <% } else if ("USER".equals(rolNombre)) { %>
                                            <span class="role-badge role-badge--user"><span class="role-icon">👤</span> User</span>
                                        <% } else { %>
                                            <span class="role-badge role-badge--default"><%=rolNombre%></span>
                                        <% } %>
                                    </td>
                                    <% if (esSuperAdmin) { %>
                                    <td>
                                        <% if (!esUsuarioActual) { %>
                                        <div class="role-select-wrapper">
                                            <select class="role-select" 
                                                    data-user-id="<%=usuario.getId()%>" 
                                                    data-user-name="<%=usuario.getNombre()%>"
                                                    data-current-role="<%=usuario.getRolId()%>"
                                                    onchange="confirmarCambioRol(this)">
                                                <% if (roles != null) {
                                                    for (Rol r : roles) { %>
                                                <option value="<%=r.getId()%>" <%=r.getId() == usuario.getRolId() ? "selected" : ""%>>
                                                    <%=r.getNombre()%>
                                                </option>
                                                <% }} %>
                                            </select>
                                            <span class="role-select-arrow">▼</span>
                                        </div>
                                        <% } else { %>
                                        <span style="font-size: 0.8rem; color: var(--text-secondary); font-style: italic;">— Tu propio rol —</span>
                                        <% } %>
                                    </td>
                                    <% } %>
                                    <td>
                                        <% if (esActivo) { %>
                                            <span class="status-badge status-badge--active">
                                                <span class="status-dot status-dot--active"></span> Activo
                                            </span>
                                        <% } else { %>
                                            <span class="status-badge status-badge--inactive">
                                                <span class="status-dot status-dot--inactive"></span> Inactivo
                                            </span>
                                        <% } %>
                                    </td>
                                    <td>
                                        <div class="actions-cell">
                                            <% if (!esUsuarioActual) { %>
                                            <a href="usuarios?action=eliminar&id=<%=usuario.getId()%>" 
                                               class="btn btn-danger btn-sm" 
                                               title="Eliminar usuario" 
                                               onclick="return confirm('¿Estás seguro de que deseas eliminar a <%=usuario.getNombre()%>?');">Eliminar</a>
                                            <% } %>
                                        </div>
                                    </td>
                                </tr>
                                <% 
                                    }
                                } else {
                                %>
                                <tr>
                                    <td colspan="<%= esSuperAdmin ? 6 : 5 %>" style="padding: 20px; text-align: center; color: var(--text-secondary);">
                                        No hay usuarios registrados
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

        </section>
    </main>

    <!-- Toast notification -->
    <div class="toast" id="toast"></div>

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

        function confirmarCambioRol(selectElement) {
            const userId = selectElement.getAttribute('data-user-id');
            const userName = selectElement.getAttribute('data-user-name');
            const currentRoleId = selectElement.getAttribute('data-current-role');
            const newRoleId = selectElement.value;
            const newRoleName = selectElement.options[selectElement.selectedIndex].text.trim();

            // If same role selected, do nothing
            if (newRoleId === currentRoleId) return;

            const confirmed = confirm(
                '¿Estás seguro de cambiar el rol de "' + userName + '" a ' + newRoleName + '?\n\n' +
                'Esta acción modificará los permisos del usuario de forma inmediata.'
            );

            if (confirmed) {
                // Navigate to the assign role URL
                window.location.href = 'usuarios?action=asignarRol&id=' + userId + '&rolId=' + newRoleId;
            } else {
                // Revert the select to the original value
                selectElement.value = currentRoleId;
            }
        }

        function showToast(message) {
            const toast = document.getElementById('toast');
            toast.textContent = message;
            toast.classList.add('show');
            setTimeout(() => toast.classList.remove('show'), 3000);
        }
    </script>
</body>

</html>
