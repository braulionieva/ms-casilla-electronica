package pe.gob.mpfn.casilla.notifications.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class AppConfig {

    @Bean
    @Profile("!prod")
    public OpenAPI usersMicroserviceOpenAPI() {

        Contact contact = new Contact();

        contact.setName("OGTI");
        contact.setUrl("http://www.mpfn.gob.pe");

        return new OpenAPI()
                .info(new Info()
                        .title("Casilla Electrónica - Bandeja")
                        .description("Servicio que permite validar y afiliar un ciudadano a la Casilla electrónica")
                        .version("1.0")
                        .contact(contact)
                );
    }


}
