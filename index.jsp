<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Agenda de Contactos - Inicio</title>
    <link rel="stylesheet" href="styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
</head>
<body class="home-body">
    <header class="main-header">
        <div class="logo">
            <span class="icon">📖</span> ConfigContacts
        </div>
        <nav class="main-nav">
            <a href="index.jsp" title="Regresar a la página principal del sitio">Inicio</a>
            <a href="login.jsp?action=add" title="Dirígete a crear nuevos contactos en el sistema">Añadir Contacto</a>
            <a href="login.jsp?action=search" title="Encuentra un contacto ya registrado fácilmente">Buscar Contacto</a>
            <a href="login.jsp?action=groups" title="Organiza tus contactos por familia, amigos o trabajo">Ver Grupos</a>
            <a href="login.jsp" title="Inicia sesión o regístrate en la plataforma" class="btn-primary-outline">Acceder</a>
        </nav>
    </header>

    <main class="hero-section">
        <div class="hero-content">
            <h1>Administra tus Contactos de Forma Inteligente</h1>
            <p>
                Tu agenda personal siempre contigo. Almacena, organiza y busca tus contactos
                personales, números de teléfono y correos fácilmente desde cualquier lugar.
            </p>
            <div class="hero-actions">
                <a href="login.jsp" class="btn-primary" title="Inicia tu sesión para administrar tu agenda">Ingresar a la Agenda</a>
                <a href="login.jsp?register=true" class="btn-secondary" title="Crea una cuenta gratuita">Registrarse</a>
            </div>
        </div>
        <div class="hero-image">
            <img src="https://images.unsplash.com/photo-1512428559087-560fa5ceab42?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80" alt="Personas conectándose, representación de agenda de contactos" class="rounded-image shadow-lg">
        </div>
    </main>

    <footer class="main-footer">
        <p>&copy; 2026 Agenda de Contactos - Prototipo Universitario.</p>
    </footer>
</body>
</html>
