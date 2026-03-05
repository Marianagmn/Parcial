# Agenda de Contactos - Proyecto Final

## 1. Descripción del Caso
El caso seleccionado consiste en la construcción de una aplicación web para gestionar una "Agenda de Contactos" personales. La aplicación web debe permitir almacenar, editar, buscar, visualizar y eliminar contactos. Cada contacto deberá tener la siguiente información: Nombre, Teléfono, Email y Grupo. El sistema contará con tres pantallas principales: inicio, login y el dashboard (panel de control) para el manejo de los contactos simulando una sesión activa con diseño adaptable a diferentes resoluciones (Responsive).

## 2. Planteamiento del Problema y Solución
**Problema:**
La falta de una herramienta organizada, accesible y centralizada para administrar contactos personales de forma digital genera dificultad en la búsqueda, desactualización de la información y la potencial pérdida de datos importantes.

**Solución:**
Desarrollar un prototipo funcional basado en tecnologías web (HTML, CSS y JavaScript) que permita mantener una libreta de contactos centralizada a través de un Dashboard. El sistema contará con autenticación para asegurar la privacidad y ofrecerá funciones CRUD completas (Crear, Leer, Actualizar, Borrar) para gestionar contactos de manera eficiente y en cualquier dispositivo, usando arquitectura responsiva y navegación amigable.

## 3. Requerimientos Funcionales y No Funcionales

### Requerimientos Funcionales
1. **Página de Inicio:** El sistema debe contar con una pantalla principal (`index.html`) que contenga el menú de opciones (mínimo 5 opciones con descripción en atributo Alt/Title), una imagen y la descripción de la app web.
2. **Autenticación (Login):** El sistema debe permitir el acceso (`login.html`) ingresando usuario y contraseña, y debe ofrecer una opción para simular la creación/registro de un usuario nuevo si no tiene cuenta.
3. **Gestión de Contactos (Dashboard):** Una vez validadas las credenciales, el sistema redigirá al Dashboard (`dashboard.html`), el cual mostrará el nombre del usuario en sesión y poseerá un botón de cierre de sesión.
4. **CRUD:** En el Dashboard, el usuario podrá añadir (Nombre, Teléfono, Email, Grupo), visualizar, editar y eliminar contactos usando búsqueda y filtrado de la información por campos o texto.
5. **Navegación Interna:** La navegación desde el menú del Dashboard debe realizarse en la misma pantalla usando secciones que se ocultan o muestran dinámicamente, en ningún caso abrirse en ventanas o páginas nuevas (Single Page Application).

### Requerimientos No Funcionales
1. **Diseño Responsivo:** La aplicación debe adaptarse correctamente a pantallas de diferentes resoluciones (móviles y escritorio) usando buenas prácticas en CSS (Flexbox/Grid).
2. **Usabilidad:** Todas las opciones del menú deben tener descripciones emergentes cuando el ratón pase por encima (atributo `title` en links o botones).
3. **Persistencia (Simulada):** El almacenamiento de datos (usuarios registrados y contactos) funcionará en memoria temporal del navegador (LocalStorage) como parte de la demostración de este prototipo.

## 4. Alcance de la App Web
Este proyecto cubre el desarrollo del prototipo de la aplicación a nivel de Frontend. El alcance contempla el diseño estructural en HTML5, hojas de estilos interactivas CSS3 (vistas de escritorio y móvil) y el uso de JavaScript vainilla para gestionar flujos simples de lógica: validación simulada de acceso, agregar usuarios dentro del navegador del cliente actual, administración de contactos dentro del mismo navegador y manejo del DOM para asegurar el requerimiento Single Page UI sin uso actual de una base de datos distribuida ni backend. Se trata de una prueba de concepto o prototipo funcional listo para conectarse a una API en siguientes etapas.

## 5. Diseño de la App (Mockup / Wireframes de Referencia)

### Pantalla 1 - Inicio (`index.html`)
- **Header:** Logotipo textual de la app y Menú principal.
  - Opciones de Menú: "Inicio", "Agregar Amigo", "Buscar Contacto", "Grupos y Listas", "Acceso".
  - *Tip (Hover):* "Ir a la página inicial", "Crear nuevo registro", etc.
- **Hero/Cuerpo:** Título grande "Bienvenido a tu Agenda", descripción comercial, bloque de imagen alusiva a la conexión entre personas y un botón central para "Ingresar ahora".

### Pantalla 2 - Autenticación (`login.html`)
- Tarjeta centrada en pantalla con dos campos de texto (Usuario, Contraseña).
- Botón principal de "Ingresar".
- Enlace en la parte inferior de la tarjeta: "¿No tienes cuenta? Regístrate aquí". Al hacer clic, este texto y el botón cambian la acción para crear una nueva credencial (validación con LocalStorage).

### Pantalla 3 - Panel / Dashboard (`dashboard.html`)
- **Sidebar (Panel lateral izquierdo) / Header Mobile:** Menu de navegación (Añadir Contacto, Mi Lista, Buscar, Editar, Salir).
- **Barra de navegación (Top):** "Hola, [Nombre]".
- **Panel principal:** Dependiendo de la opción del menú, mostrará un formulario (`<form>` para añadir/editar) o una tabla/lista (`<table>` / `<ul>` para mostrar/buscar) que operará de manera asincrónica u ocultando visibilidad de "divs" hermanos.
