package Service.Implements;

import Dao.ProveedorDAO;
import Model.Proveedor;
import Service.ProveedorService;

import java.util.List;

public class ProveedorServiceImpl implements ProveedorService {
    private final ProveedorDAO proveedorDAO = new ProveedorDAO();

    @Override
    public List<Proveedor> obtenerTodos() {
        return proveedorDAO.obtenerTodos();
    }

    @Override
    public Proveedor buscarPorId(int id) {
        return proveedorDAO.buscarPorId(id);
    }
}
