package App;

import DB.TransactionManager;
import Service.*;
import Service.Handlers.CompraHandler;
import Service.Handlers.VentaHandler;
import Service.Implements.*;
import Util.CsvExporter;
import Util.CsvRepository;
import Util.CsvSyncService;

public class Main {
    public static void main(String[] args) {
        TransactionManager txManager = new TransactionManager();
        TransaccionesService txService = new TransaccionesService(txManager);

        ClienteService clienteService = new ClienteServiceImpl(txService);
        ProductoService productoService = new ProductoServiceImpl();
        ProveedorService proveedorService = new ProveedorServiceImpl();
        VentaService ventaService = new VentaServiceImpl(txService);
        CompraService compraService = new CompraServiceImpl(txService);
        CsvExporter exporter = new CsvExporter();
        CsvRepository csvRepo = new CsvRepository();
        CsvSyncService csvSyncService = new CsvSyncService(csvRepo, exporter);

        VentaHandler ventaHandler = new VentaHandler(ventaService, productoService);
        CompraHandler compraHandler = new CompraHandler(compraService, productoService, proveedorService);

        Menu menu = new Menu(clienteService, productoService, proveedorService,
                exporter, csvRepo, csvSyncService, ventaHandler, compraHandler);
        menu.start();
    }
}
