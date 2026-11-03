CREATE DATABASE IF NOT EXISTS cooperativa_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cooperativa_db;

-- 1. Tabla de Roles (RBAC - 6 Roles)
CREATE TABLE roles (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(200) NOT NULL
) ENGINE=InnoDB;

-- 2. Tabla de Usuarios
CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(120) NOT NULL,
    id_rol INT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
) ENGINE=InnoDB;

-- 3. Tabla de Socios
CREATE TABLE socios (
    id_socio INT AUTO_INCREMENT PRIMARY KEY,
    codigo_socio VARCHAR(20) NOT NULL UNIQUE,
    dui VARCHAR(10) NOT NULL UNIQUE,
    nit VARCHAR(17) UNIQUE,
    nombres VARCHAR(80) NOT NULL,
    apellidos VARCHAR(80) NOT NULL,
    telefono VARCHAR(15) NOT NULL,
    direccion VARCHAR(250) NOT NULL,
    salario_mensual DECIMAL(12,2) NOT NULL CHECK (salario_mensual >= 0),
    aportacion_acumulada DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    estado ENUM('ACTIVO', 'INACTIVO', 'BLOQUEADO') NOT NULL DEFAULT 'ACTIVO',
    fecha_ingreso DATE NOT NULL
) ENGINE=InnoDB;

-- 4. Solicitudes de Crédito (Dictamen Comité y Reglas 3x Salario / 60% Hipoteca)
CREATE TABLE solicitudes_credito (
    id_solicitud INT AUTO_INCREMENT PRIMARY KEY,
    id_socio INT NOT NULL,
    tipo_garantia ENUM('FIDUCIARIA_SALARIO', 'HIPOTECARIA', 'PRENDARIA') NOT NULL,
    monto_solicitado DECIMAL(12,2) NOT NULL CHECK (monto_solicitado > 0),
    valor_valuo_hipoteca DECIMAL(12,2) DEFAULT 0.00,
    plazo_meses INT NOT NULL CHECK (plazo_meses > 0),
    tasa_interes_anual DECIMAL(5,4) NOT NULL,
    estado ENUM('PENDIENTE', 'APROBADA', 'RECHAZADA') NOT NULL DEFAULT 'PENDIENTE',
    observaciones_comite VARCHAR(500),
    id_usuario_evaluador INT,
    fecha_solicitud DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_dictamen DATETIME,
    CONSTRAINT fk_solicitud_socio FOREIGN KEY (id_socio) REFERENCES socios(id_socio),
    CONSTRAINT fk_solicitud_evaluador FOREIGN KEY (id_usuario_evaluador) REFERENCES usuarios(id_usuario)
) ENGINE=InnoDB;

-- 5. Créditos Activos
CREATE TABLE creditos (
    id_credito INT AUTO_INCREMENT PRIMARY KEY,
    numero_credito VARCHAR(25) NOT NULL UNIQUE,
    id_solicitud INT NOT NULL UNIQUE,
    id_socio INT NOT NULL,
    monto_otorgado DECIMAL(12,2) NOT NULL,
    saldo_capital DECIMAL(12,2) NOT NULL,
    tasa_interes_anual DECIMAL(5,4) NOT NULL,
    tasa_mora_anual DECIMAL(5,4) NOT NULL DEFAULT 0.0500,
    plazo_meses INT NOT NULL,
    cuota_nivelada DECIMAL(12,2) NOT NULL,
    fecha_desembolso DATE NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    categoria_riesgo_ncb022 ENUM('A1', 'A2', 'B', 'C1', 'C2', 'D1', 'D2', 'E') NOT NULL DEFAULT 'A1',
    estado ENUM('VIGENTE', 'VENCIDO', 'CANCELADO', 'CASTIGADO') NOT NULL DEFAULT 'VIGENTE',
    CONSTRAINT fk_credito_solicitud FOREIGN KEY (id_solicitud) REFERENCES solicitudes_credito(id_solicitud),
    CONSTRAINT fk_credito_socio FOREIGN KEY (id_socio) REFERENCES socios(id_socio)
) ENGINE=InnoDB;

-- 6. Plan de Pagos / Cuotas de Amortización
CREATE TABLE cuotas_amortizacion (
    id_cuota INT AUTO_INCREMENT PRIMARY KEY,
    id_credito INT NOT NULL,
    numero_cuota INT NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    capital_programado DECIMAL(12,2) NOT NULL,
    interes_programado DECIMAL(12,2) NOT NULL,
    gastos_administrativos DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    mora_acumulada DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    capital_pagado DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    interes_pagado DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    mora_pagada DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    gastos_pagados DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    estado ENUM('PENDIENTE', 'PARCIAL', 'PAGADA', 'VENCIDA') NOT NULL DEFAULT 'PENDIENTE',
    CONSTRAINT fk_cuota_credito FOREIGN KEY (id_credito) REFERENCES creditos(id_credito)
) ENGINE=InnoDB;

-- 7. Transacciones de Caja (Ventanilla y Control AML/UIF)
CREATE TABLE transacciones_caja (
    id_transaccion INT AUTO_INCREMENT PRIMARY KEY,
    codigo_recibo VARCHAR(30) NOT NULL UNIQUE,
    id_credito INT NOT NULL,
    id_socio INT NOT NULL,
    id_usuario_cajero INT NOT NULL,
    monto_total_recibido DECIMAL(12,2) NOT NULL CHECK (monto_total_recibido > 0),
    requiere_declaracion_uif BOOLEAN NOT NULL DEFAULT FALSE,
    origen_fondos_uif VARCHAR(255),
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tx_credito FOREIGN KEY (id_credito) REFERENCES creditos(id_credito),
    CONSTRAINT fk_tx_socio FOREIGN KEY (id_socio) REFERENCES socios(id_socio),
    CONSTRAINT fk_tx_cajero FOREIGN KEY (id_usuario_cajero) REFERENCES usuarios(id_usuario)
) ENGINE=InnoDB;

-- 8. Detalle de Cascada de Pago (Prelación: 1. Gastos -> 2. Mora -> 3. Interés -> 4. Capital)
CREATE TABLE detalle_cascada_pago (
    id_detalle_cascada INT AUTO_INCREMENT PRIMARY KEY,
    id_transaccion INT NOT NULL,
    id_cuota INT NOT NULL,
    abono_gastos DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    abono_mora DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    abono_interes DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    abono_capital DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_cascada_tx FOREIGN KEY (id_transaccion) REFERENCES transacciones_caja(id_transaccion),
    CONSTRAINT fk_cascada_cuota FOREIGN KEY (id_cuota) REFERENCES cuotas_amortizacion(id_cuota)
) ENGINE=InnoDB;

-- 9. Catálogo de Cuentas Contables (INSAFOCOOP)
CREATE TABLE cuentas_contables (
    id_cuenta INT AUTO_INCREMENT PRIMARY KEY,
    codigo_cuenta VARCHAR(20) NOT NULL UNIQUE,
    nombre_cuenta VARCHAR(120) NOT NULL,
    tipo_cuenta ENUM('ACTIVO', 'PASIVO', 'PATRIMONIO', 'INGRESO', 'GASTO', 'ORDEN') NOT NULL,
    naturaleza ENUM('DEUDORA', 'ACREEDORA') NOT NULL,
    acepta_movimiento BOOLEAN NOT NULL DEFAULT TRUE,
    saldo_actual DECIMAL(14,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB;

-- 10. Libro Diario / Partidas Contables
CREATE TABLE partidas_diario (
    id_partida INT AUTO_INCREMENT PRIMARY KEY,
    numero_partida VARCHAR(25) NOT NULL UNIQUE,
    fecha_contable DATE NOT NULL,
    concepto VARCHAR(300) NOT NULL,
    total_debe DECIMAL(14,2) NOT NULL,
    total_haber DECIMAL(14,2) NOT NULL,
    estado ENUM('MAYORIZADA', 'CERRADA_INMUTABLE') NOT NULL DEFAULT 'MAYORIZADA',
    id_usuario_registro INT NOT NULL,
    CONSTRAINT chk_doble_partida CHECK (total_debe = total_haber),
    CONSTRAINT fk_partida_usuario FOREIGN KEY (id_usuario_registro) REFERENCES usuarios(id_usuario)
) ENGINE=InnoDB;

-- 11. Detalle de Partida Contable
CREATE TABLE detalle_partida (
    id_detalle_partida INT AUTO_INCREMENT PRIMARY KEY,
    id_partida INT NOT NULL,
    id_cuenta INT NOT NULL,
    debe DECIMAL(14,2) NOT NULL DEFAULT 0.00 CHECK (debe >= 0),
    haber DECIMAL(14,2) NOT NULL DEFAULT 0.00 CHECK (haber >= 0),
    CONSTRAINT fk_det_partida FOREIGN KEY (id_partida) REFERENCES partidas_diario(id_partida),
    CONSTRAINT fk_det_cuenta FOREIGN KEY (id_cuenta) REFERENCES cuentas_contables(id_cuenta)
) ENGINE=InnoDB;

-- Semilla de los 6 Roles del Sistema
INSERT INTO roles (nombre_rol, descripcion) VALUES
('ADMIN_SISTEMA', 'Administración total de usuarios, parámetros y configuración'),
('OFICIAL_CREDITO', 'Gestión de socios, simulación y creación de solicitudes de crédito'),
('COMITE_CREDITO', 'Evaluación, dictamen, aprobación o rechazo de solicitudes'),
('CAJERO', 'Operaciones de ventanilla, cobros con cascada de pago y alertas UIF'),
('CONTADOR', 'Gestión de partidas de diario, catálogo INSAFOCOOP y cierre NCB-022'),
('AUDITOR', 'Consulta de bitácoras, inmutabilidad contable y reportes UIF/AML');