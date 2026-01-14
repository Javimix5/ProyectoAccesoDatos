package Controller;

import Model.Proveedor;
import Service.ProveedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test", description = "Controlador de prueba")
public class TestController {

    private final ProveedorService proveedorService;

    @Autowired
    public TestController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @Operation(summary = "Obtener todos los proveedores")
    @GetMapping("/proveedores")
    public List<Proveedor> getProveedores() {
        return proveedorService.obtenerTodos();
    }
}