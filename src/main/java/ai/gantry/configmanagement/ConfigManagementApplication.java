package ai.gantry.configmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackages = { "ai.gantry.configmanagement" , "io.swagger.configuration"})
public class ConfigManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigManagementApplication.class, args);
    }

}
