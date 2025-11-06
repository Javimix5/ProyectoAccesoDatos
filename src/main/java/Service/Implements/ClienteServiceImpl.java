package Service.Implements;

import Dao.ClienteDAO;
import Dao.VentaDAO;
import Model.Cliente;
import Model.Venta;
import Service.ClienteService;
import Service.BusinessException;
import Service.TransaccionesService;

import java.util.List;

public class ClienteServiceImpl implements ClienteService {
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final VentaDAO ventaDAO = new VentaDAO();
    private final TransaccionesService txService;

    public ClienteServiceImpl() {
        this.txService = null;
    }

    public ClienteServiceImpl(TransaccionesService txService) {
        this.txService = txService;
    }

    @Override
    public List<Cliente> obtenerTodos() {
        return clienteDAO.obtenerTodos();
    }

    @Override
    public Cliente buscarPorId(int id) {
        return clienteDAO.buscarPorId(id);
    }

    @Override
    public Cliente buscarPorDNI(String dni) {
        return clienteDAO.buscarPorDNI(dni);
    }

    @Override
    public boolean insertar(Cliente c) {
        try {
            if (c.getDni() != null && !Model.Cliente.isValidDni(c.getDni())) {
                System.err.println("DNI inválido antes de insertar: " + c.getDni());
                return false;
            }
            if (c.getEmail() != null && !Model.Cliente.isValidEmail(c.getEmail())) {
                System.err.println("Email inválido antes de insertar: " + c.getEmail());
                return false;
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Validación cliente: " + e.getMessage());
            return false;
        }

        if (txService != null) {
            try {
                Cliente inserted = txService.insertarClienteTransaccional(c);
                return inserted != null;
            } catch (Exception e) {
                System.err.println("Error insertar cliente transaccional: " + e.getMessage());
                return false;
            }
        }
        try {
            return clienteDAO.insertar(c);
        } catch (Exception e) {
            System.err.println("Error insertar cliente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Cliente c) {
        try {
            if (c.getDni() != null && !Model.Cliente.isValidDni(c.getDni())) {
                System.err.println("DNI inválido antes de actualizar: " + c.getDni());
                return false;
            }
            if (c.getEmail() != null && !Model.Cliente.isValidEmail(c.getEmail())) {
                System.err.println("Email inválido antes de actualizar: " + c.getEmail());
                return false;
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Validación cliente: " + e.getMessage());
            return false;
        }
        return clienteDAO.actualizar(c);
    }

    @Override
    public void eliminar(int id) throws BusinessException {
        if (txService != null) {
            try {
                boolean ok = txService.eliminarClienteTransaccional(id);
                if (!ok) throw new BusinessException("Error al eliminar cliente (transacción).");
                return;
            } catch (Service.BusinessException be) {
                throw be;
            } catch (Exception e) {
                throw new BusinessException("Error al eliminar cliente: " + e.getMessage());
            }
        }

        List<Venta> ventas = ventaDAO.listarVentas();
        boolean tieneVentas = ventas.stream()
                .anyMatch(v -> v.getCliente() != null && v.getCliente().getId() == id);
        if (tieneVentas) {
            throw new BusinessException("No se puede eliminar: el cliente tiene ventas asociadas.");
        }

        boolean ok = clienteDAO.eliminar(id);
        if (!ok) {
            throw new BusinessException("Error al eliminar cliente (DAO devolvió false).");
        }
    }
}