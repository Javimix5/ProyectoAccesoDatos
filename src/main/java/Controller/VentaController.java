package Controller;

import Model.Venta;
import Service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    @Autowired
    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public List<Venta> getAllVentas() {
        return ventaService.listarVentas();
    }

    @GetMapping("/{id}")
    public Venta getVentaById(@PathVariable int id) {
        return ventaService.buscarPorId(id);
    }

    @PostMapping
    public Venta createVenta(@RequestBody Venta venta) {
        return ventaService.crearVenta(venta);
    }
}