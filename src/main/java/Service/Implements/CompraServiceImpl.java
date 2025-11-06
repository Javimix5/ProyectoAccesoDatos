package Service.Implements;

import Dao.CompraDAO;
import Model.Compra;
import Service.CompraService;
import Service.TransaccionesService;

import java.util.List;

public class CompraServiceImpl implements CompraService {
    private final CompraDAO compraDAO = new CompraDAO();
    private final TransaccionesService txService;

    public CompraServiceImpl() {
        this.txService = null;
    }

    public CompraServiceImpl(TransaccionesService txService) {
        this.txService = txService;
    }

    @Override
    public boolean crearCompra(Compra compra) {
        if (txService != null) {
            try {
                return txService.crearCompraTransaccional(compra);
            } catch (Exception e) {
                System.err.println("Error crearCompra transaccional: " + e.getMessage());
                return false;
            }
        }
        return compraDAO.crearCompra(compra);
    }

    @Override
    public List<Compra> listarCompras() {
        return compraDAO.listarCompras();
    }

    @Override
    public Compra buscarPorId(int id) {
        return compraDAO.listarCompras().stream()
                .filter(c -> c.getId() == id)
                .findFirst().orElse(null);
    }
}
