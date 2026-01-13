import Service.*;
import Service.Handlers.CompraHandler;
import Service.Handlers.VentaHandler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"App", "Service", "Repository", "Controller", "Model"})
@EntityScan(basePackages = "Model")
@EnableJpaRepositories(basePackages = "Repository")
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ClienteService clienteService,
                                               ProductoService productoService,
                                               ProveedorService proveedorService,
                                               VentaService ventaService,
                                               CompraService compraService) {
        return args -> {
            boolean consoleMode = false;
            for (String arg : args) {
                if ("--console".equals(arg)) {
                    consoleMode = true;
                    break;
                }
            }

            if (consoleMode) {
                VentaHandler ventaHandler = new VentaHandler(ventaService, productoService);
                CompraHandler compraHandler = new CompraHandler(compraService, productoService, proveedorService);

                Menu menu = new Menu(clienteService, productoService, proveedorService,
                        ventaHandler, compraHandler);
                menu.start();
            } else {
                System.out.println("Aplicación iniciada en modo Web API.");
                System.out.println("Acceda a la documentación en: http://localhost:8080/swagger-ui/index.html");
                System.out.println("Para modo consola, ejecute con el argumento: --console");
            }
        };
    }
}