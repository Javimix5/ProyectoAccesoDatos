package Service;

import Model.Cliente;
import java.util.List;

public interface ClienteService {
    List<Cliente> obtenerTodos();
    Cliente buscarPorId(int id);
    Cliente buscarPorDNI(String dni);
    boolean insertar(Cliente c);
    boolean actualizar(Cliente c);
    void eliminar(int id) throws BusinessException;
}
