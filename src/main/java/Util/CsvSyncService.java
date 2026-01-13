package Util;
/*
import Model.Cliente;
import Model.Compra;
import Model.Producto;
import Model.Proveedor;
import Model.Venta;
import Service.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CsvSyncService {
    private final CsvRepository csvRepo;
    private final CsvExporter csvExporter;
    private final ClienteService clienteService;
    private final ProveedorService proveedorService;
    private final VentaService ventaService;
    private final CompraService compraService;
    private final ProductoService productoService;

    public CsvSyncService(CsvRepository csvRepo, CsvExporter csvExporter,
                          ClienteService clienteService, ProveedorService proveedorService,
                          VentaService ventaService, CompraService compraService,
                          ProductoService productoService) {
        this.csvRepo = csvRepo;
        this.csvExporter = csvExporter;
        this.clienteService = clienteService;
        this.proveedorService = proveedorService;
        this.ventaService = ventaService;
        this.compraService = compraService;
        this.productoService = productoService;
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

    private void syncCsvToDatabase() {
        List<Cliente> csvClientes = csvRepo.loadClientes();
        for (Cliente c : csvClientes) {
            if (c == null) continue;
            Cliente db = c.getDni() != null && !c.getDni().isBlank() ? clienteService.buscarPorDNI(c.getDni()) : null;
            if (db == null) {
                clienteService.guardar(c);
            } else {
                c.setId(db.getId());
                clienteService.guardar(c);
            }
        }

        Set<Integer> ventasExistentes = ventaService.listarVentas().stream()
                .map(Venta::getNumFactura).collect(Collectors.toSet());
        for (Venta v : csvRepo.loadVentas()) {
            if (v == null) continue;
            if (v.getNumFactura() > 0 && ventasExistentes.contains(v.getNumFactura())) continue;
            Cliente c = v.getCliente();
            Cliente dbC = null;
            if (c != null) {
                if (c.getId() > 0) dbC = clienteService.buscarPorId(c.getId());
                if (dbC == null && c.getDni() != null) dbC = clienteService.buscarPorDNI(c.getDni());
            }
            if (dbC == null) continue;
            v.setCliente(dbC);
            ventaService.crearVenta(v);
        }

        Set<Integer> comprasExistentes = compraService.listarCompras().stream()
                .map(Compra::getId).collect(Collectors.toSet());
        for (Compra comp : csvRepo.loadCompras()) {
            if (comp == null) continue;
            if (comp.getId() > 0 && comprasExistentes.contains(comp.getId())) continue;
            Proveedor prov = comp.getProveedor();
            Proveedor dbP = prov != null ? proveedorService.buscarPorId(prov.getId()) : null;
            if (dbP == null) continue;
            comp.setProveedor(dbP);
            compraService.crearCompra(comp);
        }
    }

    private void exportDbToCsv() {
        csvExporter.exportClientes(clienteService.obtenerTodos());
        csvExporter.exportProveedores(proveedorService.obtenerTodos());
        csvExporter.exportProductos(productoService.obtenerTodos());
        csvExporter.exportVentas(ventaService.listarVentas());
        csvExporter.exportCompras(compraService.listarCompras());
    }
}*/