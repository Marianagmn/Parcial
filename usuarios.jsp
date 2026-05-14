<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="modelo.Usuario" %>
<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Usuarios - Agenda de Contactos</title>
    <link rel="stylesheet" href="styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
</head>

<body class="dashboard-body">

    <!-- Sidebar / Top Nav for Mobile -->
    <aside class="sidebar" id="sidebar">
        <div class="sidebar-header">
            <span class="icon">📖</span>
            <h2>ConfigContacts</h2>
        </div>
        <nav class="sidebar-nav">
            <a href="contactos" class="nav-item" title="Ver todos tus contactos guardados">📋 Lista de Contactos</a>
            <a href="dashboard.jsp#add" class="nav-item" title="Añadir un nuevo contacto a la agenda">➕ Añadir Contacto</a>
            <a href="dashboard.jsp#search" class="nav-item" title="Buscar contactos por nombre o grupo">🔍 Buscar</a>
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
                <span>Hola, <strong id="user-display-name">${sessionScope.usuarioNombre}</strong></span>
                <div class="avatar shadow-sm" title="Tu foto de perfil">${sessionScope.usuarioNombre != null ? sessionScope.usuarioNombre.charAt(0).toUpperCase() : 'U'}</div>
            </div>
        </header>

        <section class="content-area">

            <!-- View: User List -->
            <div id="view-users" class="view-section active">
                <div class="section-header">
                    <h2>Gestión de Usuarios</h2>
                    <p class="text-secondary">Administra los usuarios del sistema y sus roles.</p>
                </div>

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
                                <option value="1">ADMIN - Administrador</option>
                                <option value="2" selected>USER - Usuario Estándar</option>
                            </select>
                        </div>
                        <button type="submit" class="btn-primary" title="Crear nuevo usuario">Crear Usuario</button>
                    </form>
                </div>

                <div class="card shadow-md">
                    <h3>Lista de Usuarios</h3>
                    <table style="width: 100%; border-collapse: collapse;">
                        <thead>
                            <tr style="background-color: #f3f4f6; border-bottom: 2px solid #e5e7eb;">
                                <th style="padding: 12px; text-align: left;">Nombre</th>
                                <th style="padding: 12px; text-align: left;">Usuario</th>
                                <th style="padding: 12px; text-align: left;">Email</th>
                                <th style="padding: 12px; text-align: left;">Rol</th>
                                <th style="padding: 12px; text-align: left;">Estado</th>
                                <th style="padding: 12px; text-align: left;">Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% 
                            List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios");
                            if (usuarios != null && !usuarios.isEmpty()) {
                                for (Usuario usuario : usuarios) {
                            %>
                            <tr style="border-bottom: 1px solid #e5e7eb;">
                                <td style="padding: 12px;">${usuario.getNombre()}</td>
                                <td style="padding: 12px;">${usuario.getUsername()}</td>
                                <td style="padding: 12px;">${usuario.getEmail() != null ? usuario.getEmail() : 'Sin email'}</td>
                                <td style="padding: 12px;">
                                    ${usuario.getRol() != null ? usuario.getRol().getNombre() : 'Sin rol'}
                                </td>
                                <td style="padding: 12px;">
                                    ${usuario.isActivo() ? '<span style="color: green;">Activo</span>' : '<span style="color: red;">Inactivo</span>'}
                                </td>
                                <td style="padding: 12px;">
                                    <a href="usuarios?action=asignarRol&id=${usuario.getId()}&rolId=1" class="btn btn-primary-outline btn-sm" title="Asignar rol ADMIN">Admin</a>
                                    <a href="usuarios?action=asignarRol&id=${usuario.getId()}&rolId=2" class="btn btn-primary-outline btn-sm" title="Asignar rol USER">User</a>
                                    <% if (usuario.getId() != session.getAttribute("usuarioId")) { %>
                                    <a href="usuarios?action=eliminar&id=${usuario.getId()}" class="btn btn-danger btn-sm" title="Eliminar usuario" onclick="return confirm('¿Estás seguro de que deseas eliminar este usuario?');">Eliminar</a>
                                    <% } %>
                                </td>
                            </tr>
                            <% 
                                }
                            } else {
                            %>
                            <tr>
                                <td colspan="6" style="padding: 20px; text-align: center;">No hay usuarios registrados</td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
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
