package Service;

import Model.Venta;
import java.util.List;

public interface VentaService {
    boolean crearVenta(Venta venta);
    List<Venta> listarVentas();
    Venta buscarPorId(int id);
}
