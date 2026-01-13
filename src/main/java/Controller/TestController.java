package Controller;

import Model.Proveedor;
import Service.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final ProveedorService proveedorService;

    @Autowired
    public TestController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping("/proveedores")
    public List<Proveedor> getProveedores() {
        return proveedorService.obtenerTodos();
    }
}