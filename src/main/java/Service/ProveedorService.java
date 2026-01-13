package Service;

import Model.Proveedor;
import Repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    @Autowired
    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public Proveedor buscarPorId(int id) {
        Optional<Proveedor> proveedor = proveedorRepository.findById(id);
        return proveedor.orElse(null);
    }

    public List<Proveedor> obtenerTodos() {
        return proveedorRepository.findAll();
    }
    
    public Proveedor guardar(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }
    
    public void eliminar(int id) {
        proveedorRepository.deleteById(id);
    }
}