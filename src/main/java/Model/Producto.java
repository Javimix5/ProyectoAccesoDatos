package Model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String nombre;
    private String coleccion;
    private int stock;
    private BigDecimal precio;
    
    @Column(name = "id_proveedor")
    private Integer idProveedor;

    public Producto() {
    }

    public Producto(int id, String nombre, String coleccion, int stock, BigDecimal precio, Integer idProveedor) {
        this.id = id;
        this.nombre = nombre;
        this.coleccion = coleccion;
        this.stock = stock;
        this.precio = precio;
        this.idProveedor = idProveedor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getColeccion() {
        return coleccion;
    }

    public void setColeccion(String coleccion) {
        this.coleccion = coleccion;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    @Override
    public String toString() {
        return "Producto #" + id + " | " + nombre + " | Colección: " + (coleccion != null ? coleccion : "N/A")
                + " | Stock: " + stock + " | Precio: " + precio + " | ProveedorId: " + (idProveedor != null ? idProveedor : "N/A");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Producto)) return false;
        Producto producto = (Producto) o;
        return id == producto.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}