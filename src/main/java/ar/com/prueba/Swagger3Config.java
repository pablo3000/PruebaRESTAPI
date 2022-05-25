package ar.com.prueba;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;


@Configuration
public class Swagger3Config {

	@Bean
	public OpenAPI initOpenAPI() {
	return new OpenAPI().info(
	new Info()
	.title("prueba Rest")
	.description("API rest de Prueba")
	.version("v1.0")
	.contact(new Contact()
		      .name("Aca va el nombre")
		      .email("prueba@gmail.com")
		      .url("https://www.google.com.ar/"))
	);
	}
	}