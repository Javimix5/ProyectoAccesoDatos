package Service;

import Model.Cliente;
import Repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente buscarPorId(int id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        return cliente.orElse(null);
    }

    public List<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }
    
    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }
    
    public void eliminar(int id) {
        clienteRepository.deleteById(id);
    }

    // Método añadido para compatibilidad con el menú
    public boolean insertar(Cliente cliente) {
        try {
            clienteRepository.save(cliente);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Método añadido para compatibilidad con el menú
    public Cliente buscarPorDNI(String dni) {
        // Implementación simple buscando en todos (idealmente usar un método en repositorio)
        return clienteRepository.findAll().stream()
                .filter(c -> dni.equalsIgnoreCase(c.getDni()))
                .findFirst()
                .orElse(null);
    }
}