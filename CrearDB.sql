CREATE DATABASE IF NOT EXISTS misterios_de_cartas;
USE misterios_de_cartas;

-- 1. Tabla de Proveedores
CREATE TABLE proveedores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email_contacto VARCHAR(100)
);

-- 2. Tabla de Clientes
CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido1 VARCHAR(50) NOT NULL,
    apellido2 VARCHAR(50),
    telefono VARCHAR(15),
    email VARCHAR(100) UNIQUE NOT NULL,
    direccion VARCHAR(255),
    dni VARCHAR(10) UNIQUE NOT NULL
);

-- 3. Tabla de Productos
CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    coleccion VARCHAR(100),
    stock INT NOT NULL DEFAULT 0,
    precio DECIMAL(10, 2) NOT NULL,
    id_proveedor INT,
    FOREIGN KEY (id_proveedor) REFERENCES proveedores(id)
);

-- 4. Tabla de Ventas (o Facturas)
CREATE TABLE ventas (
    num_factura INT AUTO_INCREMENT PRIMARY KEY,
    fecha_venta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_cliente INT,
    total_importe DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id)
);

-- 5. Tabla de Detalle de Ventas (Tabla intermedia)
CREATE TABLE detalle_ventas (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_venta INT,
    id_producto INT,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_venta) REFERENCES ventas(num_factura),
    FOREIGN KEY (id_producto) REFERENCES productos(id)
);
-- DATOS DE EJEMPLO PARA GESTIONAR --

-- Insertar 3 Proveedores
INSERT INTO proveedores (nombre, email_contacto) VALUES
('Distribuidora Mágica', 'ventas@distrimagic.com'),
('El Rincón del Coleccionista', 'contacto@rinconcoleccion.es'),
('Importaciones TCG Asia', 'support@tcgasian.com');

-- Insertar 3 Clientes
INSERT INTO clientes (nombre, apellido1, apellido2, telefono, email, direccion, dni) VALUES
('Laura', 'García', 'Pérez', '611223344', 'laura.garcia@email.com', 'Calle Mayor 1, Madrid', '12345678A'),
('Carlos', 'Martínez', 'Sánchez', '655667788', 'carlos.martinez@email.com', 'Avenida de la Luz 23, Barcelona', '87654321B'),
('Ana', 'López', 'Jiménez', '699887766', 'ana.lopez@email.com', 'Plaza Nueva 5, Sevilla', '11223344C');

-- Insertar 10 Productos
INSERT INTO productos (nombre, coleccion, stock, precio, id_proveedor) VALUES
('Dragón de Ojos Azules', 'Leyendas del Duelo', 15, 25.50, 1),
('Mago Oscuro', 'Invasión del Caos', 20, 15.00, 1),
('Charizard VMAX', 'Voltaje Vívido', 10, 120.00, 2),
('Pikachu Surfista V', 'Celebraciones', 30, 8.75, 2),
('Black Lotus', 'Alpha', 1, 15000.00, 3),
('Llanura (Ilustración completa)', 'Zendikar Rising', 50, 2.50, 2),
('Sol Ring (Commander)', 'Commander 2021', 40, 5.00, 1),
('Omnath, Locus of Creation', 'Zendikar Rising', 5, 22.00, 2),
('Agumon - Vínculo de la Amistad', 'BT-06', 12, 45.00, 3),
('Beelzemon Blast Mode', 'BT-02', 8, 35.50, 3);

-- Insertar 3 Ventas
INSERT INTO ventas (id_cliente, total_importe) VALUES
(1, 0), -- Venta para Laura García
(2, 0), -- Venta para Carlos Martínez
(1, 0); -- Otra venta para Laura García

-- Insertar detalles para la Venta 1 (Laura)
-- Compra 1 Dragón de Ojos Azules (25.50) y 2 Magos Oscuros (15.00 * 2 = 30.00)
INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario) VALUES
(1, 1, 1, 25.50),
(1, 2, 2, 15.00);
UPDATE ventas SET total_importe = (25.50 + 30.00) WHERE num_factura = 1;

-- Insertar detalles para la Venta 2 (Carlos)
-- Compra 1 Charizard VMAX (120.00)
INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario) VALUES
(2, 3, 1, 120.00);
UPDATE ventas SET total_importe = 120.00 WHERE num_factura = 2;

-- Insertar detalles para la Venta 3 (Laura)
-- Compra 5 Llanuras (2.50 * 5 = 12.50) y 1 Sol Ring (5.00)
INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario) VALUES
(3, 6, 5, 2.50),
(3, 7, 1, 5.00);
UPDATE ventas SET total_importe = (12.50 + 5.00) WHERE num_factura = 3;

-- 6. Tabla de Compras 
CREATE TABLE IF NOT EXISTS compras (
    id INT AUTO_INCREMENT PRIMARY KEY,
    proveedor_id INTEGER,
    fecha TEXT,
    total NUMERIC,
    FOREIGN KEY (proveedor_id)
        REFERENCES proveedores (id)
);

-- 7. Tabla Detalle de Compras
CREATE TABLE IF NOT EXISTS detalle_compras (
    id INT AUTO_INCREMENT PRIMARY KEY,
    compra_id INTEGER,
    producto_id INTEGER,
    cantidad INTEGER,
    precio_unitario NUMERIC,
    FOREIGN KEY (compra_id)
        REFERENCES compras (id)
        ON DELETE CASCADE,
    FOREIGN KEY (producto_id)
        REFERENCES productos (id)
);

