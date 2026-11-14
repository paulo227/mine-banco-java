package app;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@OpenAPIDefinition(
    info = @Info(
        title = "MineBanco API",
        version = "1.0.0",
        description = "API REST para operações bancárias: contas correntes e poupanças.",
        contact = @Contact(name = "MineBanco Dev Team")
    )
)
@SpringBootApplication
@ComponentScan(basePackages = {"app", "controller", "exception", "model", "service", "util"})
public class MineBancoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MineBancoApplication.class, args);
    }
}
