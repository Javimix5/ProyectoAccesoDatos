package Service.Implements;

import Dao.ProductoDAO;
import Model.Producto;
import Service.ProductoService;

import java.util.List;

public class ProductoServiceImpl implements ProductoService {
    private final ProductoDAO productoDAO = new ProductoDAO();

    @Override
    public List<Producto> obtenerInventario() {
        return productoDAO.obtenerInventario();
    }

    @Override
    public Producto buscarPorId(int id) {
        return productoDAO.buscarPorId(id);
    }

    @Override
    public List<Producto> obtenerPorProveedor(int idProveedor) {
        return productoDAO.obtenerPorProveedor(idProveedor);
    }
}
