package App;

import Model.*;
import Service.*;
import Service.Handlers.CompraHandler;
import Service.Handlers.VentaHandler;
import Util.CsvExporter;
import Util.CsvRepository;
import Util.CsvSyncService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class Menu {
    private final ClienteService clienteService;
    private final ProductoService productoService;
    private final ProveedorService proveedorService;
    private final VentaHandler ventaHandler;
    private final CompraHandler compraHandler;
    private final CsvExporter csvExporter;
    private final CsvRepository csvRepo;
    private final CsvSyncService csvSyncService;
    private final Scanner scanner = new Scanner(System.in);
    private boolean useCsv = false;

    Menu(ClienteService cs, ProductoService ps, ProveedorService prs,
         CsvExporter exporter, CsvRepository repo, CsvSyncService csvSyncService,
         VentaHandler ventaHandler, CompraHandler compraHandler) {
        this.clienteService = cs;
        this.productoService = ps;
        this.proveedorService = prs;
        this.csvExporter = exporter;
        this.csvRepo = repo;
        this.csvSyncService = csvSyncService;
        this.ventaHandler = ventaHandler;
        this.compraHandler = compraHandler;
    }

    public void start() {
        elegirModo();
        menuPrincipal();
    }

    private void elegirModo() {
        System.out.println("Seleccione modo de acceso a datos:");
        System.out.println("1. Base de datos");
        System.out.println("2. CSV (src/main/resources)");
        System.out.print("Elige: ");
        String op = scanner.nextLine().trim();
        useCsv = "2".equals(op);
        System.out.println("Modo seleccionado: " + (useCsv ? "CSV" : "Base de datos"));
    }

    private void menuPrincipal() {
        while (true) {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. Gestión de Clientes");
            System.out.println("2. Gestión de Productos");
            System.out.println("3. Gestión de Ventas");
            System.out.println("4. Gestión de Compras");
            System.out.println("5. Exportar/Actualizar CSV");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");
            String op = scanner.nextLine().trim();
            switch (op) {
                case "1":
                    gestionarClientes();
                    break;
                case "2":
                    listarProductos();
                    break;
                case "3":
                    gestionarVentas();
                    break;
                case "4":
                    gestionarCompras();
                    break;
                case "5":
                    exportarCsv();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private void gestionarClientes() {
        while (true) {
            System.out.println("\n--- GESTIÓN DE CLIENTES ---");
            System.out.println("1. Listar clientes");
            System.out.println("2. Añadir cliente");
            System.out.println("3. Eliminar cliente");
            System.out.println("0. Volver");
            System.out.print("Elige: ");
            String op = scanner.nextLine().trim();
            switch (op) {
                case "1":
                    if (useCsv) {
                        List<Cliente> clientes = csvRepo.loadClientes();
                        if (clientes.isEmpty()) System.out.println("Sin clientes (CSV).");
                        else clientes.forEach(System.out::println);
                        System.out.print("Pulsa ENTER para volver...");
                        scanner.nextLine();
                    } else {
                        List<Cliente> clientes = clienteService.obtenerTodos();
                        if (clientes.isEmpty()) System.out.println("Sin clientes.");
                        else clientes.forEach(System.out::println);
                        System.out.print("Pulsa ENTER para volver...");
                        scanner.nextLine();
                    }
                    break;
                case "2":
                    Cliente nuevo = new Cliente();
                    System.out.print("Nombre: ");
                    nuevo.setNombre(scanner.nextLine().trim());
                    System.out.print("Apellido1: ");
                    nuevo.setApellido1(scanner.nextLine().trim());
                    System.out.print("Apellido2: ");
                    nuevo.setApellido2(scanner.nextLine().trim());
                    System.out.print("Teléfono: ");
                    nuevo.setTelefono(scanner.nextLine().trim());

                    String emailInput;
                    while (true) {
                        System.out.print("Email: ");
                        emailInput = scanner.nextLine().trim();
                        try {
                            nuevo.setEmail(emailInput);
                            break;
                        } catch (IllegalArgumentException e) {
                            System.out.println("Email no válido. Introduzca de nuevo.");
                        }
                    }

                    System.out.print("Dirección: ");
                    nuevo.setDireccion(scanner.nextLine().trim());

                    String dniInput;
                    while (true) {
                        System.out.print("DNI: ");
                        dniInput = scanner.nextLine().trim();
                        try {
                            nuevo.setDni(dniInput);
                            break;
                        } catch (IllegalArgumentException e) {
                            System.out.println("DNI no válido. Debe ser 8 números y una letra. Introduzca de nuevo.");
                        }
                    }

                    if (useCsv) {
                        boolean ok = csvRepo.appendCliente(nuevo);
                        System.out.println(ok ? "Cliente añadido (CSV)." : "Error añadiendo cliente (CSV).");
                    } else {
                        boolean ok = clienteService.insertar(nuevo);
                        System.out.println(ok ? "Cliente añadido." : "Error añadiendo cliente.");
                    }
                    break;
                case "3":
                    if (useCsv) {
                        List<Cliente> clientesCsv = csvRepo.loadClientes();
                        if (clientesCsv.isEmpty()) System.out.println("Sin clientes (CSV).");
                        else clientesCsv.forEach(System.out::println);
                    } else {
                        List<Cliente> clientesDb = clienteService.obtenerTodos();
                        if (clientesDb.isEmpty()) System.out.println("Sin clientes.");
                        else clientesDb.forEach(System.out::println);
                    }

                    System.out.print("ID cliente a eliminar: ");
                    try {
                        int id = Integer.parseInt(scanner.nextLine().trim());
                        if (useCsv) {
                        } else {
                            try {
                                clienteService.eliminar(id);
                                System.out.println("Cliente eliminado.");
                            } catch (Service.BusinessException e) {
                                System.out.println("No se puede eliminar: " + e.getMessage());
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("ID no válido.");
                    }
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private void listarProductos() {
        if (useCsv) {
            List<Producto> inventario = csvRepo.loadProductos();
            if (inventario.isEmpty()) System.out.println("Sin productos (CSV).");
            else inventario.forEach(System.out::println);
            System.out.print("Pulsa ENTER para volver...");
            scanner.nextLine();
            return;
        }
        List<Producto> inventario = productoService.obtenerInventario();
        if (inventario.isEmpty()) System.out.println("Sin productos.");
        else inventario.forEach(System.out::println);
        System.out.print("Pulsa ENTER para volver...");
        scanner.nextLine();
    }

    private void gestionarVentas() {
        while (true) {
            System.out.println("\n--- GESTIÓN DE VENTAS ---");
            System.out.println("1. Crear venta");
            System.out.println("2. Listar ventas");
            System.out.println("0. Volver");
            System.out.print("Elige: ");
            String op = scanner.nextLine().trim();
            switch (op) {
                case "1":
                    crearVentaInteractiva();
                    break;
                case "2":
                    listarVentas();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private void crearVentaInteractiva() {
        if (useCsv) {
            List<Cliente> clientes = csvRepo.loadClientes();
            if (clientes.isEmpty()) {
                System.out.println("No hay clientes (CSV).");
                return;
            }
            clientes.forEach(c -> System.out.println("ID: " + c.getId() + " | " + c.getNombre() + " | DNI: " + c.getDni()));
            System.out.print("Introduzca ID cliente: ");
            String entrada = scanner.nextLine().trim();
            Cliente cliente = clientes.stream().filter(c -> String.valueOf(c.getId()).equals(entrada)).findFirst().orElse(null);
            if (cliente == null) {
                System.out.println("Cliente no encontrado (CSV).");
                return;
            }
            Venta venta = new Venta(cliente);
            List<Producto> inventario = csvRepo.loadProductos();
            if (inventario.isEmpty()) {
                System.out.println("No hay productos (CSV).");
                return;
            }
            while (true) {
                inventario.forEach(System.out::println);
                System.out.print("ID producto (0 para terminar): ");
                int id = Integer.parseInt(scanner.nextLine().trim());
                if (id == 0) break;
                Producto p = inventario.stream().filter(prod -> prod.getId() == id).findFirst().orElse(null);
                if (p == null) {
                    System.out.println("Producto no encontrado (CSV).");
                    continue;
                }
                System.out.print("Cantidad: ");
                int cantidad = Integer.parseInt(scanner.nextLine().trim());
                if (cantidad <= 0) {
                    System.out.println("Cantidad no válida.");
                    continue;
                }
                if (p.getStock() < cantidad) {
                    System.out.println("Stock insuficiente (CSV).");
                    continue;
                }
                venta.agregarDetalle(new Model.DetalleVenta(p, cantidad));
                System.out.println("Añadido. Subtotal actual: " + venta.getTotalImporte());
            }
            if (venta.getDetalles().isEmpty()) {
                System.out.println("Venta vacía, cancelada.");
                return;
            }
            boolean ok = csvRepo.appendVenta(venta);
            System.out.println(ok ? "Venta registrada (CSV)." : "Error al registrar venta (CSV).");
            return;
        }

        System.out.println("\nClientes disponibles:");
        List<Cliente> clientes = clienteService.obtenerTodos();
        if (clientes == null || clientes.isEmpty()) {
            System.out.println("No hay clientes. Cree uno antes.");
            return;
        }
        clientes.forEach(c -> System.out.println("ID: " + c.getId() + " | Nombre: " + c.getNombre() + " " + c.getApellido1() + " | DNI: " + c.getDni()));

        System.out.print("Introduzca ID o DNI del cliente: ");
        String entrada = scanner.nextLine().trim();
        Cliente cliente = null;
        if (entrada.matches("\\d+")) {
            try {
                cliente = clienteService.buscarPorId(Integer.parseInt(entrada));
            } catch (Exception ignored) {
            }
        }
        if (cliente == null) cliente = clienteService.buscarPorDNI(entrada);
        if (cliente == null) {
            System.out.println("Cliente no encontrado.");
            return;
        }

        Venta venta = new Venta(cliente);
        List<Producto> inventario = productoService.obtenerInventario();
        while (true) {
            inventario.forEach(System.out::println);
            System.out.print("ID producto (0 para terminar): ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            if (id == 0) break;
            Producto p = productoService.buscarPorId(id);
            if (p == null) {
                System.out.println("Producto no existe.");
                continue;
            }
            System.out.print("Cantidad: ");
            int cantidad = Integer.parseInt(scanner.nextLine().trim());
            if (cantidad <= 0) {
                System.out.println("Cantidad no válida.");
                continue;
            }
            if (p.getStock() < cantidad) {
                System.out.println("Stock insuficiente.");
                continue;
            }
            venta.agregarDetalle(new Model.DetalleVenta(p, cantidad));
            System.out.println("Añadido. Subtotal actual: " + venta.getTotalImporte());
        }
        if (venta.getDetalles().isEmpty()) {
            System.out.println("Venta vacía, cancelada.");
            return;
        }
        try {
            boolean ok = ventaHandler.crearVenta(venta);
            System.out.println(ok ? "Venta registrada." : "Error al registrar venta.");
        } catch (BusinessException e) {
            System.out.println("Error registrando venta: " + e.getMessage());
        }
    }

    private void listarVentas() {
        if (useCsv) {
            List<Venta> ventas = csvRepo.loadVentas();
            if (ventas.isEmpty()) System.out.println("Sin ventas (CSV).");
            else ventas.forEach(v -> System.out.println("Venta #" + v.getNumFactura() + " | Cliente: " +
                    (v.getCliente() != null ? v.getCliente().getNombre() : "N/A") + " | Total: " + v.getTotalImporte()));
            System.out.print("Pulsa ENTER para volver...");
            scanner.nextLine();
            return;
        }
        List<Venta> ventas = ventaHandler.listarVentas();
        if (ventas.isEmpty()) System.out.println("Sin ventas.");
        else ventas.forEach(v -> System.out.println("Venta #" + v.getNumFactura() + " | Cliente: " +
                (v.getCliente() != null ? v.getCliente().getNombre() : "N/A") + " | Total: " + v.getTotalImporte()));
        System.out.print("Pulsa ENTER para volver...");
        scanner.nextLine();
    }

    private void gestionarCompras() {
        while (true) {
            System.out.println("\n--- GESTIÓN DE COMPRAS ---");
            System.out.println("1. Crear compra");
            System.out.println("2. Listar compras");
            System.out.println("0. Volver");
            System.out.print("Elige: ");
            String op = scanner.nextLine().trim();
            switch (op) {
                case "1":
                    crearCompraInteractiva();
                    break;
                case "2":
                    listarCompras();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private void crearCompraInteractiva() {
        if (useCsv) {
            List<Proveedor> proveedores = csvRepo.loadProveedores();
            if (proveedores.isEmpty()) {
                System.out.println("No hay proveedores (CSV).");
                return;
            }
            proveedores.forEach(p -> System.out.println("ID: " + p.getId() + " | " + p.getNombre()));
            System.out.print("ID proveedor: ");
            int idProv = Integer.parseInt(scanner.nextLine().trim());
            Proveedor prov = proveedores.stream().filter(p -> p.getId() == idProv).findFirst().orElse(null);
            if (prov == null) {
                System.out.println("Proveedor no encontrado (CSV).");
                return;
            }
            Compra compra = new Compra(prov);
            List<Producto> productos = csvRepo.loadProductos().stream()
                    .filter(p -> p.getIdProveedor() != null && p.getIdProveedor() == prov.getId())
                    .collect(Collectors.toList());
            if (productos.isEmpty()) System.out.println("No hay productos del proveedor (CSV). Mostrando todos.");
            List<Producto> pool = productos.isEmpty() ? csvRepo.loadProductos() : productos;
            while (true) {
                pool.forEach(System.out::println);
                System.out.print("ID producto (0 para terminar): ");
                int id = Integer.parseInt(scanner.nextLine().trim());
                if (id == 0) break;
                Producto p = pool.stream().filter(prod -> prod.getId() == id).findFirst().orElse(null);
                if (p == null) {
                    System.out.println("Producto no encontrado (CSV).");
                    continue;
                }
                System.out.print("Cantidad: ");
                int cantidad = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Precio unitario: ");
                BigDecimal precio = new BigDecimal(scanner.nextLine().trim());
                compra.agregarDetalle(new Model.DetalleCompra(p, cantidad, precio));
                System.out.println("Añadido. Total provisional: " + compra.getTotalImporte());
            }
            if (compra.getDetalles().isEmpty()) {
                System.out.println("Compra vacía, cancelada.");
                return;
            }
            boolean ok = csvRepo.appendCompra(compra);
            System.out.println(ok ? "Compra registrada (CSV)." : "Error al registrar compra (CSV).");
            return;
        }

        List<Proveedor> proveedores = proveedorService.obtenerTodos();
        if (proveedores.isEmpty()) {
            System.out.println("No hay proveedores.");
            return;
        }
        proveedores.forEach(p -> System.out.println("ID: " + p.getId() + " | " + p.getNombre()));
        System.out.print("ID proveedor: ");
        int idProv = Integer.parseInt(scanner.nextLine().trim());
        Proveedor prov = proveedorService.buscarPorId(idProv);
        if (prov == null) {
            System.out.println("Proveedor no existe.");
            return;
        }
        Compra compra = new Compra(prov);
        List<Producto> productosProv = productoService.obtenerPorProveedor(idProv);
        if (productosProv.isEmpty())
            System.out.println("Este proveedor no tiene productos asociados. Puede añadir igual.");
        List<Producto> pool = productosProv.isEmpty() ? productoService.obtenerInventario() : productosProv;
        while (true) {
            pool.forEach(System.out::println);
            System.out.print("ID producto (0 para terminar): ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            if (id == 0) break;
            Producto p = productoService.buscarPorId(id);
            if (p == null) {
                System.out.println("Producto no existe.");
                continue;
            }
            if (!productosProv.isEmpty() && (p.getIdProveedor() == null || p.getIdProveedor() != idProv)) {
                System.out.println("Producto no pertenece a este proveedor.");
                continue;
            }
            System.out.print("Cantidad: ");
            int cantidad = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Precio unitario: ");
            BigDecimal precio = new BigDecimal(scanner.nextLine().trim());
            compra.agregarDetalle(new Model.DetalleCompra(p, cantidad, precio));
            System.out.println("Añadido. Total provisional: " + compra.getTotalImporte());
        }
        if (compra.getDetalles().isEmpty()) {
            System.out.println("Compra vacía, cancelada.");
            return;
        }
        try {
            boolean ok = compraHandler.crearCompra(compra);
            System.out.println(ok ? "Compra registrada." : "Error al registrar compra.");
        } catch (BusinessException e) {
            System.out.println("Error registrando compra: " + e.getMessage());
        }
    }

    private void listarCompras() {
        if (useCsv) {
            List<Compra> compras = csvRepo.loadCompras();
            if (compras.isEmpty()) System.out.println("Sin compras (CSV).");
            else compras.forEach(System.out::println);
            System.out.print("Pulsa ENTER para volver...");
            scanner.nextLine();
            return;
        }
        List<Compra> compras = compraHandler.listarCompras();
        if (compras.isEmpty()) System.out.println("Sin compras.");
        else compras.forEach(System.out::println);
        System.out.print("Pulsa ENTER para volver...");
        scanner.nextLine();
    }

    private void exportarCsv() {
        if (useCsv) {
            System.out.println("Sincronizando CSV -> Base de datos y actualizando CSV desde BD...");
            boolean ok = csvSyncService.syncAndExport();
            System.out.println(ok ? "Sincronización completada." : "Error durante la sincronización. Consulte logs.");
            return;
        }

        try {
            csvExporter.exportClientes(clienteService.obtenerTodos());
            csvExporter.exportProveedores(proveedorService.obtenerTodos());
            csvExporter.exportProductos(productoService.obtenerInventario());
            List<Venta> ventas = ventaHandler.listarVentas();
            List<Compra> compras = compraHandler.listarCompras();
            csvExporter.exportVentas(ventas);
            csvExporter.exportCompras(compras);

            System.out.println("CSV generados en src/main/resources");
        } catch (Exception e) {
            System.out.println("Error exportando CSV: " + e.getMessage());
        }
    }
}
