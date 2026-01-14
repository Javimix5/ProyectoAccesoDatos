package Controller;

import Model.Producto;
import Service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Producto", description = "Controlador para gestionar productos")
public class ProductoController {

    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @Operation(summary = "Obtener todos los productos")
    @GetMapping
    public List<Producto> getAllProductos() {
        return productoService.obtenerTodos();
    }

    @Operation(summary = "Obtener producto por ID")
    @GetMapping("/{id}")
    public Producto getProductoById(@PathVariable int id) {
        return productoService.buscarPorId(id);
    }

    @Operation(summary = "Crear un nuevo producto")
    @PostMapping
    public Producto createProducto(@RequestBody Producto producto) {
        return productoService.guardar(producto);
    }

    @Operation(summary = "Eliminar producto por ID")
    @DeleteMapping("/{id}")
    public void deleteProducto(@PathVariable int id) {
        productoService.eliminar(id);
    }
}