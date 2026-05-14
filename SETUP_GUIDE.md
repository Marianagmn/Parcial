# Guía de Configuración - Agenda de Contactos JSP

## Requisitos Previos

### Software Necesario
- **Java Development Kit (JDK)**: Versión 11 o superior
- **Apache Tomcat**: Versión 9.0 o superior (para Jakarta EE 9)
- **MySQL Server**: Versión 5.7 o superior
- **IDE Recomendado**: NetBeans, Eclipse, o IntelliJ IDEA

### Dependencias JAR Requeridas

Para que el proyecto funcione correctamente, necesitas agregar los siguientes archivos JAR a tu proyecto:

1. **Jakarta Servlet API** (para Tomcat 9.0+)
   - Descargar: [Jakarta Servlet API 5.0.0](https://mvnrepository.com/artifact/jakarta.servlet/jakarta.servlet-api/5.0.0)
   - Ubicación: `WEB-INF/lib/`

2. **MySQL Connector/J**
   - Descargar: [MySQL Connector/J 8.0.33](https://mvnrepository.com/artifact/com.mysql/mysql-connector-j/8.0.33)
   - Ubicación: `WEB-INF/lib/`

## Configuración de la Base de Datos

### 1. Crear la Base de Datos

Ejecuta el script SQL proporcionado en `database.sql`:

```bash
mysql -u root -p < database.sql
```

O ejecuta el script directamente en tu cliente MySQL:

```sql
CREATE DATABASE IF NOT EXISTS agenda_contactos;
USE agenda_contactos;

-- El resto del script se encuentra en database.sql
```

### 2. Configurar las Credenciales

Edita el archivo `src/modelo/DatabaseConnection.java` y actualiza las credenciales:

```java
private static final String URL = "jdbc:mysql://localhost:3306/agenda_contactos";
private static final String USER = "root";  // Cambiar por tu usuario de MySQL
private static final String PASSWORD = "tu_password";  // Cambiar por tu contraseña de MySQL
```

## Configuración del Proyecto en NetBeans

### 1. Importar el Proyecto

1. Abre NetBeans
2. Ve a `File` > `Open Project`
3. Selecciona la carpeta `Parcial`
4. Haz clic en `Open Project`

### 2. Configurar las Dependencias

1. Haz clic derecho en el proyecto
2. Selecciona `Properties`
3. Ve a `Libraries`
4. Haz clic en `Add JAR/Folder`
5. Agrega los archivos JAR mencionados arriba:
   - `jakarta.servlet-api-5.0.0.jar`
   - `mysql-connector-j-8.0.33.jar`

### 3. Configurar el Servidor

1. Haz clic derecho en el proyecto
2. Selecciona `Properties`
3. Ve a `Run`
4. En `Server`, selecciona tu servidor Apache Tomcat

## Configuración del Proyecto en Eclipse

### 1. Importar el Proyecto

1. Abre Eclipse
2. Ve a `File` > `Import`
3. Selecciona `Existing Maven Projects` o `Existing Projects into Workspace`
4. Selecciona la carpeta `Parcial`
5. Haz clic en `Finish`

### 2. Convertir a Proyecto Web Dinámico (si es necesario)

1. Haz clic derecho en el proyecto
2. Selecciona `Configure` > `Convert to Maven Project` (si no es Maven)
3. O `Properties` > `Project Facets` y habilita `Dynamic Web Module`

### 3. Configurar las Dependencias

1. Crea la carpeta `WEB-INF/lib` en `src/main/webapp/WEB-INF/`
2. Copia los archivos JAR mencionados arriba
3. Haz clic derecho en el proyecto
4. Selecciona `Properties` > `Java Build Path`
5. Ve a la pestaña `Libraries`
6. Haz clic en `Add External JARs`
7. Agrega los archivos JAR

### 4. Configurar el Servidor

1. Ve a `Window` > `Show View` > `Servers`
2. Haz clic derecho en el área de servidores
3. Selecciona `New` > `Server`
4. Selecciona Apache Tomcat
5. Configura la ubicación de Tomcat
6. Arrastra tu proyecto al servidor

## Estructura del Proyecto

```
Parcial/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── controladores/     # Servlets
│   │   │   │   ├── AuthFilter.java
│   │   │   │   ├── ContactServlet.java
│   │   │   │   ├── EncodingFilter.java
│   │   │   │   ├── LoginServlet.java
│   │   │   │   ├── LogoutServlet.java
│   │   │   │   ├── RoleServlet.java
│   │   │   │   └── UserServlet.java
│   │   │   ├── interfaces/       # Interfaces DAO
│   │   │   │   ├── ContactDAO.java
│   │   │   │   ├── RoleDAO.java
│   │   │   │   └── UserDAO.java
│   │   │   └── modelo/           # Modelos e Implementaciones
│   │   │       ├── ContactDAOImpl.java
│   │   │       ├── Contacto.java
│   │   │       ├── DatabaseConnection.java
│   │   │       ├── Permiso.java
│   │   │       ├── RoleDAOImpl.java
│   │   │       ├── Rol.java
│   │   │       ├── UserDAOImpl.java
│   │   │       └── Usuario.java
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   └── web.xml
│   │       ├── dashboard.jsp
│   │       ├── error404.jsp
│   │       ├── error500.jsp
│   │       ├── index.jsp
│   │       ├── login.jsp
│   │       ├── roles.jsp
│   │       ├── usuarios.jsp
│   │       └── styles.css
├── database.sql
├── pom.xml
└── README.md
```

## Ejecutar la Aplicación

### Desde NetBeans

1. Haz clic derecho en el proyecto
2. Selecciona `Run`
3. La aplicación se desplegará en Tomcat
4. Abre tu navegador en: `http://localhost:8080/Parcial`

### Desde Eclipse

1. Haz clic derecho en el proyecto
2. Selecciona `Run As` > `Run on Server`
3. Selecciona tu servidor Tomcat
4. Haz clic en `Finish`
5. Abre tu navegador en: `http://localhost:8080/Parcial`

## Usuarios de Prueba

El script `database.sql` crea los siguientes usuarios por defecto:

| Usuario | Contraseña | Rol |
|---------|-------------|-----|
| superadmin | super123 | SUPERADMIN |
| admin | admin123 | ADMIN |
| usuario | user123 | USER |

**IMPORTANTE**: En producción, utiliza hashing de contraseñas (BCrypt, SHA-256, etc.) en lugar de texto plano.

## Funcionalidades Implementadas

### Gestión de Contactos
- ✅ Listar contactos
- ✅ Crear nuevos contactos
- ✅ Editar contactos existentes
- ✅ Eliminar contactos
- ✅ Buscar contactos por nombre, teléfono, email o grupo

### Gestión de Usuarios
- ✅ Crear nuevos usuarios
- ✅ Listar usuarios
- ✅ Editar usuarios
- ✅ Eliminar usuarios
- ✅ Asignar roles a usuarios (solo SUPERADMIN)

### Gestión de Roles y Permisos
- ✅ Crear nuevos roles
- ✅ Editar roles existentes
- ✅ Eliminar roles (excepto roles básicos)
- ✅ Asignar permisos a roles
- ✅ Eliminar permisos de roles

### Autenticación y Sesión
- ✅ Login de usuarios
- ✅ Registro de nuevos usuarios
- ✅ Logout
- ✅ Mantener sesión activa
- ✅ Identificar usuario autenticado

### Registro de Actividades
- ✅ Login/Logout
- ✅ Crear/Editar/Eliminar contactos
- ✅ Crear/Editar/Eliminar usuarios
- ✅ Asignar roles
- ✅ Crear/Editar/Eliminar roles

## Solución de Problemas Comunes

### Error: "The import jakarta cannot be resolved"

**Causa**: Falta el JAR de Jakarta Servlet API en el classpath.

**Solución**:
1. Descarga `jakarta.servlet-api-5.0.0.jar`
2. Agrégalo a `WEB-INF/lib/`
3. Agrégalo al classpath del proyecto

### Error: "java.sql.SQLException: Access denied for user"

**Causa**: Credenciales de MySQL incorrectas.

**Solución**:
1. Verifica las credenciales en `DatabaseConnection.java`
2. Asegúrate de que el usuario de MySQL tenga permisos en la base de datos

### Error: "java.lang.ClassNotFoundException: com.mysql.cj.jdbc.Driver"

**Causa**: Falta el JAR de MySQL Connector.

**Solución**:
1. Descarga `mysql-connector-j-8.0.33.jar`
2. Agrégalo a `WEB-INF/lib/`
3. Agrégalo al classpath del proyecto

### Error: 404 al acceder a la aplicación

**Causa**: El contexto de la aplicación es incorrecto.

**Solución**:
1. Verifica que el contexto sea correcto (generalmente el nombre del proyecto)
2. Intenta acceder a `http://localhost:8080/Parcial` o `http://localhost:8080/`

## Notas de Seguridad

⚠️ **IMPORTANTE**: Este es un proyecto académico. Para producción:

1. **Contraseñas**: Implementa hashing de contraseñas (BCrypt, Argon2, etc.)
2. **SQL Injection**: Usa PreparedStatement (ya implementado)
3. **XSS**: Escapa el contenido dinámico en JSP
4. **CSRF**: Implementa tokens CSRF para formularios
5. **HTTPS**: Configura SSL/TLS en el servidor
6. **Validación**: Agrega validación más robusta en el lado del servidor

## Soporte

Si encuentras problemas, verifica:
1. Que todos los JAR estén en `WEB-INF/lib/`
2. Que las credenciales de MySQL sean correctas
3. Que el servidor Tomcat esté corriendo
4. Que el puerto 8080 no esté en uso por otra aplicación
