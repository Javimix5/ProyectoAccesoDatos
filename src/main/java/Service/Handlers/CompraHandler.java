package Service.Handlers;

import Model.Compra;
import Model.DetalleCompra;
import Model.Producto;
import Model.Proveedor;
import Service.BusinessException;
import Service.CompraService;
import Service.ProductoService;
import Service.ProveedorService;

import java.math.BigDecimal;
import java.util.List;

public class CompraHandler {
    private final CompraService compraService;
    private final ProductoService productoService;
    private final ProveedorService proveedorService;

    public CompraHandler(CompraService compraService,
                         ProductoService productoService,
                         ProveedorService proveedorService) {
        this.compraService = compraService;
        this.productoService = productoService;
        this.proveedorService = proveedorService;
    }

    public boolean crearCompra(Compra compra) throws BusinessException {
        if (compra == null) throw new BusinessException("Compra nula.");
        Proveedor prov = compra.getProveedor();
        if (prov == null || prov.getId() <= 0) throw new BusinessException("Compra debe tener un proveedor válido.");
        Proveedor provBD = proveedorService.buscarPorId(prov.getId());
        if (provBD == null) throw new BusinessException("Proveedor no existe: " + prov.getId());

        List<DetalleCompra> detalles = compra.getDetalles();
        if (detalles == null || detalles.isEmpty()) throw new BusinessException("Compra sin detalles.");

        for (DetalleCompra d : detalles) {
            if (d == null) throw new BusinessException("Detalle inválido.");
            Producto p = d.getProducto();
            if (p == null) throw new BusinessException("Detalle sin producto.");
            Producto prodBD = productoService.buscarPorId(p.getId());
            if (prodBD == null) throw new BusinessException("Producto no existe: " + p.getId());
            if (d.getCantidad() <= 0) throw new BusinessException("Cantidad inválida para producto " + p.getId());
            BigDecimal precio = d.getPrecioUnitario();
            if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0)
                throw new BusinessException("Precio unitario inválido para producto " + p.getId());
        }

        compraService.crearCompra(compra);
        return true;
    }

    public List<Compra> listarCompras() {
        return compraService.listarCompras();
    }

    public Compra buscarPorId(int id) {
        return compraService.buscarPorId(id);
    }
}