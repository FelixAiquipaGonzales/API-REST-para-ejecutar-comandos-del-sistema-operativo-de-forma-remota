package app.sencico.edu.pe.gematica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "API de Ejecución de Comandos del Sistema",
        version = "1.0.0",
        description = "API REST para ejecutar comandos del sistema operativo de forma remota",
        contact = @Contact(
            name = "SENCICO - Área de Geomática",
            email = "geomatica@sencico.edu.pe"
        ),
        license = @License(
            name = "Apache 2.0",
            url = "http://www.apache.org/licenses/LICENSE-2.0"
        )
    )
)
public class GematicaApplication {

	public static void main(String[] args) {
		SpringApplication.run(GematicaApplication.class, args);
	}

}
