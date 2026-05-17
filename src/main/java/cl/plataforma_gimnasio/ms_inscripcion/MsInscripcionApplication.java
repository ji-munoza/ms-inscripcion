package cl.plataforma_gimnasio.ms_inscripcion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MsInscripcionApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsInscripcionApplication.class, args);
    }

}