package Dao;

import DB.ConexionDB;
import Model.Cliente;
import Model.DetalleVenta;
import Model.Producto;
import Model.Venta;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {
    private final ProductoDAO productoDAO = new ProductoDAO();

    public boolean crearVenta(Venta venta) {
        String insertVenta = "INSERT INTO ventas (id_cliente, fecha_venta, total_importe) VALUES (?, ?, ?)";
        String insertDetalle = "INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psVenta = conn.prepareStatement(insertVenta, Statement.RETURN_GENERATED_KEYS)) {
                Integer clienteId = venta.getCliente() != null ? venta.getCliente().getId() : null;
                if (clienteId == null) {
                    conn.rollback();
                    return false;
                }
                psVenta.setInt(1, clienteId);
                psVenta.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                psVenta.setBigDecimal(3, venta.getTotalImporte() != null ? venta.getTotalImporte() : BigDecimal.ZERO);
                psVenta.executeUpdate();
                try (ResultSet rsKeys = psVenta.getGeneratedKeys()) {
                    if (!rsKeys.next()) {
                        conn.rollback();
                        return false;
                    }
                    int ventaId = rsKeys.getInt(1);
                    try (PreparedStatement psDet = conn.prepareStatement(insertDetalle)) {
                        for (DetalleVenta d : venta.getDetalles()) {
                            Producto p = d.getProducto();
                            if (p == null) {
                                conn.rollback();
                                System.err.println("Detalle sin producto");
                                return false;
                            }
                            Producto prodBD = productoDAO.buscarPorId(conn, p.getId());
                            if (prodBD == null) {
                                conn.rollback();
                                System.err.println("Producto no existe: " + p.getId());
                                return false;
                            }
                            if (prodBD.getStock() < d.getCantidad()) {
                                conn.rollback();
                                System.err.println("Stock insuficiente para producto " + p.getId());
                                return false;
                            }
                            psDet.setInt(1, ventaId);
                            psDet.setInt(2, p.getId());
                            psDet.setInt(3, d.getCantidad());
                            psDet.setBigDecimal(4, d.getPrecioUnitario() != null ? d.getPrecioUnitario() : prodBD.getPrecio());
                            psDet.addBatch();

                            int nuevoStock = prodBD.getStock() - d.getCantidad();
                            boolean ok = productoDAO.actualizarStock(conn, p.getId(), nuevoStock);
                            if (!ok) {
                                conn.rollback();
                                System.err.println("Fallo actualizar stock producto " + p.getId());
                                return false;
                            }
                        }
                        psDet.executeBatch();
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                System.err.println("Error creando venta: " + ex.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error conexión crearVenta: " + e.getMessage());
            return false;
        }
    }

    public List<Venta> listarVentas() {
        String sql = "SELECT v.num_factura AS id, v.fecha_venta AS fecha, v.total_importe AS total, " +
                "c.id AS cliente_id, c.nombre AS cliente_nombre, c.apellido1 AS cliente_apellido1, c.dni AS cliente_dni " +
                "FROM ventas v LEFT JOIN clientes c ON v.id_cliente = c.id ORDER BY v.num_factura DESC";
        List<Venta> ventas = new ArrayList<>();
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Cliente cliente = null;
                int cid = rs.getInt("cliente_id");
                if (!rs.wasNull()) {
                    cliente = new Cliente(cid,
                            rs.getString("cliente_nombre"),
                            rs.getString("cliente_apellido1"),
                            "", "", "", "",
                            rs.getString("cliente_dni"));
                }
                Venta v = new Venta(cliente);
                v.setNumFactura(rs.getInt("id"));
                Timestamp ts = rs.getTimestamp("fecha");
                if (ts != null) v.setFechaVenta(ts.toLocalDateTime());
                v.setTotalImporte(rs.getBigDecimal("total"));
                ventas.add(v);
            }
        } catch (SQLException e) {
            System.err.println("Error listando ventas: " + e.getMessage());
        }
        return ventas;
    }
}
