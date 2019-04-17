package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageProperties.class,
})
public class Application {

    public static void main(String[] args) throws InterruptedException {

        SpringApplication.run(Application.class, args);

    }
}