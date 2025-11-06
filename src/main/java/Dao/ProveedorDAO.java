package Dao;

import DB.ConexionDB;
import Model.Proveedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    public Proveedor buscarPorId(int id) {
        String sql = "SELECT id, nombre, email_contacto FROM proveedores WHERE id = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Proveedor(rs.getInt("id"), rs.getString("nombre"), rs.getString("email_contacto"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error buscarPorId Proveedor: " + e.getMessage());
        }
        return null;
    }

    public List<Proveedor> obtenerTodos() {
        List<Proveedor> list = new ArrayList<>();
        String sql = "SELECT id, nombre, email_contacto FROM proveedores ORDER BY id";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Proveedor(rs.getInt("id"), rs.getString("nombre"), rs.getString("email_contacto")));
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerTodos Proveedores: " + e.getMessage());
        }
        return list;
    }
}
