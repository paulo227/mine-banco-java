package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import util.DatabaseManager;
import javax.annotation.PostConstruct;

@SpringBootApplication
@ComponentScan(basePackages = {"app", "controller", "model", "service", "util"})
public class MineBancoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MineBancoApplication.class, args);
    }

    @PostConstruct
    public void init() {
        DatabaseManager.inicializarTabelas();
    }
}
