-- ═══════════════════════════════════════════════════════════════════════════
-- Script DDL: Base de datos de la clínica veterinaria Huellas Sanas
-- Motor:  MySQL 8.x
-- Charset: utf8mb4 (soporte completo de emojis y caracteres especiales)
-- ═══════════════════════════════════════════════════════════════════════════

CREATE DATABASE IF NOT EXISTS huellassanas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE huellassanas;

-- ─── Tabla: usuarios ─────────────────────────────────────────────────────────
-- Almacena las credenciales de acceso al sistema.
-- IMPORTANTE: password_hash contiene SHA-256 en hexadecimal (64 chars).
CREATE TABLE IF NOT EXISTS usuarios (
    id            INT          NOT NULL AUTO_INCREMENT,
    nombre        VARCHAR(100) NOT NULL,
    apellido      VARCHAR(100) NOT NULL,
    correo        VARCHAR(150)          UNIQUE,
    telefono      VARCHAR(20),
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash CHAR(64)     NOT NULL COMMENT 'SHA-256 hex',
    rol           ENUM('ADMINISTRADOR','RECEPCIONISTA','VETERINARIO') NOT NULL,
    activo        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- ─── Tabla: clientes ─────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS clientes (
    id         INT          NOT NULL AUTO_INCREMENT,
    nombre     VARCHAR(100) NOT NULL,
    apellido   VARCHAR(100) NOT NULL,
    correo     VARCHAR(150)          UNIQUE,
    telefono   VARCHAR(20),
    dni        VARCHAR(20)  NOT NULL UNIQUE,
    direccion  VARCHAR(255),
    created_at TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- ─── Tabla: veterinarios ─────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS veterinarios (
    id            INT          NOT NULL AUTO_INCREMENT,
    nombre        VARCHAR(100) NOT NULL,
    apellido      VARCHAR(100) NOT NULL,
    correo        VARCHAR(150)          UNIQUE,
    telefono      VARCHAR(20),
    licencia      VARCHAR(50)  NOT NULL UNIQUE,
    especialidad  VARCHAR(100),
    disponible    BOOLEAN      NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- ─── Tabla: mascotas ─────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS mascotas (
    id               INT          NOT NULL AUTO_INCREMENT,
    nombre           VARCHAR(100) NOT NULL,
    especie          ENUM('PERRO','GATO','AVE','REPTIL','ROEDOR','OTRO') NOT NULL,
    raza             VARCHAR(100),
    fecha_nacimiento DATE,
    peso             DECIMAL(6,2)          COMMENT 'Peso en kilogramos',
    cliente_id       INT          NOT NULL,
    created_at       TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_mascota_cliente
        FOREIGN KEY (cliente_id) REFERENCES clientes(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ─── Tabla: citas ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS citas (
    id              INT  NOT NULL AUTO_INCREMENT,
    mascota_id      INT  NOT NULL,
    veterinario_id  INT  NOT NULL,
    fecha_hora      DATETIME NOT NULL,
    motivo          TEXT,
    diagnostico     TEXT,
    estado          ENUM('PENDIENTE','CONFIRMADA','EN_PROGRESO','COMPLETADA','CANCELADA')
                    NOT NULL DEFAULT 'PENDIENTE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_cita_mascota
        FOREIGN KEY (mascota_id) REFERENCES mascotas(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_cita_veterinario
        FOREIGN KEY (veterinario_id) REFERENCES veterinarios(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ─── Datos de prueba ──────────────────────────────────────────────────────────
-- Contraseña: 'admin123' → SHA-256 hex
-- Puedes verificar con: SELECT SHA2('admin123', 256);
INSERT INTO usuarios (nombre, apellido, correo, username, password_hash, rol)
VALUES ('Admin', 'Sistema', 'admin@huellassanas.com', 'admin',
        '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
        'ADMINISTRADOR')
ON DUPLICATE KEY UPDATE id=id;

INSERT INTO veterinarios (nombre, apellido, correo, telefono, licencia, especialidad)
VALUES ('Laura', 'Gómez', 'lgomez@huellassanas.com', '3001112233', 'VET-001', 'Medicina General')
ON DUPLICATE KEY UPDATE id=id;

-- ─── Tabla: historial_clinico ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS historial_clinico (
    id              INT          NOT NULL AUTO_INCREMENT,
    mascota_id      INT          NOT NULL,
    fecha_hora      DATETIME     NOT NULL,
    diagnostico     TEXT         NOT NULL,
    tratamiento     TEXT,
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_historial_mascota
        FOREIGN KEY (mascota_id) REFERENCES mascotas(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;
