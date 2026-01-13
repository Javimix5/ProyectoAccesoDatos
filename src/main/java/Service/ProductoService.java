package Service;

import Model.Producto;
import Repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Autowired
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public Producto buscarPorId(int id) {
        Optional<Producto> producto = productoRepository.findById(id);
        return producto.orElse(null);
    }

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }
    
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }
    
    public void eliminar(int id) {
        productoRepository.deleteById(id);
    }

    // Método añadido para compatibilidad con el menú
    public List<Producto> obtenerInventario() {
        return obtenerTodos();
    }

    // Método añadido para compatibilidad con el menú
    public List<Producto> obtenerPorProveedor(int idProveedor) {
        return obtenerTodos().stream()
                .filter(p -> p.getIdProveedor() != null && p.getIdProveedor() == idProveedor)
                .collect(Collectors.toList());
    }
}