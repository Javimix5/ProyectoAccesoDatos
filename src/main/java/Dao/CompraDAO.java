package Dao;

import DB.ConexionDB;
import Model.Compra;
import Model.DetalleCompra;
import Model.Producto;
import Model.Proveedor;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CompraDAO {
    private final ProductoDAO productoDAO = new ProductoDAO();

    public boolean crearCompra(Compra compra) {
        String insertCompra = "INSERT INTO compras (proveedor_id, fecha, total) VALUES (?, ?, ?)";
        String insertDetalle = "INSERT INTO detalle_compras (compra_id, producto_id, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        try (Connection conn = ConexionDB.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psCompra = conn.prepareStatement(insertCompra, Statement.RETURN_GENERATED_KEYS)) {
                Integer provId = compra.getProveedor() != null ? compra.getProveedor().getId() : null;
                if (provId == null) {
                    conn.rollback();
                    return false;
                }
                psCompra.setInt(1, provId);
                psCompra.setString(2, LocalDateTime.now().format(fmt));
                psCompra.setBigDecimal(3, compra.getTotalImporte() != null ? compra.getTotalImporte() : BigDecimal.ZERO);
                psCompra.executeUpdate();
                try (ResultSet rs = psCompra.getGeneratedKeys()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    int compraId = rs.getInt(1);
                    try (PreparedStatement psDet = conn.prepareStatement(insertDetalle)) {
                        for (DetalleCompra d : compra.getDetalles()) {
                            Producto p = d.getProducto();
                            if (p == null) {
                                conn.rollback();
                                System.err.println("Detalle compra sin producto");
                                return false;
                            }
                            psDet.setInt(1, compraId);
                            psDet.setInt(2, p.getId());
                            psDet.setInt(3, d.getCantidad());
                            psDet.setBigDecimal(4, d.getPrecioUnitario() != null ? d.getPrecioUnitario() : BigDecimal.ZERO);
                            psDet.addBatch();

                            Producto prodBD = productoDAO.buscarPorId(conn, p.getId());
                            int nuevoStock = (prodBD != null ? prodBD.getStock() : 0) + d.getCantidad();
                            boolean ok = productoDAO.actualizarStock(conn, p.getId(), nuevoStock);
                            if (!ok) {
                                conn.rollback();
                                System.err.println("Fallo actualizar stock compra producto " + p.getId());
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
                System.err.println("Error creando compra: " + ex.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error conexión createCompra: " + e.getMessage());
            return false;
        }
    }

    public List<Compra> listarCompras() {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT c.id AS compra_id, c.fecha AS fecha, c.total AS total, " +
                "p.id AS prov_id, p.nombre AS prov_nombre, p.email_contacto AS prov_email " +
                "FROM compras c LEFT JOIN proveedores p ON c.proveedor_id = p.id ORDER BY c.id DESC";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Proveedor prov = null;
                int pid = rs.getInt("prov_id");
                if (!rs.wasNull()) prov = new Proveedor(pid, rs.getString("prov_nombre"), rs.getString("prov_email"));
                Compra comp = new Compra(prov);
                comp.setId(rs.getInt("compra_id"));
                String f = rs.getString("fecha");
                if (f != null && !f.isBlank()) {
                    try {
                        comp.setFecha(LocalDateTime.parse(f));
                    } catch (Exception ex) {
                        try {
                            Timestamp ts = rs.getTimestamp("fecha");
                            if (ts != null) comp.setFecha(ts.toLocalDateTime());
                        } catch (Exception ignored) {
                        }
                    }
                }
                BigDecimal total = rs.getBigDecimal("total");
                comp.setTotalImporte(total != null ? total : BigDecimal.ZERO);
                compras.add(comp);
            }
        } catch (SQLException e) {
            System.err.println("Error listando compras: " + e.getMessage());
        }
        return compras;
    }
}
