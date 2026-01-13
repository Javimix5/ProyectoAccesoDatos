package Controller;

import Model.Producto;
import Service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<Producto> getAllProductos() {
        return productoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Producto getProductoById(@PathVariable int id) {
        return productoService.buscarPorId(id);
    }

    @PostMapping
    public Producto createProducto(@RequestBody Producto producto) {
        return productoService.guardar(producto);
    }

    @DeleteMapping("/{id}")
    public void deleteProducto(@PathVariable int id) {
        productoService.eliminar(id);
    }
}