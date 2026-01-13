import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class ApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetClientes() throws Exception {
        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetProductos() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testGetProveedores() throws Exception {
        mockMvc.perform(get("/api/proveedores"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetVentas() throws Exception {
        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCompras() throws Exception {
        mockMvc.perform(get("/api/compras"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testSwaggerUi() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testApiDocs() throws Exception {
        // La ruta se ha configurado como /API_DOC.json en application.properties
        mockMvc.perform(get("/API_DOC.json"))
                .andExpect(status().isOk());
    }
}