package Util;

import Dao.*;
import Model.Cliente;
import Model.Compra;
import Model.Proveedor;
import Model.Venta;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CsvSyncService {
    private final CsvRepository csvRepo;
    private final CsvExporter csvExporter;
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProveedorDAO proveedorDAO = new ProveedorDAO();
    private final VentaDAO ventaDAO = new VentaDAO();
    private final CompraDAO compraDAO = new CompraDAO();

    public CsvSyncService(CsvRepository csvRepo, CsvExporter csvExporter) {
        this.csvRepo = csvRepo;
        this.csvExporter = csvExporter;
    }

    public boolean syncAndExport() {
        try {
            syncCsvToDatabase();
            exportDbToCsv();
            return true;
        } catch (Exception e) {
            System.err.println("Error syncAndExport: " + e.getMessage());
            return false;
        }
    }

    private void syncCsvToDatabase() throws Exception {
        List<Cliente> csvClientes = csvRepo.loadClientes();
        for (Cliente c : csvClientes) {
            if (c == null) continue;
            Cliente db = c.getDni() != null && !c.getDni().isBlank() ? clienteDAO.buscarPorDNI(c.getDni()) : null;
            if (db == null) {
                clienteDAO.insertar(c);
            } else {
                c.setId(db.getId());
                clienteDAO.actualizar(c);
            }
        }

        Set<Integer> ventasExistentes = ventaDAO.listarVentas().stream()
                .map(Venta::getNumFactura).collect(Collectors.toSet());
        for (Venta v : csvRepo.loadVentas()) {
            if (v == null) continue;
            if (v.getNumFactura() > 0 && ventasExistentes.contains(v.getNumFactura())) continue;
            Cliente c = v.getCliente();
            Cliente dbC = null;
            if (c != null) {
                if (c.getId() > 0) dbC = clienteDAO.buscarPorId(c.getId());
                if (dbC == null && c.getDni() != null) dbC = clienteDAO.buscarPorDNI(c.getDni());
            }
            if (dbC == null) continue;
            v.setCliente(dbC);
            ventaDAO.crearVenta(v);
        }

        Set<Integer> comprasExistentes = compraDAO.listarCompras().stream()
                .map(Compra::getId).collect(Collectors.toSet());
        for (Compra comp : csvRepo.loadCompras()) {
            if (comp == null) continue;
            if (comp.getId() > 0 && comprasExistentes.contains(comp.getId())) continue;
            Proveedor prov = comp.getProveedor();
            Proveedor dbP = prov != null ? proveedorDAO.buscarPorId(prov.getId()) : null;
            if (dbP == null) continue;
            comp.setProveedor(dbP);
            compraDAO.crearCompra(comp);
        }
    }

    private void exportDbToCsv() throws Exception {
        CsvExporter exporter = this.csvExporter;
        ClienteDAO cDao = new ClienteDAO();
        ProveedorDAO pDao = new ProveedorDAO();
        ProductoDAO prodDao = new ProductoDAO();

        exporter.exportClientes(cDao.obtenerTodos());
        exporter.exportProveedores(pDao.obtenerTodos());
        exporter.exportProductos(prodDao.obtenerInventario());
        exporter.exportVentas(ventaDAO.listarVentas());
        exporter.exportCompras(compraDAO.listarCompras());
    }
}
