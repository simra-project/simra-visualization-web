package visualization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class VisualizationAPI {

    public static void main(String[] args) {
        SpringApplication.run(VisualizationAPI.class, args);
    }
}
