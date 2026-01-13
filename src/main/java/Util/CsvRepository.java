package Util;
/*
import Dao.ClienteDAO;
import Dao.CompraDAO;
import Dao.ProveedorDAO;
import Dao.VentaDAO;
import Model.*;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CsvRepository {
    private final Path resourcesDir = Path.of("src", "main", "resources");
    private final DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public CsvRepository() {
        try {
            directorio();
        } catch (Exception ignored) {
        }
    }

    private void directorio() throws Exception {
        if (!Files.exists(resourcesDir)) Files.createDirectories(resourcesDir);
    }

    public List<Cliente> loadClientes() {
        Path f = resourcesDir.resolve("clientes.csv");
        List<Cliente> list = new ArrayList<>();
        if (!Files.exists(f)) return list;
        try (BufferedReader br = Files.newBufferedReader(f, StandardCharsets.UTF_8)) {
            String header = br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                List<String> cols = parseCsvLine(line);
                if (cols.size() < 8) continue;
                Cliente c = new Cliente();
                c.setId(parseIntSafe(cols.get(0), 0));
                c.setNombre(cols.get(1));
                c.setApellido1(cols.get(2));
                c.setApellido2(cols.get(3));
                c.setTelefono(cols.get(4));
                c.setEmail(cols.get(5));
                c.setDireccion(cols.get(6));
                c.setDni(cols.get(7));
                list.add(c);
            }
        } catch (Exception e) {
            System.err.println("Error loadClientes: " + e.getMessage());
        }
        return list;
    }

    public boolean appendCliente(Cliente nuevo) {
        try {
            Path f = resourcesDir.resolve("clientes.csv");
            List<String> lines = new ArrayList<>();
            if (Files.exists(f)) {
                lines = Files.readAllLines(f, StandardCharsets.UTF_8);
            } else {
                lines.add("id,nombre,apellido1,apellido2,telefono,email,direccion,dni");
            }
            int nextId = 1;
            if (lines.size() > 1) {
                for (int i = lines.size() - 1; i >= 1; i--) {
                    String l = lines.get(i).trim();
                    if (l.isEmpty()) continue;
                    List<String> cols = parseCsvLine(l);
                    if (!cols.isEmpty()) {
                        nextId = Math.max(nextId, parseIntSafe(cols.get(0), 0) + 1);
                        break;
                    }
                }
            }
            nuevo.setId(nextId);
            String row = String.join(",",
                    String.valueOf(nuevo.getId()),
                    escapeCsv(nuevo.getNombre()),
                    escapeCsv(nuevo.getApellido1()),
                    escapeCsv(nuevo.getApellido2()),
                    escapeCsv(nuevo.getTelefono()),
                    escapeCsv(nuevo.getEmail()),
                    escapeCsv(nuevo.getDireccion()),
                    escapeCsv(nuevo.getDni())
            );
            lines.add(row);
            Files.write(f, lines, StandardCharsets.UTF_8);
            ClienteDAO dao = new ClienteDAO();
            dao.insertar(nuevo);
            return true;
        } catch (Exception e) {
            System.err.println("Error appendCliente: " + e.getMessage());
            return false;
        }
    }

    public boolean removeClienteById(int id) {
        Path f = resourcesDir.resolve("clientes.csv");
        if (!Files.exists(f)) return false;
        try {
            List<String> lines = Files.readAllLines(f, StandardCharsets.UTF_8);
            if (lines.size() <= 1) return false;
            List<String> newLines = new ArrayList<>();
            newLines.add(lines.get(0));
            boolean removed = false;
            for (int i = 1; i < lines.size(); i++) {
                List<String> cols = parseCsvLine(lines.get(i));
                if (cols.isEmpty()) continue;
                int rid = parseIntSafe(cols.get(0), -1);
                if (rid == id) {
                    removed = true;
                    continue;
                }
                newLines.add(lines.get(i));
            }
            if (removed) Files.write(f, newLines, StandardCharsets.UTF_8);
            return removed;
        } catch (Exception e) {
            System.err.println("Error removeClienteById: " + e.getMessage());
            return false;
        }
    }

    public List<Proveedor> loadProveedores() {
        Path f = resourcesDir.resolve("proveedores.csv");
        List<Proveedor> list = new ArrayList<>();
        if (!Files.exists(f)) return list;
        try (BufferedReader br = Files.newBufferedReader(f, StandardCharsets.UTF_8)) {
            String header = br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                List<String> cols = parseCsvLine(line);
                if (cols.size() < 3) continue;
                Proveedor p = new Proveedor();
                p.setId(parseIntSafe(cols.get(0), 0));
                p.setNombre(cols.get(1));
                p.setEmailContacto(cols.get(2));
                list.add(p);
            }
        } catch (Exception e) {
            System.err.println("Error loadProveedores: " + e.getMessage());
        }
        return list;
    }

    public boolean appendProveedor(Proveedor prov) {
        try {
            Path f = resourcesDir.resolve("proveedores.csv");
            List<String> lines = new ArrayList<>();
            if (Files.exists(f)) lines = Files.readAllLines(f, StandardCharsets.UTF_8);
            else lines.add("id,nombre,email_contacto");
            int nextId = 1;
            if (lines.size() > 1) {
                for (int i = lines.size() - 1; i >= 1; i--) {
                    String l = lines.get(i).trim();
                    if (l.isEmpty()) continue;
                    List<String> cols = parseCsvLine(l);
                    if (!cols.isEmpty()) {
                        nextId = Math.max(nextId, parseIntSafe(cols.get(0), 0) + 1);
                        break;
                    }
                }
            }
            prov.setId(nextId);
            String row = String.join(",", String.valueOf(prov.getId()), escapeCsv(prov.getNombre()), escapeCsv(prov.getEmailContacto()));
            lines.add(row);
            Files.write(f, lines, StandardCharsets.UTF_8);
            return true;
        } catch (Exception e) {
            System.err.println("Error appendProveedor: " + e.getMessage());
            return false;
        }
    }

    public List<Producto> loadProductos() {
        Path f = resourcesDir.resolve("productos.csv");
        List<Producto> list = new ArrayList<>();
        if (!Files.exists(f)) return list;
        try (BufferedReader br = Files.newBufferedReader(f, StandardCharsets.UTF_8)) {
            String header = br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                List<String> cols = parseCsvLine(line);
                if (cols.size() < 6) continue;
                Producto p = new Producto();
                p.setId(parseIntSafe(cols.get(0), 0));
                p.setNombre(cols.get(1));
                p.setColeccion(cols.get(2));
                p.setStock(parseIntSafe(cols.get(3), 0));
                try {
                    p.setPrecio(cols.get(4) == null || cols.get(4).isBlank() ? null : new BigDecimal(cols.get(4)));
                } catch (Exception ignored) {
                }
                p.setIdProveedor(cols.get(5) == null || cols.get(5).isBlank() ? null : parseIntSafe(cols.get(5), 0));
                list.add(p);
            }
        } catch (Exception e) {
            System.err.println("Error loadProductos: " + e.getMessage());
        }
        return list;
    }

    public List<Venta> loadVentas() {
        Path f = resourcesDir.resolve("ventas.csv");
        List<Venta> list = new ArrayList<>();
        if (!Files.exists(f)) return list;
        try (BufferedReader br = Files.newBufferedReader(f, StandardCharsets.UTF_8)) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                List<String> cols = parseCsvLine(line);
                if (cols.size() < 4) continue;
                Venta v = new Venta(null);
                v.setNumFactura(parseIntSafe(cols.get(0), 0));
                String clienteNombre = cols.get(1);
                if (clienteNombre != null && !clienteNombre.isBlank()) {
                    Cliente c = new Cliente();
                    c.setNombre(clienteNombre);
                    v.setCliente(c);
                }
                if (cols.get(2) != null && !cols.get(2).isBlank()) {
                    try {
                        v.setFechaVenta(LocalDateTime.parse(cols.get(2)));
                    } catch (Exception ignored) {
                    }
                }
                try {
                    v.setTotalImporte(cols.get(3) != null && !cols.get(3).isBlank() ? new BigDecimal(cols.get(3)) : BigDecimal.ZERO);
                } catch (Exception ignored) {
                }
                list.add(v);
            }
        } catch (Exception e) {
            System.err.println("Error loadVentas: " + e.getMessage());
        }
        return list;
    }

    public boolean appendVenta(Venta venta) {
        try {
            Path f = resourcesDir.resolve("ventas.csv");
            List<String> lines = new ArrayList<>();
            if (Files.exists(f)) lines = Files.readAllLines(f, StandardCharsets.UTF_8);
            else lines.add("num_factura,cliente_nombre,fecha,total_importe");
            int nextId = 1;
            if (lines.size() > 1) {
                for (int i = lines.size() - 1; i >= 1; i--) {
                    List<String> cols = parseCsvLine(lines.get(i));
                    if (!cols.isEmpty()) {
                        nextId = Math.max(nextId, parseIntSafe(cols.get(0), 0) + 1);
                        break;
                    }
                }
            }
            venta.setNumFactura(nextId);
            String fecha = venta.getFechaVenta() != null ? venta.getFechaVenta().toString() : LocalDateTime.now().toString();
            String clienteNombre = venta.getCliente() != null ? venta.getCliente().getNombre() : "";
            String row = String.join(",",
                    String.valueOf(venta.getNumFactura()),
                    escapeCsv(clienteNombre),
                    escapeCsv(fecha),
                    venta.getTotalImporte() != null ? venta.getTotalImporte().toString() : "0"
            );
            lines.add(row);
            Files.write(f, lines, StandardCharsets.UTF_8);

            boolean dbOk = false;
            if (venta.getCliente() != null) {
                ClienteDAO cdao = new ClienteDAO();
                Cliente c = null;
                if (venta.getCliente().getId() != 0) c = cdao.buscarPorId(venta.getCliente().getId());
                if (c == null && venta.getCliente().getDni() != null)
                    c = cdao.buscarPorDNI(venta.getCliente().getDni());
                if (c != null) {
                    venta.setCliente(c);
                    VentaDAO vdao = new VentaDAO();
                    dbOk = vdao.crearVenta(venta);
                }
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error appendVenta: " + e.getMessage());
            return false;
        }
    }

    public List<Compra> loadCompras() {
        Path f = resourcesDir.resolve("compras.csv");
        List<Compra> list = new ArrayList<>();
        if (!Files.exists(f)) return list;
        try (BufferedReader br = Files.newBufferedReader(f, StandardCharsets.UTF_8)) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                List<String> cols = parseCsvLine(line);
                if (cols.size() < 4) continue;
                Compra c = new Compra();
                c.setId(parseIntSafe(cols.get(0), 0));
                String provNombre = cols.get(1);
                if (provNombre != null && !provNombre.isBlank()) {
                    Proveedor p = new Proveedor();
                    p.setNombre(provNombre);
                    c.setProveedor(p);
                }
                if (cols.get(2) != null && !cols.get(2).isBlank()) {
                    try {
                        c.setFecha(LocalDateTime.parse(cols.get(2)));
                    } catch (Exception ignored) {
                    }
                }
                try {
                    c.setTotalImporte(cols.get(3) != null && !cols.get(3).isBlank() ? new BigDecimal(cols.get(3)) : BigDecimal.ZERO);
                } catch (Exception ignored) {
                }
                list.add(c);
            }
        } catch (Exception e) {
            System.err.println("Error loadCompras: " + e.getMessage());
        }
        return list;
    }

    public boolean appendCompra(Compra compra) {
        try {
            Path f = resourcesDir.resolve("compras.csv");
            List<String> lines = new ArrayList<>();
            if (Files.exists(f)) lines = Files.readAllLines(f, StandardCharsets.UTF_8);
            else lines.add("id,proveedor_nombre,fecha,total_importe");
            int nextId = 1;
            if (lines.size() > 1) {
                for (int i = lines.size() - 1; i >= 1; i--) {
                    List<String> cols = parseCsvLine(lines.get(i));
                    if (!cols.isEmpty()) {
                        nextId = Math.max(nextId, parseIntSafe(cols.get(0), 0) + 1);
                        break;
                    }
                }
            }
            compra.setId(nextId);
            String fecha = compra.getFecha() != null ? compra.getFecha().toString() : LocalDateTime.now().toString();
            String provNombre = compra.getProveedor() != null ? compra.getProveedor().getNombre() : "";
            String row = String.join(",",
                    String.valueOf(compra.getId()),
                    escapeCsv(provNombre),
                    escapeCsv(fecha),
                    compra.getTotalImporte() != null ? compra.getTotalImporte().toString() : "0"
            );
            lines.add(row);
            Files.write(f, lines, StandardCharsets.UTF_8);


            boolean dbOk = false;
            if (compra.getProveedor() != null) {
                ProveedorDAO pdao = new ProveedorDAO();
                Proveedor pBD = null;
                if (compra.getProveedor().getId() != 0) pBD = pdao.buscarPorId(compra.getProveedor().getId());
                if (pBD != null) {
                    compra.setProveedor(pBD);
                    CompraDAO cdao = new CompraDAO();
                    dbOk = cdao.crearCompra(compra);
                }
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error appendCompra: " + e.getMessage());
            return false;
        }
    }

    private String escapeCsv(String s) {
        if (s == null) return "";
        String replaced = s.replace("\"", "\"\"");
        if (replaced.contains(",") || replaced.contains("\n") || replaced.contains("\r") || replaced.contains("\"")) {
            return "\"" + replaced + "\"";
        }
        return replaced;
    }

    private List<String> parseCsvLine(String line) {
        List<String> cols = new ArrayList<>();
        if (line == null) return cols;
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cur.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                cols.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(ch);
            }
        }
        cols.add(cur.toString());
        return cols;
    }

    private int parseIntSafe(String s, int def) {
        try {
            if (s == null || s.isBlank()) return def;
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return def;
        }
    }
}
*/