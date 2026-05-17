package cl.plataforma_gimnasio.ms_inscripcion.controller;

import cl.plataforma_gimnasio.ms_inscripcion.dto.InscripcionRequestDTO;
import cl.plataforma_gimnasio.ms_inscripcion.dto.InscripcionResponseDTO;
import cl.plataforma_gimnasio.ms_inscripcion.service.InscripcionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gimnasio/inscripciones")
@RequiredArgsConstructor
@Slf4j
public class InscripcionController {

    private final InscripcionService inscripcionService;

    @GetMapping
    public ResponseEntity<List<InscripcionResponseDTO>> obtenerTodos() {
        log.info("Solicitud recibida para obtener todas las inscripciones");
        List<InscripcionResponseDTO> inscripciones = inscripcionService.obtenerTodos();
        if (inscripciones.isEmpty()) {
            log.warn("No se obtuvo ninguna inscripcion, la lista esta vacia");
            return ResponseEntity.noContent().build();
        }
        log.info("Obtenidas {} inscripciones", inscripciones.size());
        return ResponseEntity.ok(inscripciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InscripcionResponseDTO> obtenerPorId(@PathVariable Integer id) {
        log.info("Solicitud recibida para obtener inscripcion con ID: {}", id);
        InscripcionResponseDTO inscripcion = inscripcionService.obtenerPorId(id);
        log.info("Obtenida inscripcion con ID: {}", id);
        return ResponseEntity.ok(inscripcion);
    }

    @PostMapping
    public ResponseEntity<InscripcionResponseDTO> guardar(@Valid @RequestBody InscripcionRequestDTO dto) {
        log.info("Solicitud recibida para crear inscripcion en la sede: {} para Socio ID: {}", dto.getSedeInscripcion(), dto.getIdSocio());
        return ResponseEntity.status(HttpStatus.CREATED).body(inscripcionService.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InscripcionResponseDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody InscripcionRequestDTO dto) {
        log.info("Solicitud recibida para actualizar inscripcion con ID: {}", id);
        InscripcionResponseDTO inscripcionActualizada = inscripcionService.actualizar(id, dto);
        log.info("Inscripcion con ID {} modificada exitosamente.", id);
        return ResponseEntity.ok(inscripcionActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        log.warn("Solicitud recibida para eliminar inscripcion con ID: {}", id);
        inscripcionService.eliminar(id);
        log.info("Eliminada inscripcion con ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}