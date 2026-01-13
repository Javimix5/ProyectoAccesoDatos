package Controller;

import Model.Proveedor;
import Service.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    @Autowired
    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public List<Proveedor> getAllProveedores() {
        return proveedorService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Proveedor getProveedorById(@PathVariable int id) {
        return proveedorService.buscarPorId(id);
    }

    @PostMapping
    public Proveedor createProveedor(@RequestBody Proveedor proveedor) {
        return proveedorService.guardar(proveedor);
    }

    @DeleteMapping("/{id}")
    public void deleteProveedor(@PathVariable int id) {
        proveedorService.eliminar(id);
    }
}