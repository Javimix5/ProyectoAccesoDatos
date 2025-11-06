package Service;

import DB.TransactionManager;
import Model.Cliente;
import Model.Compra;
import Model.DetalleCompra;
import Model.DetalleVenta;
import Model.Producto;
import Model.Venta;
import Dao.ProductoDAO;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

public class TransaccionesService {
    private final TransactionManager txManager;
    private final ProductoDAO productoDAO = new ProductoDAO();

    public TransaccionesService(TransactionManager txManager) {
        this.txManager = txManager;
    }

    public boolean crearVentaTransaccional(Venta venta) throws Exception {
        if (venta == null || venta.getCliente() == null || venta.getDetalles() == null || venta.getDetalles().isEmpty())
            throw new IllegalArgumentException("Venta inválida.");

        return txManager.execute(conn -> {
            String insertVenta = "INSERT INTO ventas (id_cliente, fecha_venta, total_importe) VALUES (?, ?, ?)";
            String insertDetalle = "INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
            try (PreparedStatement psVenta = conn.prepareStatement(insertVenta, Statement.RETURN_GENERATED_KEYS)) {
                psVenta.setInt(1, venta.getCliente().getId());
                psVenta.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                psVenta.setBigDecimal(3, venta.getTotalImporte() != null ? venta.getTotalImporte() : BigDecimal.ZERO);
                psVenta.executeUpdate();
                try (ResultSet rs = psVenta.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("No se obtuvo ID de venta");
                    int ventaId = rs.getInt(1);
                    try (PreparedStatement psDet = conn.prepareStatement(insertDetalle)) {
                        for (DetalleVenta d : venta.getDetalles()) {
                            Producto p = d.getProducto();
                            if (p == null) throw new SQLException("Detalle sin producto");
                            Producto prodBD = productoDAO.buscarPorId(conn, p.getId());
                            if (prodBD == null) throw new SQLException("Producto no existe: " + p.getId());
                            if (prodBD.getStock() < d.getCantidad()) throw new SQLException("Stock insuficiente para producto " + p.getId());

                            BigDecimal precioUnit = d.getPrecioUnitario() != null ? d.getPrecioUnitario() : prodBD.getPrecio();
                            psDet.setInt(1, ventaId);
                            psDet.setInt(2, p.getId());
                            psDet.setInt(3, d.getCantidad());
                            psDet.setBigDecimal(4, precioUnit != null ? precioUnit : BigDecimal.ZERO);
                            psDet.addBatch();

                            int nuevoStock = prodBD.getStock() - d.getCantidad();
                            boolean okUpdate = productoDAO.actualizarStock(conn, p.getId(), nuevoStock);
                            if (!okUpdate) throw new SQLException("Fallo actualizar stock producto " + p.getId());
                        }
                        psDet.executeBatch();
                    }
                }
            }
            return true;
        });
    }

    public boolean crearCompraTransaccional(Compra compra) throws Exception {
        if (compra == null || compra.getProveedor() == null || compra.getDetalles() == null || compra.getDetalles().isEmpty())
            throw new IllegalArgumentException("Compra inválida.");

        return txManager.execute(conn -> {
            String insertCompra = "INSERT INTO compras (proveedor_id, fecha, total) VALUES (?, ?, ?)";
            String insertDetalle = "INSERT INTO detalle_compras (compra_id, producto_id, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
            try (PreparedStatement psCompra = conn.prepareStatement(insertCompra, Statement.RETURN_GENERATED_KEYS)) {
                psCompra.setInt(1, compra.getProveedor().getId());
                psCompra.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                psCompra.setBigDecimal(3, compra.getTotalImporte() != null ? compra.getTotalImporte() : BigDecimal.ZERO);
                psCompra.executeUpdate();
                try (ResultSet rs = psCompra.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("No se obtuvo ID de compra");
                    int compraId = rs.getInt(1);
                    try (PreparedStatement psDet = conn.prepareStatement(insertDetalle)) {
                        for (DetalleCompra d : compra.getDetalles()) {
                            Producto p = d.getProducto();
                            if (p == null) throw new SQLException("Detalle compra sin producto");
                            psDet.setInt(1, compraId);
                            psDet.setInt(2, p.getId());
                            psDet.setInt(3, d.getCantidad());
                            psDet.setBigDecimal(4, d.getPrecioUnitario() != null ? d.getPrecioUnitario() : BigDecimal.ZERO);
                            psDet.addBatch();

                            Producto prodBD = productoDAO.buscarPorId(conn, p.getId());
                            int nuevoStock = (prodBD != null ? prodBD.getStock() : 0) + d.getCantidad();
                            boolean ok = productoDAO.actualizarStock(conn, p.getId(), nuevoStock);
                            if (!ok) throw new SQLException("Fallo actualizar stock compra producto " + p.getId());
                        }
                        psDet.executeBatch();
                    }
                }
            }
            return true;
        });
    }

    public Cliente insertarClienteTransaccional(Cliente c) throws Exception {
        if (c == null) throw new IllegalArgumentException("Cliente nulo.");
        return txManager.execute(conn -> {
            String sql = "INSERT INTO clientes (nombre, apellido1, apellido2, telefono, email, direccion, dni) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, c.getNombre());
                ps.setString(2, c.getApellido1());
                ps.setString(3, c.getApellido2());
                ps.setString(4, c.getTelefono());
                ps.setString(5, c.getEmail());
                ps.setString(6, c.getDireccion());
                ps.setString(7, c.getDni());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) c.setId(rs.getInt(1));
                }
                return c;
            }
        });
    }

    public boolean eliminarClienteTransaccional(int id) throws Exception {
        return txManager.execute(conn -> {
            String countSql = "SELECT COUNT(*) FROM ventas WHERE id_cliente = ?";
            try (PreparedStatement psCount = conn.prepareStatement(countSql)) {
                psCount.setInt(1, id);
                try (ResultSet rs = psCount.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new Service.BusinessException("No se puede eliminar: el cliente tiene ventas asociadas.");
                    }
                }
            }
            String delSql = "DELETE FROM clientes WHERE id = ?";
            try (PreparedStatement psDel = conn.prepareStatement(delSql)) {
                psDel.setInt(1, id);
                return psDel.executeUpdate() > 0;
            }
        });
    }

    public boolean actualizarProductoTransaccional(Producto p) throws Exception {
        if (p == null) throw new IllegalArgumentException("Producto nulo.");
        return txManager.execute(conn -> {
            String sql = "UPDATE productos SET nombre = ?, coleccion = ?, stock = ?, precio = ?, id_proveedor = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, p.getNombre());
                ps.setString(2, p.getColeccion());
                ps.setInt(3, p.getStock());
                ps.setBigDecimal(4, p.getPrecio());
                if (p.getIdProveedor() == null) ps.setNull(5, Types.INTEGER);
                else ps.setInt(5, p.getIdProveedor());
                ps.setInt(6, p.getId());
                return ps.executeUpdate() > 0;
            }
        });
    }
}
