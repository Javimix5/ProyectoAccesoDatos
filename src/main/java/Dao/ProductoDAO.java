package Dao;

import DB.ConexionDB;
import Model.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public Producto buscarPorId(Connection conn, int id) throws SQLException {
        String sql = "SELECT id, nombre, coleccion, stock, precio, id_proveedor FROM productos WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Producto p = new Producto();
                    p.setId(rs.getInt("id"));
                    p.setNombre(rs.getString("nombre"));
                    p.setColeccion(rs.getString("coleccion"));
                    p.setStock(rs.getInt("stock"));
                    p.setPrecio(rs.getBigDecimal("precio"));
                    Object provObj = rs.getObject("id_proveedor");
                    p.setIdProveedor(provObj != null ? rs.getInt("id_proveedor") : null);
                    return p;
                }
            }
        }
        return null;
    }

    public Producto buscarPorId(int id) {
        try (Connection conn = ConexionDB.getConnection()) {
            return buscarPorId(conn, id);
        } catch (SQLException e) {
            System.err.println("Error buscarPorId Producto: " + e.getMessage());
            return null;
        }
    }

    public List<Producto> obtenerInventario() {
        List<Producto> list = new ArrayList<>();
        String sql = "SELECT id, nombre, coleccion, stock, precio, id_proveedor FROM productos ORDER BY id";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Producto p = new Producto();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setColeccion(rs.getString("coleccion"));
                p.setStock(rs.getInt("stock"));
                p.setPrecio(rs.getBigDecimal("precio"));
                Object provObj = rs.getObject("id_proveedor");
                p.setIdProveedor(provObj != null ? rs.getInt("id_proveedor") : null);
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerInventario: " + e.getMessage());
        }
        return list;
    }

    public List<Producto> obtenerPorProveedor(int idProveedor) {
        List<Producto> list = new ArrayList<>();
        String sql = "SELECT id, nombre, coleccion, stock, precio, id_proveedor FROM productos WHERE id_proveedor = ? ORDER BY id";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto();
                    p.setId(rs.getInt("id"));
                    p.setNombre(rs.getString("nombre"));
                    p.setColeccion(rs.getString("coleccion"));
                    p.setStock(rs.getInt("stock"));
                    p.setPrecio(rs.getBigDecimal("precio"));
                    Object provObj = rs.getObject("id_proveedor");
                    p.setIdProveedor(provObj != null ? rs.getInt("id_proveedor") : null);
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerPorProveedor: " + e.getMessage());
        }
        return list;
    }

    public boolean actualizarStock(int idProducto, int nuevoStock) {
        try (Connection conn = ConexionDB.getConnection()) {
            return actualizarStock(conn, idProducto, nuevoStock);
        } catch (SQLException e) {
            System.err.println("Error actualizarStock (conn): " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarStock(Connection conn, int idProducto, int nuevoStock) throws SQLException {
        String sql = "UPDATE productos SET stock = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idProducto);
            return ps.executeUpdate() > 0;
        }
    }
}
