package Service;

import Model.Producto;
import java.util.List;

public interface ProductoService {
    List<Producto> obtenerInventario();
    Producto buscarPorId(int id);
    List<Producto> obtenerPorProveedor(int idProveedor);
}
