<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Acceso - Agenda de Contactos</title>
    <link rel="stylesheet" href="styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
</head>

<body class="auth-body flex-center">

    <div class="auth-container shadow-lg">
        <div class="auth-header">
            <h2>Bienvenido</h2>
            <p id="auth-subtitle">${modoRegistro ? 'Regístrate para continuar' : 'Inicia sesión en tu agenda'}</p>
        </div>

        <form id="auth-form" class="auth-form" action="login" method="post">
            <!-- Dynamic field for signup -->
            <div class="form-group" id="name-group" style="${modoRegistro ? 'display: block;' : 'display: none;'}">
                <label for="reg-name">Nombre Completo</label>
                <input type="text" id="reg-name" name="nombre" placeholder="Ej: Juan Pérez" 
                       value="${nombre}" ${modoRegistro ? 'required' : ''}>
            </div>

            <div class="form-group">
                <label for="username">Usuario (Email o Nickname)</label>
                <input type="text" id="username" name="username" required placeholder="tu@correo.com" 
                       value="${username}">
            </div>

            <div class="form-group">
                <label for="password">Contraseña</label>
                <input type="password" id="password" name="password" required placeholder="••••••••">
            </div>

            <div class="form-group" id="email-group" style="${modoRegistro ? 'display: block;' : 'display: none;'}">
                <label for="reg-email">Correo Electrónico</label>
                <input type="email" id="reg-email" name="email" placeholder="Ej: usuario@email.com" 
                       value="${email}">
            </div>

            <input type="hidden" name="action" value="${modoRegistro ? 'registro' : 'login'}">

            <div id="auth-error" class="alert debug-alert"
                style="${error != null ? 'display:block; color: #dc2626; font-size: 0.875rem; margin-bottom: 1rem;' : 'display:none; color: #dc2626; font-size: 0.875rem; margin-bottom: 1rem;'}">
                ${error}
            </div>

            <button type="submit" class="btn-primary w-full" id="auth-btn"
                title="Haz clic para ${modoRegistro ? 'crear cuenta' : 'iniciar sesión'}">
                ${modoRegistro ? 'Crear Cuenta' : 'Acceder'}
            </button>
        </form>

        <div class="auth-footer">
            <p id="toggle-auth-text">
                ${modoRegistro ? '¿Ya tienes una cuenta? ' : '¿No tienes credenciales? '}
                <a href="login.jsp?${modoRegistro ? '' : 'register=true'}" id="toggle-auth-link"
                    title="${modoRegistro ? 'Inicia sesión con tu cuenta existente' : 'Regístrate como nuevo usuario'}">
                    ${modoRegistro ? 'Inicia sesión' : 'Regístrate aquí'}
                </a>
            </p>
            <p style="margin-top: 1rem;">
                <a href="index.jsp" class="text-secondary" title="Regresar al inicio">Volver al Inicio</a>
            </p>
        </div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Toggle between login and register modes
            const toggleLink = document.getElementById('toggle-auth-link');
            if (toggleLink) {
                toggleLink.addEventListener('click', function(e) {
                    e.preventDefault();
                    const url = new URL(window.location.href);
                    const registerParam = url.searchParams.get('register');
                    if (registerParam === 'true') {
                        url.searchParams.delete('register');
                    } else {
                        url.searchParams.set('register', 'true');
                    }
                    window.location.href = url.toString();
                });
            }
        });
    </script>
</body>

</html>
