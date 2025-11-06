package Service.Implements;

import Dao.VentaDAO;
import Model.Venta;
import Service.VentaService;
import Service.TransaccionesService;

import java.util.List;

public class VentaServiceImpl implements VentaService {
    private final VentaDAO ventaDAO = new VentaDAO();
    private final TransaccionesService txService;

    public VentaServiceImpl() {
        this.txService = null;
    }

    public VentaServiceImpl(TransaccionesService txService) {
        this.txService = txService;
    }

    @Override
    public boolean crearVenta(Venta venta) {
        if (txService != null) {
            try {
                return txService.crearVentaTransaccional(venta);
            } catch (Exception e) {
                System.err.println("Error crearVenta transaccional: " + e.getMessage());
                return false;
            }
        }
        return ventaDAO.crearVenta(venta);
    }

    @Override
    public List<Venta> listarVentas() {
        return ventaDAO.listarVentas();
    }

    @Override
    public Venta buscarPorId(int id) {
        return ventaDAO.listarVentas().stream()
                .filter(v -> v.getNumFactura() == id)
                .findFirst().orElse(null);
    }
}
