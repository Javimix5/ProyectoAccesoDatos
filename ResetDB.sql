USE misterios_de_cartas;

SET FOREIGN_KEY_CHECKS = 0;

-- Limpiar tablas
TRUNCATE TABLE detalle_ventas;
TRUNCATE TABLE detalle_compras;
TRUNCATE TABLE ventas;
TRUNCATE TABLE compras;
TRUNCATE TABLE productos;
TRUNCATE TABLE clientes;
TRUNCATE TABLE proveedores;

SET FOREIGN_KEY_CHECKS = 1;

-- 1. Insertar Proveedores (3 originales)
INSERT INTO proveedores (id, nombre, email_contacto) VALUES
(1, 'Distribuidora Mágica', 'ventas@distrimagic.com'),
(2, 'El Rincón del Coleccionista', 'contacto@rinconcoleccion.es'),
(3, 'Importaciones TCG Asia', 'support@tcgasian.com');

-- 2. Insertar Clientes (3 originales)
INSERT INTO clientes (id, nombre, apellido1, apellido2, telefono, email, direccion, dni) VALUES
(1, 'Laura', 'García', 'Pérez', '611223344', 'laura.garcia@email.com', 'Calle Mayor 1, Madrid', '12345678A'),
(2, 'Carlos', 'Martínez', 'Sánchez', '655667788', 'carlos.martinez@email.com', 'Avenida de la Luz 23, Barcelona', '87654321B'),
(3, 'Ana', 'López', 'Jiménez', '699887766', 'ana.lopez@email.com', 'Plaza Nueva 5, Sevilla', '11223344C');

-- 3. Insertar Productos (10 originales)
INSERT INTO productos (id, nombre, coleccion, stock, precio, id_proveedor) VALUES
(1, 'Dragón de Ojos Azules', 'Leyendas del Duelo', 15, 25.50, 1),
(2, 'Mago Oscuro', 'Invasión del Caos', 20, 15.00, 1),
(3, 'Charizard VMAX', 'Voltaje Vívido', 10, 120.00, 2),
(4, 'Pikachu Surfista V', 'Celebraciones', 30, 8.75, 2),
(5, 'Black Lotus', 'Alpha', 1, 15000.00, 3),
(6, 'Llanura (Ilustración completa)', 'Zendikar Rising', 50, 2.50, 2),
(7, 'Sol Ring (Commander)', 'Commander 2021', 40, 5.00, 1),
(8, 'Omnath, Locus of Creation', 'Zendikar Rising', 5, 22.00, 2),
(9, 'Agumon - Vínculo de la Amistad', 'BT-06', 12, 45.00, 3),
(10, 'Beelzemon Blast Mode', 'BT-02', 8, 35.50, 3);

-- 4. Insertar Ventas (3 originales)
-- Forzamos los IDs (num_factura) para asegurar la integridad con los detalles
INSERT INTO ventas (num_factura, id_cliente, total_importe, fecha_venta) VALUES
(1, 1, 55.50, NOW()), -- Venta 1: Laura (25.50 + 30.00)
(2, 2, 120.00, NOW()), -- Venta 2: Carlos (120.00)
(3, 1, 17.50, NOW());  -- Venta 3: Laura (12.50 + 5.00)

-- 5. Insertar Detalle de Ventas (Vinculados a las ventas anteriores)
INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario) VALUES
-- Detalles Venta 1
(1, 1, 1, 25.50), -- 1 Dragón
(1, 2, 2, 15.00), -- 2 Magos
-- Detalles Venta 2
(2, 3, 1, 120.00), -- 1 Charizard
-- Detalles Venta 3
(3, 6, 5, 2.50),  -- 5 Llanuras
(3, 7, 1, 5.00);  -- 1 Sol Ring

-- 6. Insertar Compras (Datos de ejemplo extra para probar la API de Compras)
INSERT INTO compras (id, proveedor_id, fecha, total) VALUES
(1, 1, NOW(), 100.00);

-- 7. Insertar Detalle de Compras
INSERT INTO detalle_compras (compra_id, producto_id, cantidad, precio_unitario) VALUES
(1, 1, 4, 25.00);

SELECT 'Base de datos restaurada con datos originales correctamente' AS Status;