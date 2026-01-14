package Controller;

import Model.Venta;
import Service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@Tag(name = "Venta", description = "Controlador para gestionar ventas")
public class VentaController {

    private final VentaService ventaService;

    @Autowired
    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @Operation(summary = "Obtener todas las ventas")
    @GetMapping
    public List<Venta> getAllVentas() {
        return ventaService.listarVentas();
    }

    @Operation(summary = "Obtener venta por ID")
    @GetMapping("/{id}")
    public Venta getVentaById(@PathVariable int id) {
        return ventaService.buscarPorId(id);
    }

    @Operation(summary = "Crear una nueva venta")
    @PostMapping
    public Venta createVenta(@RequestBody Venta venta) {
        return ventaService.crearVenta(venta);
    }
}