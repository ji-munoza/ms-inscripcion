package cl.plataforma_gimnasio.ms_inscripcion.service;

import cl.plataforma_gimnasio.ms_inscripcion.dto.InscripcionRequestDTO;
import cl.plataforma_gimnasio.ms_inscripcion.dto.InscripcionResponseDTO;
import cl.plataforma_gimnasio.ms_inscripcion.dto.PlanResponseDTO;
import cl.plataforma_gimnasio.ms_inscripcion.dto.SocioResponseDTO;
import cl.plataforma_gimnasio.ms_inscripcion.exception.ResourceNotFoundException;
import cl.plataforma_gimnasio.ms_inscripcion.model.Inscripcion;
import cl.plataforma_gimnasio.ms_inscripcion.repository.InscripcionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final WebClient.Builder webClientBuilder;

    public List<InscripcionResponseDTO> obtenerTodos() {
        log.info("Iniciando obtencion de todas las inscripciones");
        return inscripcionRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public InscripcionResponseDTO obtenerPorId(Integer id) {
        log.info("Iniciando obtencion de inscripcion con ID: {}", id);
        return inscripcionRepository.findById(id)
                .map(this::convertirAResponseDTO)
                .orElseThrow(() -> {
                    log.error("Error al obtener inscripcion: El ID {} no existe.", id);
                    return new ResourceNotFoundException("La inscripcion con ID " + id + " no existe.");
                });
    }

    public InscripcionResponseDTO guardar(InscripcionRequestDTO dto) {
        log.info("Iniciando registro de inscripcion en sede: {} para Socio ID: {}", dto.getSedeInscripcion(), dto.getIdSocio());
        log.info("Validando existencia del socio mediante WebClient...");
        SocioResponseDTO socio = webClientBuilder.build()
                .get()
                .uri("http://ms-socio/api/gimnasio/socios/{id}", dto.getIdSocio())
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.NOT_FOUND), response -> {
                    log.error("Error WebClient: El Socio con ID {} no existe en ms-socio.", dto.getIdSocio());
                    return Mono.error(new ResourceNotFoundException("El socio con ID " + dto.getIdSocio() + " no existe."));
                })
                .bodyToMono(SocioResponseDTO.class)
                .block();

        log.info("Validando existencia del plan mediante WebClient...");
        PlanResponseDTO plan = webClientBuilder.build()
                .get()
                .uri("http://ms-plan/api/gimnasio/planes/{id}", dto.getIdPlan())
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.NOT_FOUND), response -> {
                    log.error("Error WebClient: El Plan con ID {} no existe en ms-plan.", dto.getIdPlan());
                    return Mono.error(new ResourceNotFoundException("El plan con ID " + dto.getIdPlan() + " no existe."));
                })
                .bodyToMono(PlanResponseDTO.class)
                .block();

        log.info("Validaciones HTTP exitosas para Socio '{}-{}' y Plan '{}'. Procediendo a guardar.",
                socio.getRutSocio(), socio.getDvSocio(), plan.getNombrePlan());

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setSedeInscripcion(dto.getSedeInscripcion());
        inscripcion.setEstadoInscripcion(dto.getEstadoInscripcion());
        inscripcion.setIdPlan(dto.getIdPlan());
        inscripcion.setIdSocio(dto.getIdSocio());
        inscripcion.setIdPago(dto.getIdPago());

        Inscripcion inscripcionGuardada = inscripcionRepository.save(inscripcion);
        log.info("Inscripcion guardada con exito. Nuevo ID asignado: {}", inscripcionGuardada.getIdInscripcion());

        return convertirAResponseDTO(inscripcionGuardada);
    }

    public InscripcionResponseDTO actualizar(Integer id, InscripcionRequestDTO dto) {
        log.info("Iniciando actualizacion de inscripcion con ID: {}", id);

        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Error al actualizar: El ID {} no existe.", id);
                    return new ResourceNotFoundException("La inscripcion con ID " + id + " no existe.");
                });

        log.info("Validando IDs actualizados en servicios remotos...");
        webClientBuilder.build().get().uri("http://ms-socio/api/gimnasio/socios/{id}", dto.getIdSocio()).retrieve()
                .onStatus(status -> status.equals(HttpStatus.NOT_FOUND), r -> Mono.error(new ResourceNotFoundException("El socio con ID " + dto.getIdSocio() + " no existe.")))
                .bodyToMono(SocioResponseDTO.class).block();

        webClientBuilder.build().get().uri("http://ms-plan/api/gimnasio/planes/{id}", dto.getIdPlan()).retrieve()
                .onStatus(status -> status.equals(HttpStatus.NOT_FOUND), r -> Mono.error(new ResourceNotFoundException("El plan con ID " + dto.getIdPlan() + " no existe.")))
                .bodyToMono(PlanResponseDTO.class).block();

        inscripcion.setSedeInscripcion(dto.getSedeInscripcion());
        inscripcion.setEstadoInscripcion(dto.getEstadoInscripcion());
        inscripcion.setIdPlan(dto.getIdPlan());
        inscripcion.setIdSocio(dto.getIdSocio());
        inscripcion.setIdPago(dto.getIdPago());

        Inscripcion inscripcionActualizada = inscripcionRepository.save(inscripcion);
        log.info("Inscripcion con ID {} actualizada con exito.", id);

        return convertirAResponseDTO(inscripcionActualizada);
    }

    public void eliminar(Integer id) {
        log.warn("Iniciando eliminacion de inscripcion con ID: {}", id);

        if (!inscripcionRepository.existsById(id)) {
            log.error("Error al eliminar: No existe la inscripcion con ID: {}", id);
            throw new ResourceNotFoundException("La inscripcion con ID " + id + " no existe.");
        }

        inscripcionRepository.deleteById(id);
        log.info("Inscripcion con ID {} eliminada correctamente.", id);
    }

    private InscripcionResponseDTO convertirAResponseDTO(Inscripcion inscripcion) {
        InscripcionResponseDTO response = new InscripcionResponseDTO();
        response.setIdInscripcion(inscripcion.getIdInscripcion());
        response.setSedeInscripcion(inscripcion.getSedeInscripcion());
        response.setEstadoInscripcion(inscripcion.getEstadoInscripcion());
        response.setIdPlan(inscripcion.getIdPlan());
        response.setIdSocio(inscripcion.getIdSocio());
        response.setIdPago(inscripcion.getIdPago());
        return response;
    }
}