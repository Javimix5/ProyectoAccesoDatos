package Model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_compra")
public class DetalleCompra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;
    
    private int cantidad;
    
    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;
    
    @ManyToOne
    @JoinColumn(name = "id_compra")
    private Compra compra;

    public DetalleCompra() {
    }

    public DetalleCompra(Producto producto, int cantidad, BigDecimal precioUnitario) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

    public BigDecimal getSubtotal() {
        if (precioUnitario == null) return BigDecimal.ZERO;
        return precioUnitario.multiply(new BigDecimal(cantidad));
    }
}