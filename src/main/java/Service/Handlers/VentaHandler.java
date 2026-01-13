package Service.Handlers;

import Model.DetalleVenta;
import Model.Producto;
import Model.Venta;
import Service.BusinessException;
import Service.ProductoService;
import Service.VentaService;

import java.util.List;

public class VentaHandler {
    private final VentaService ventaService;
    private final ProductoService productoService;

    public VentaHandler(VentaService ventaService, ProductoService productoService) {
        this.ventaService = ventaService;
        this.productoService = productoService;
    }

    public boolean crearVenta(Venta venta) throws BusinessException {
        if (venta == null) throw new BusinessException("Venta nula.");
        if (venta.getCliente() == null) throw new BusinessException("Venta debe tener un cliente.");
        List<DetalleVenta> detalles = venta.getDetalles();
        if (detalles == null || detalles.isEmpty()) throw new BusinessException("Venta sin detalles.");

        for (DetalleVenta d : detalles) {
            if (d == null) throw new BusinessException("Detalle inválido.");
            Producto p = d.getProducto();
            if (p == null) throw new BusinessException("Detalle sin producto.");
            Producto prodBD = productoService.buscarPorId(p.getId());
            if (prodBD == null) throw new BusinessException("Producto no existe: " + p.getId());
            if (d.getCantidad() <= 0) throw new BusinessException("Cantidad inválida para producto " + p.getId());
            if (prodBD.getStock() < d.getCantidad()) {
                throw new BusinessException("Stock insuficiente para producto " + p.getId());
            }
        }

        ventaService.crearVenta(venta);
        return true;
    }

    public List<Venta> listarVentas() {
        return ventaService.listarVentas();
    }

    public Venta buscarPorId(int id) {
        return ventaService.buscarPorId(id);
    }
}