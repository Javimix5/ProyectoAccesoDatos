package Model;

public class Proveedor {
    private int id;
    private String nombre;
    private String emailContacto;

    public Proveedor() {
    }

    public Proveedor(String nombre, String emailContacto) {
        this.nombre = nombre;
        this.emailContacto = emailContacto;
    }

    public Proveedor(int id, String nombre, String emailContacto) {
        this.id = id;
        this.nombre = nombre;
        this.emailContacto = emailContacto;
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

    public String getEmailContacto() {
        return emailContacto;
    }

    public void setEmailContacto(String emailContacto) {
        this.emailContacto = emailContacto;
    }

    @Override
    public String toString() {
        return "Proveedor #" + id + ": " + nombre + " (Contacto: " + emailContacto + ")";
    }
}