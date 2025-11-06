package Util;

import Model.*;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvExporter {
    private final Path resourcesDir = Path.of("src", "main", "resources");

    public CsvExporter() {
    }

    private void directorio() throws Exception {
        if (!Files.exists(resourcesDir)) {
            Files.createDirectories(resourcesDir);
        }
    }

    public void exportClientes(List<Cliente> clientes) throws Exception {
        directorio();
        try (PrintWriter pw = new PrintWriter(resourcesDir.resolve("clientes.csv").toFile())) {
            pw.println("id,nombre,apellido1,apellido2,telefono,email,direccion,dni");
            for (Cliente c : clientes) {
                pw.printf("%d,%s,%s,%s,%s,%s,%s,%s%n",
                        c.getId(),
                        escape(c.getNombre()),
                        escape(c.getApellido1()),
                        escape(c.getApellido2()),
                        escape(c.getTelefono()),
                        escape(c.getEmail()),
                        escape(c.getDireccion()),
                        escape(c.getDni()));
            }
        }
    }

    public void exportProveedores(List<Proveedor> proveedores) throws Exception {
        directorio();
        try (PrintWriter pw = new PrintWriter(resourcesDir.resolve("proveedores.csv").toFile())) {
            pw.println("id,nombre,email_contacto");
            for (Proveedor p : proveedores) {
                pw.printf("%d,%s,%s%n", p.getId(), escape(p.getNombre()), escape(p.getEmailContacto()));
            }
        }
    }

    public void exportProductos(List<Producto> productos) throws Exception {
        directorio();
        try (PrintWriter pw = new PrintWriter(resourcesDir.resolve("productos.csv").toFile())) {
            pw.println("id,nombre,coleccion,stock,precio,id_proveedor");
            for (Producto p : productos) {
                pw.printf("%d,%s,%s,%d,%s,%s%n",
                        p.getId(),
                        escape(p.getNombre()),
                        escape(p.getColeccion()),
                        p.getStock(),
                        p.getPrecio() != null ? p.getPrecio().toString() : "",
                        p.getIdProveedor() != null ? p.getIdProveedor().toString() : "");
            }
        }
    }

    public void exportVentas(List<Venta> ventas) throws Exception {
        directorio();
        try (PrintWriter pw = new PrintWriter(resourcesDir.resolve("ventas.csv").toFile())) {
            pw.println("num_factura,cliente_nombre,fecha,total_importe");
            for (Venta v : ventas) {
                pw.printf("%d,%s,%s,%s%n",
                        v.getNumFactura(),
                        v.getCliente() != null ? String.valueOf(v.getCliente().getNombre()) : "",
                        v.getFechaVenta() != null ? v.getFechaVenta().toString() : "",
                        v.getTotalImporte() != null ? v.getTotalImporte().toString() : "");
            }
        }
    }

    public void exportCompras(List<Compra> compras) throws Exception {
        directorio();
        try (PrintWriter pw = new PrintWriter(resourcesDir.resolve("compras.csv").toFile())) {
            pw.println("id,proveedor_nombre,fecha,total_importe");
            for (Compra c : compras) {
                pw.printf("%d,%s,%s,%s%n",
                        c.getId(),
                        c.getProveedor() != null ? String.valueOf(c.getProveedor().getNombre()) : "",
                        c.getFecha() != null ? c.getFecha().toString() : "",
                        c.getTotalImporte() != null ? c.getTotalImporte().toString() : "");
            }
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        String replaced = s.replace("\"", "\"\"");
        if (replaced.contains(",") || replaced.contains("\n") || replaced.contains("\r") || replaced.contains("\"")) {
            return "\"" + replaced + "\"";
        }
        return replaced;
    }
}
