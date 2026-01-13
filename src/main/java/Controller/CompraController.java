package Controller;

import Model.Compra;
import Service.CompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    private final CompraService compraService;

    @Autowired
    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @GetMapping
    public List<Compra> getAllCompras() {
        return compraService.listarCompras();
    }

    @GetMapping("/{id}")
    public Compra getCompraById(@PathVariable int id) {
        return compraService.buscarPorId(id);
    }

    @PostMapping
    public Compra createCompra(@RequestBody Compra compra) {
        return compraService.crearCompra(compra);
    }
}