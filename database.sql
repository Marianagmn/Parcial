-- Script SQL para crear la base de datos de Agenda de Contactos
-- Compatible con MySQL

CREATE DATABASE IF NOT EXISTS agenda_contactos;
USE agenda_contactos;

-- Tabla de roles
CREATE TABLE IF NOT EXISTS roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

-- Tabla de permisos
CREATE TABLE IF NOT EXISTS permisos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

-- Tabla de roles_permisos (relación muchos a muchos)
CREATE TABLE IF NOT EXISTS roles_permisos (
    rol_id INT,
    permiso_id INT,
    PRIMARY KEY (rol_id, permiso_id),
    FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permiso_id) REFERENCES permisos(id) ON DELETE CASCADE
);

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    rol_id INT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (rol_id) REFERENCES roles(id)
);

-- Tabla de contactos
CREATE TABLE IF NOT EXISTS contactos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    grupo VARCHAR(50) DEFAULT 'Otros',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabla de actividades (log de actividades)
CREATE TABLE IF NOT EXISTS actividades (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT,
    accion VARCHAR(100) NOT NULL,
    descripcion TEXT,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

-- Insertar datos iniciales de roles
INSERT INTO roles (nombre, descripcion) VALUES 
('ADMIN', 'Administrador con acceso completo'),
('USER', 'Usuario estándar con acceso limitado');

-- Insertar datos iniciales de permisos
INSERT INTO permisos (nombre, descripcion) VALUES 
('CONTACTO_VER', 'Ver contactos'),
('CONTACTO_CREAR', 'Crear nuevos contactos'),
('CONTACTO_EDITAR', 'Editar contactos existentes'),
('CONTACTO_ELIMINAR', 'Eliminar contactos'),
('USUARIO_VER', 'Ver usuarios'),
('USUARIO_CREAR', 'Crear nuevos usuarios'),
('USUARIO_EDITAR', 'Editar usuarios'),
('USUARIO_ELIMINAR', 'Eliminar usuarios'),
('USUARIO_GESTIONAR_ROLES', 'Asignar roles y permisos a usuarios');

-- Asignar permisos al rol ADMIN (todos los permisos)
INSERT INTO roles_permisos (rol_id, permiso_id)
SELECT 1, id FROM permisos;

-- Asignar permisos al rol USER (solo permisos de contactos)
INSERT INTO roles_permisos (rol_id, permiso_id)
SELECT 2, id FROM permisos WHERE nombre IN ('CONTACTO_VER', 'CONTACTO_CREAR', 'CONTACTO_EDITAR', 'CONTACTO_ELIMINAR');

-- Insertar usuario administrador por defecto (password: admin123)
-- Nota: En producción, usar hash real de contraseña
INSERT INTO usuarios (nombre, username, password, email, rol_id) VALUES 
('Administrador', 'admin', 'admin123', 'admin@agenda.com', 1);

-- Insertar usuario de prueba (password: user123)
INSERT INTO usuarios (nombre, username, password, email, rol_id) VALUES 
('Usuario Prueba', 'usuario', 'user123', 'usuario@agenda.com', 2);
