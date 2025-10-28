import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Main.class, args);

        // Executa migração Flyway usando o DataSource configurado no application.properties
        DataSource ds = ctx.getBean(DataSource.class);
        Flyway flyway = Flyway.configure().dataSource(ds).load();
        flyway.migrate();

        // resto da inicialização...
    }
}

