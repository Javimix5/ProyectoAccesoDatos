import Model.*;
import Service.*;
import Service.Handlers.CompraHandler;
import Service.Handlers.VentaHandler;

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
    private final Scanner scanner = new Scanner(System.in);

    Menu(ClienteService cs, ProductoService ps, ProveedorService prs,
         VentaHandler ventaHandler, CompraHandler compraHandler) {
        this.clienteService = cs;
        this.productoService = ps;
        this.proveedorService = prs;
        this.ventaHandler = ventaHandler;
        this.compraHandler = compraHandler;
    }

    public void start() {
        System.out.println("Modo seleccionado: Base de datos (Spring Boot)");
        menuPrincipal();
    }

    private void menuPrincipal() {
        while (true) {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. Gestión de Clientes");
            System.out.println("2. Gestión de Productos");
            System.out.println("3. Gestión de Ventas");
            System.out.println("4. Gestión de Compras");
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
                    List<Cliente> clientes = clienteService.obtenerTodos();
                    if (clientes.isEmpty()) System.out.println("Sin clientes.");
                    else clientes.forEach(System.out::println);
                    System.out.print("Pulsa ENTER para volver...");
                    scanner.nextLine();
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

                    clienteService.guardar(nuevo);
                    System.out.println("Cliente añadido.");
                    break;
                case "3":
                    List<Cliente> clientesDb = clienteService.obtenerTodos();
                    if (clientesDb.isEmpty()) System.out.println("Sin clientes.");
                    else clientesDb.forEach(System.out::println);

                    System.out.print("ID cliente a eliminar: ");
                    try {
                        int id = Integer.parseInt(scanner.nextLine().trim());
                        try {
                            clienteService.eliminar(id);
                            System.out.println("Cliente eliminado.");
                        } catch (Exception e) {
                            System.out.println("No se puede eliminar: " + e.getMessage());
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
        List<Producto> inventario = productoService.obtenerTodos();
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
        System.out.println("\nClientes disponibles:");
        List<Cliente> clientes = clienteService.obtenerTodos();
        if (clientes == null || clientes.isEmpty()) {
            System.out.println("No hay clientes. Cree uno antes.");
            return;
        }
        clientes.forEach(c -> System.out.println("ID: " + c.getId() + " | Nombre: " + c.getNombre() + " " + c.getApellido1() + " | DNI: " + c.getDni()));

        System.out.print("Introduzca ID del cliente: ");
        String entrada = scanner.nextLine().trim();
        Cliente cliente = null;
        if (entrada.matches("\\d+")) {
            try {
                cliente = clienteService.buscarPorId(Integer.parseInt(entrada));
            } catch (Exception ignored) {
            }
        }
        if (cliente == null) {
            System.out.println("Cliente no encontrado.");
            return;
        }

        Venta venta = new Venta(cliente);
        List<Producto> inventario = productoService.obtenerTodos();
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
        // Nota: productoService.obtenerPorProveedor(idProv) no existe en la interfaz básica, usamos obtenerTodos
        List<Producto> productosProv = productoService.obtenerTodos().stream()
                .filter(p -> p.getIdProveedor() != null && p.getIdProveedor() == idProv)
                .collect(Collectors.toList());
        
        if (productosProv.isEmpty())
            System.out.println("Este proveedor no tiene productos asociados. Puede añadir igual.");
        List<Producto> pool = productosProv.isEmpty() ? productoService.obtenerTodos() : productosProv;
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
        List<Compra> compras = compraHandler.listarCompras();
        if (compras.isEmpty()) System.out.println("Sin compras.");
        else compras.forEach(System.out::println);
        System.out.print("Pulsa ENTER para volver...");
        scanner.nextLine();
    }
}