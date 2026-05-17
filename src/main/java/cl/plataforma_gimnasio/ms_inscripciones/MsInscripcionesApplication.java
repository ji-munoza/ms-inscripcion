package cl.plataforma_gimnasio.ms_inscripciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MsInscripcionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsInscripcionesApplication.class, args);
	}

}
