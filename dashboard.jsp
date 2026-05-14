<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="modelo.Contacto" %>
<%@ page import="modelo.Usuario" %>
<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Agenda de Contactos</title>
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
            <a href="contactos" class="nav-item active" data-target="view-list" title="Ver todos tus contactos guardados">📋
                Lista de Contactos</a>
            <a href="#add" class="nav-item" data-target="view-add" title="Añadir un nuevo contacto a la agenda">➕ Añadir
                Contacto</a>
            <a href="#search" class="nav-item" data-target="view-search" title="Buscar contactos por nombre o grupo">🔍
                Buscar / Filtrar</a>
            <% if ("ADMIN".equals(session.getAttribute("usuarioRol"))) { %>
            <a href="usuarios" class="nav-item" data-target="view-users" title="Gestionar usuarios del sistema">👥
                Gestionar Usuarios</a>
            <% } %>
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

            <!-- View: Contact List -->
            <div id="view-list" class="view-section active">
                <div class="section-header">
                    <h2>Mis Contactos</h2>
                    <p class="text-secondary">Aquí puedes ver toda tu agenda personal.</p>
                </div>

                <div class="contacts-grid" id="contacts-container">
                    <% 
                    List<Contacto> contactos = (List<Contacto>) request.getAttribute("contactos");
                    if (contactos != null && !contactos.isEmpty()) {
                        for (Contacto contacto : contactos) {
                            String groupBg = "#E5E7EB";
                            if ("Familia".equals(contacto.getGrupo())) groupBg = "#DBEAFE";
                            if ("Trabajo".equals(contacto.getGrupo())) groupBg = "#FEF3C7";
                            if ("Amigos".equals(contacto.getGrupo())) groupBg = "#D1FAE5";
                    %>
                    <div class="contact-card">
                        <div class="contact-header">
                            <div class="contact-avatar">${contacto.getNombre().charAt(0).toUpperCase()}</div>
                            <div>
                                <h3 class="contact-name">${contacto.getNombre()}</h3>
                                <span class="contact-group-badge" style="background-color: ${groupBg}">${contacto.getGrupo()}</span>
                            </div>
                        </div>
                        <div class="contact-info">
                            <p title="Teléfono">📞 ${contacto.getTelefono()}</p>
                            <p title="Email">✉️ ${contacto.getEmail() != null ? contacto.getEmail() : 'Sin correo'}</p>
                        </div>
                        <div class="contact-actions">
                            <a href="contactos?action=editar&id=${contacto.getId()}" class="btn btn-primary-outline btn-sm" title="Editar la información de ${contacto.getNombre()}">Editar</a>
                            <a href="contactos?action=eliminar&id=${contacto.getId()}" class="btn btn-danger btn-sm" title="Eliminar a ${contacto.getNombre()} de la agenda" onclick="return confirm('¿Estás seguro de que deseas eliminar este contacto?');">Eliminar</a>
                        </div>
                    </div>
                    <% 
                        }
                    } else {
                    %>
                    <div id="empty-state" class="empty-state" style="display: block;">
                        <span class="empty-icon">📂</span>
                        <h3>Aún no tienes contactos</h3>
                        <p>Comienza a crear tu agenda añadiendo tu primer contacto.</p>
                        <a href="#add" class="btn-primary" title="Ve al formulario para crear un contacto">Crear Contacto</a>
                    </div>
                    <% } %>
                </div>
            </div>

            <!-- View: Add / Edit Contact -->
            <div id="view-add" class="view-section">
                <div class="section-header">
                    <h2 id="form-title">${modoEdicion ? 'Editar Contacto' : 'Añadir Nuevo Contacto'}</h2>
                    <p class="text-secondary">Rellena la información del contacto.</p>
                </div>

                <div class="card shadow-md form-card">
                    <form id="contact-form" action="contactos" method="post">
                        <input type="hidden" name="action" value="${modoEdicion ? 'actualizar' : 'crear'}">
                        <% if (modoEdicion && request.getAttribute("contacto") != null) { 
                            Contacto contacto = (Contacto) request.getAttribute("contacto");
                        %>
                        <input type="hidden" name="id" value="${contacto.getId()}">
                        <% } %>

                        <div class="form-group row-group">
                            <div class="form-col">
                                <label for="contact-name">Nombre Completo <span class="required">*</span></label>
                                <input type="text" id="contact-name" name="nombre" required placeholder="Ej. Ana García"
                                       value="${contacto != null ? contacto.getNombre() : ''}">
                            </div>
                            <div class="form-col">
                                <label for="contact-phone">Teléfono <span class="required">*</span></label>
                                <input type="tel" id="contact-phone" name="telefono" required placeholder="Ej. +57 300 000 0000"
                                       value="${contacto != null ? contacto.getTelefono() : ''}">
                            </div>
                        </div>

                        <div class="form-group row-group">
                            <div class="form-col">
                                <label for="contact-email">Correo Electrónico</label>
                                <input type="email" id="contact-email" name="email" placeholder="Ej. ana.garcia@email.com"
                                       value="${contacto != null ? contacto.getEmail() : ''}">
                            </div>
                            <div class="form-col">
                                <label for="contact-group">Grupo</label>
                                <select id="contact-group" name="grupo">
                                    <option value="Familia" ${contacto != null && "Familia".equals(contacto.getGrupo()) ? 'selected' : ''}>Familia</option>
                                    <option value="Amigos" ${contacto != null && "Amigos".equals(contacto.getGrupo()) ? 'selected' : ''}>Amigos</option>
                                    <option value="Trabajo" ${contacto != null && "Trabajo".equals(contacto.getGrupo()) ? 'selected' : ''}>Trabajo</option>
                                    <option value="Otros" ${(contacto == null || "Otros".equals(contacto.getGrupo())) ? 'selected' : ''}>Otros</option>
                                </select>
                            </div>
                        </div>

                        <div class="form-actions">
                            <a href="contactos" class="btn-secondary" id="cancel-edit-btn" style="${modoEdicion ? 'display: inline-block;' : 'display: none;'}"
                                title="Cancelar edición">Cancelar</a>
                            <button type="submit" class="btn-primary" id="save-contact-btn"
                                title="Guardar la información del contacto">Guardar Contacto</button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- View: Search -->
            <div id="view-search" class="view-section">
                <div class="section-header">
                    <h2>Buscar Resultados</h2>
                    <p class="text-secondary">Encuentra a alguien específico en tu red.</p>
                </div>

                <div class="search-bar-container card shadow-sm">
                    <form action="contactos" method="get">
                        <input type="hidden" name="action" value="buscar">
                        <div class="form-group">
                            <input type="text" name="termino" placeholder="Buscar por Nombre, Teléfono o Email..."
                                title="Ingresa un término para filtrar tus contactos" value="${terminoBusqueda}">
                        </div>
                        <button type="submit" class="btn-primary">Buscar</button>
                    </form>
                </div>

                <div class="contacts-grid" id="search-results-container">
                    <% 
                    List<Contacto> resultados = (List<Contacto>) request.getAttribute("contactos");
                    if (resultados != null && !resultados.isEmpty()) {
                        for (Contacto contacto : resultados) {
                            String groupBg = "#E5E7EB";
                            if ("Familia".equals(contacto.getGrupo())) groupBg = "#DBEAFE";
                            if ("Trabajo".equals(contacto.getGrupo())) groupBg = "#FEF3C7";
                            if ("Amigos".equals(contacto.getGrupo())) groupBg = "#D1FAE5";
                    %>
                    <div class="contact-card">
                        <div class="contact-header">
                            <div class="contact-avatar">${contacto.getNombre().charAt(0).toUpperCase()}</div>
                            <div>
                                <h3 class="contact-name">${contacto.getNombre()}</h3>
                                <span class="contact-group-badge" style="background-color: ${groupBg}">${contacto.getGrupo()}</span>
                            </div>
                        </div>
                        <div class="contact-info">
                            <p title="Teléfono">📞 ${contacto.getTelefono()}</p>
                            <p title="Email">✉️ ${contacto.getEmail() != null ? contacto.getEmail() : 'Sin correo'}</p>
                        </div>
                        <div class="contact-actions">
                            <a href="contactos?action=editar&id=${contacto.getId()}" class="btn btn-primary-outline btn-sm" title="Editar">Editar</a>
                            <a href="contactos?action=eliminar&id=${contacto.getId()}" class="btn btn-danger btn-sm" title="Eliminar" onclick="return confirm('¿Estás seguro de que deseas eliminar este contacto?');">Eliminar</a>
                        </div>
                    </div>
                    <% 
                        }
                    } else if (terminoBusqueda != null && !terminoBusqueda.isEmpty()) {
                    %>
                    <p class="text-secondary">No se encontraron resultados que coincidan con la búsqueda.</p>
                    <% } else { %>
                    <p class="text-secondary">Escribe algo arriba para comenzar a buscar.</p>
                    <% } %>
                </div>
            </div>

        </section>
    </main>

    <script>
        document.addEventListener('DOMContentLoaded', () => {
            // SPA Navigation System
            const navItems = document.querySelectorAll('.nav-item');
            const views = document.querySelectorAll('.view-section');

            function switchView(targetId) {
                // Update Nav Menu UI
                navItems.forEach(nav => nav.classList.remove('active'));
                const activeNav = document.querySelector(`[data-target="${targetId}"]`);
                if(activeNav) activeNav.classList.add('active');

                // Toggle Views
                views.forEach(view => {
                    view.classList.remove('active');
                    if (view.id === targetId) {
                        view.classList.add('active');
                    }
                });

                // Hide sidebar on mobile after clicking
                document.getElementById('sidebar').classList.remove('open');
            }

            navItems.forEach(item => {
                item.addEventListener('click', (e) => {
                    const target = item.getAttribute('data-target');
                    if (target && target.startsWith('view-')) {
                        e.preventDefault();
                        switchView(target);
                    }
                });
            });

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
