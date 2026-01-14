package Service;

import Model.DetalleVenta;
import Model.Venta;
import Repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        // Asignar fecha si no viene
        if (venta.getFechaVenta() == null) {
            venta.setFechaVenta(LocalDateTime.now());
        }

        // Vincular detalles con la venta y recalcular total
        BigDecimal total = BigDecimal.ZERO;
        if (venta.getDetalles() != null) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                detalle.setVenta(venta); // IMPORTANTE: Vincular el hijo con el padre
                if (detalle.getSubtotal() != null) {
                    total = total.add(detalle.getSubtotal());
                }
            }
        }
        venta.setTotalImporte(total);

        return ventaRepository.save(venta);
    }

    @Transactional(readOnly = true)
    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Venta buscarPorId(int id) {
        Optional<Venta> venta = ventaRepository.findById(id);
        return venta.orElse(null);
    }
}