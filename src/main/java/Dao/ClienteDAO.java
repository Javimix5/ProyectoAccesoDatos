package Dao;

import DB.ConexionDB;
import Model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public boolean insertar(Cliente c) {
        String sql = "INSERT INTO clientes (nombre, apellido1, apellido2, telefono, email, direccion, dni) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
            return true;
        } catch (SQLException e) {
            System.err.println("Error insertar Cliente: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Cliente c) {
        String sql = "UPDATE clientes SET nombre=?, apellido1=?, apellido2=?, telefono=?, email=?, direccion=?, dni=? WHERE id=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido1());
            ps.setString(3, c.getApellido2());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getEmail());
            ps.setString(6, c.getDireccion());
            ps.setString(7, c.getDni());
            ps.setInt(8, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizar Cliente: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminar Cliente: " + e.getMessage());
            return false;
        }
    }

    public Cliente buscarPorId(int id) {
        String sql = "SELECT id, nombre, apellido1, apellido2, telefono, email, direccion, dni FROM clientes WHERE id = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("apellido1"),
                            rs.getString("apellido2"),
                            rs.getString("telefono"),
                            rs.getString("email"),
                            rs.getString("direccion"),
                            rs.getString("dni")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error buscarPorId Cliente: " + e.getMessage());
        }
        return null;
    }

    public Cliente buscarPorDNI(String dni) {
        if (dni == null) return null;
        String sql = "SELECT id, nombre, apellido1, apellido2, telefono, email, direccion, dni " +
                "FROM clientes WHERE LOWER(TRIM(dni)) = LOWER(TRIM(?))";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dni.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("apellido1"),
                            rs.getString("apellido2"),
                            rs.getString("telefono"),
                            rs.getString("email"),
                            rs.getString("direccion"),
                            rs.getString("dni")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error buscarPorDNI Cliente: " + e.getMessage());
        }
        return null;
    }

    public List<Cliente> obtenerTodos() {
        List<Cliente> list = new ArrayList<>();
        String sql = "SELECT id, nombre, apellido1, apellido2, telefono, email, direccion, dni FROM clientes ORDER BY id";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Cliente c = new Cliente(rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido1"),
                        rs.getString("apellido2"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getString("direccion"),
                        rs.getString("dni"));
                list.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error obtenerTodos Clientes: " + e.getMessage());
        }
        return list;
    }

    public boolean eliminarYResequenciar(int id) {
        Connection conn = null;
        String deleteSql = "DELETE FROM clientes WHERE id = ?";
        String updateClientesSql = "UPDATE clientes SET id = id - 1 WHERE id > ?";
        String updateVentasSql = "UPDATE ventas SET cliente_id = cliente_id - 1 WHERE cliente_id > ?";

        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psDel = conn.prepareStatement(deleteSql)) {
                psDel.setInt(1, id);
                int deleted = psDel.executeUpdate();
                if (deleted == 0) {
                    conn.rollback();
                    return false;
                }
            }

            try (PreparedStatement psUpdCli = conn.prepareStatement(updateClientesSql)) {
                psUpdCli.setInt(1, id);
                psUpdCli.executeUpdate();
            }

            try (PreparedStatement psUpdVen = conn.prepareStatement(updateVentasSql)) {
                psUpdVen.setInt(1, id);
                psUpdVen.executeUpdate();
            }

            try (Statement st = conn.createStatement()) {
                st.executeUpdate("ALTER TABLE clientes AUTO_INCREMENT = " + (getMaxId(conn) + 1));
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { /* ignorar */ }
            }
            System.err.println("Error eliminarYResequenciar Cliente: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    private int getMaxId(Connection conn) {
        String sql = "SELECT COALESCE(MAX(id),0) AS m FROM clientes";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("m");
        } catch (SQLException ignored) {}
        return 0;
    }
}
