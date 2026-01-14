package Controller;

import Model.Proveedor;
import Service.ProveedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@Tag(name = "Proveedor", description = "Controlador para gestionar proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    @Autowired
    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @Operation(summary = "Obtener todos los proveedores")
    @GetMapping
    public List<Proveedor> getAllProveedores() {
        return proveedorService.obtenerTodos();
    }

    @Operation(summary = "Obtener proveedor por ID")
    @GetMapping("/{id}")
    public Proveedor getProveedorById(@PathVariable int id) {
        return proveedorService.buscarPorId(id);
    }

    @Operation(summary = "Crear un nuevo proveedor")
    @PostMapping
    public Proveedor createProveedor(@RequestBody Proveedor proveedor) {
        return proveedorService.guardar(proveedor);
    }

    @Operation(summary = "Eliminar proveedor por ID")
    @DeleteMapping("/{id}")
    public void deleteProveedor(@PathVariable int id) {
        proveedorService.eliminar(id);
    }
}