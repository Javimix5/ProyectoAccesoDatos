package Service;

import Model.Compra;
import Model.DetalleCompra;
import Model.Producto;
import Repository.CompraRepository;
import Repository.ProductoRepository;
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
    private final ProductoRepository productoRepository;

    @Autowired
    public CompraService(CompraRepository compraRepository, ProductoRepository productoRepository) {
        this.compraRepository = compraRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public Compra crearCompra(Compra compra) {
        // 1. Asignar fecha si no viene
        if (compra.getFecha() == null) {
            compra.setFecha(LocalDateTime.now());
        }

        BigDecimal total = BigDecimal.ZERO;

        if (compra.getDetalles() != null) {
            for (DetalleCompra detalle : compra.getDetalles()) {
                // 2. Vincular el detalle con la compra (Padre)
                detalle.setCompra(compra);

                // 3. RECUPERAR EL PRODUCTO REAL DE LA BD
                if (detalle.getProducto() != null) {
                    Producto productoReal = productoRepository.findById(detalle.getProducto().getId())
                            .orElseThrow(() -> new RuntimeException("Producto no encontrado ID: " + detalle.getProducto().getId()));

                    // 4. ACTUALIZAR STOCK (Sumar)
                    productoReal.setStock(productoReal.getStock() + detalle.getCantidad());
                    productoRepository.save(productoReal);

                    // 5. Asignar el producto real al detalle
                    detalle.setProducto(productoReal);
                    // Si no viene precio, usar el del producto (opcional, en compras suele venir el precio de coste)
                    if (detalle.getPrecioUnitario() == null) {
                        detalle.setPrecioUnitario(productoReal.getPrecio());
                    }
                }

                // 6. Calcular subtotal
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