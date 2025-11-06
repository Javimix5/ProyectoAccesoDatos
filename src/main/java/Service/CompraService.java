package Service;

import Model.Compra;
import java.util.List;

public interface CompraService {
    boolean crearCompra(Compra compra);
    List<Compra> listarCompras();
    Compra buscarPorId(int id);
}
