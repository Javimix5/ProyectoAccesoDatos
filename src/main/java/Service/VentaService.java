package Service;

import Model.DetalleVenta;
import Model.Producto;
import Model.Venta;
import Repository.ProductoRepository;
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
    private final ProductoRepository productoRepository;

    @Autowired
    public VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public Venta crearVenta(Venta venta) {
        // 1. Asignar fecha si no viene
        if (venta.getFechaVenta() == null) {
            venta.setFechaVenta(LocalDateTime.now());
        }

        BigDecimal total = BigDecimal.ZERO;

        if (venta.getDetalles() != null) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                // 2. Vincular el detalle con la venta (Padre)
                detalle.setVenta(venta);

                // 3. RECUPERAR EL PRODUCTO REAL DE LA BD
                if (detalle.getProducto() != null) {
                    Producto productoReal = productoRepository.findById(detalle.getProducto().getId())
                            .orElseThrow(() -> new RuntimeException("Producto no encontrado ID: " + detalle.getProducto().getId()));

                    // 4. ACTUALIZAR STOCK
                    if (productoReal.getStock() < detalle.getCantidad()) {
                        throw new RuntimeException("Stock insuficiente para el producto: " + productoReal.getNombre());
                    }
                    productoReal.setStock(productoReal.getStock() - detalle.getCantidad());
                    productoRepository.save(productoReal); // Guardar el nuevo stock

                    // 5. Asignar el producto real al detalle y fijar precio
                    detalle.setProducto(productoReal);
                    detalle.setPrecioUnitario(productoReal.getPrecio());
                }

                // 6. Calcular subtotal
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