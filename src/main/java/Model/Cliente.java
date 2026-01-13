package Model;

import jakarta.persistence.*;
import java.util.regex.Pattern;

@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String telefono;
    private String email;
    private String direccion;
    private String dni;

    @Transient
    private static final Pattern DNI_PATTERN = Pattern.compile("^[0-9]{8}[A-Za-z]$");
    @Transient
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Cliente() {
    }

    public Cliente(int id, String nombre, String apellido1, String apellido2, String telefono, String email, String direccion, String dni) {
        this.id = id;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.telefono = telefono;
        setEmail(email);
        this.direccion = direccion;
        setDni(dni);
    }

    public static boolean isValidDni(String dni) {
        if (dni == null) return false;
        return DNI_PATTERN.matcher(dni.trim()).matches();
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
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

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && !email.trim().isEmpty() && !isValidEmail(email)) {
            throw new IllegalArgumentException("Email no válido: " + email);
        }
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        if (dni != null && !dni.trim().isEmpty() && !isValidDni(dni)) {
            throw new IllegalArgumentException("DNI no válido: " + dni);
        }
        this.dni = dni;
    }

    @Override
    public String toString() {
        return "Cliente #" + id + ": " + nombre + " " + apellido1 + " " + apellido2 +
                " | DNI: " + dni + " | Email: " + email + " | Dirección: " + direccion + " | Teléfono: " + telefono;
    }
}