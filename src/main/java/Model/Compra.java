package Model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "compras")
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;
    
    private LocalDateTime fecha;
    
    @Column(name = "total_importe")
    private BigDecimal totalImporte = BigDecimal.ZERO;
    
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCompra> detalles = new ArrayList<>();

    public Compra() {
    }

    public Compra(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Compra(int id, Proveedor proveedor, LocalDateTime fecha, BigDecimal totalImporte) {
        this.id = id;
        this.proveedor = proveedor;
        this.fecha = fecha;
        this.totalImporte = totalImporte != null ? totalImporte : BigDecimal.ZERO;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotalImporte() {
        return totalImporte;
    }

    public void setTotalImporte(BigDecimal totalImporte) {
        this.totalImporte = totalImporte != null ? totalImporte : BigDecimal.ZERO;
    }

    public List<DetalleCompra> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCompra> detalles) {
        this.detalles = detalles != null ? detalles : new ArrayList<>();
    }

    public void agregarDetalle(DetalleCompra d) {
        if (d == null) return;
        d.setCompra(this);
        detalles.add(d);
        if (d.getPrecioUnitario() != null) {
            totalImporte = totalImporte.add(d.getPrecioUnitario().multiply(java.math.BigDecimal.valueOf(d.getCantidad())));
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String fechaStr = (fecha != null) ? fecha.format(fmt) : "N/A";
        String provNombre = (proveedor != null ? proveedor.getNombre() : "N/A");
        return "Compra #" + id + " | Fecha: " + fechaStr + " | Proveedor: " + provNombre + " | Total: " + totalImporte
                + " | Detalles: " + (detalles != null ? detalles.size() : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Compra)) return false;
        Compra compra = (Compra) o;
        return id == compra.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}