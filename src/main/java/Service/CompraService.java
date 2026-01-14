package Service;

import Model.Compra;
import Model.DetalleCompra;
import Repository.CompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CompraService {

    private final CompraRepository compraRepository;

    @Autowired
    public CompraService(CompraRepository compraRepository) {
        this.compraRepository = compraRepository;
    }

    @Transactional
    public Compra crearCompra(Compra compra) {
        // Asignar fecha si no viene
        if (compra.getFecha() == null) {
            compra.setFecha(LocalDateTime.now());
        }

        // Vincular detalles con la compra y recalcular total
        BigDecimal total = BigDecimal.ZERO;
        if (compra.getDetalles() != null) {
            for (DetalleCompra detalle : compra.getDetalles()) {
                detalle.setCompra(compra); // IMPORTANTE: Vincular el hijo con el padre
                if (detalle.getSubtotal() != null) {
                    total = total.add(detalle.getSubtotal());
                }
            }
        }
        compra.setTotalImporte(total);

        return compraRepository.save(compra);
    }

    @Transactional(readOnly = true)
    public List<Compra> listarCompras() {
        return compraRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Compra buscarPorId(int id) {
        Optional<Compra> compra = compraRepository.findById(id);
        return compra.orElse(null);
    }
}