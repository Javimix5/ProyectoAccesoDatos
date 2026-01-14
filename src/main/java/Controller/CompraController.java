package Controller;

import Model.Compra;
import Service.CompraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras")
@Tag(name = "Compra", description = "Controlador para gestionar compras")
public class CompraController {

    private final CompraService compraService;

    @Autowired
    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @Operation(summary = "Obtener todas las compras")
    @GetMapping
    public List<Compra> getAllCompras() {
        return compraService.listarCompras();
    }

    @Operation(summary = "Obtener compra por ID")
    @GetMapping("/{id}")
    public Compra getCompraById(@PathVariable int id) {
        return compraService.buscarPorId(id);
    }

    @Operation(summary = "Crear una nueva compra")
    @PostMapping
    public Compra createCompra(@RequestBody Compra compra) {
        return compraService.crearCompra(compra);
    }
}