package Controller;

import Model.Cliente;
import Service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Cliente", description = "Controlador para gestionar clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Operation(summary = "Obtener todos los clientes")
    @GetMapping
    public List<Cliente> getAllClientes() {
        return clienteService.obtenerTodos();
    }

    @Operation(summary = "Obtener cliente por ID")
    @GetMapping("/{id}")
    public Cliente getClienteById(@PathVariable int id) {
        return clienteService.buscarPorId(id);
    }

    @Operation(summary = "Crear un nuevo cliente")
    @PostMapping
    public Cliente createCliente(@RequestBody Cliente cliente) {
        return clienteService.guardar(cliente);
    }

    @Operation(summary = "Eliminar cliente por ID")
    @DeleteMapping("/{id}")
    public void deleteCliente(@PathVariable int id) {
        clienteService.eliminar(id);
    }
}