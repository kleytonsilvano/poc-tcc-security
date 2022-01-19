package tcc.poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;

@SpringBootApplication
@ImportResource("classpath:flow-definition.xml")
public class App extends AuthorizationServerConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(tcc.poc.App.class, args);
    }

}
