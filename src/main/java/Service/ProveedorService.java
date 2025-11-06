package Service;

import Model.Proveedor;
import java.util.List;

public interface ProveedorService {
    List<Proveedor> obtenerTodos();
    Proveedor buscarPorId(int id);
}
