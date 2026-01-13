package Service;

import Model.Venta;
import Repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;

    @Autowired
    public VentaService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    @Transactional
    public Venta crearVenta(Venta venta) {
        return ventaRepository.save(venta);
    }

    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    public Venta buscarPorId(int id) {
        Optional<Venta> venta = ventaRepository.findById(id);
        return venta.orElse(null);
    }
}