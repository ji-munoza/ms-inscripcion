package cl.plataforma_gimnasio.ms_inscripcion.service;

import cl.plataforma_gimnasio.ms_inscripcion.dto.InscripcionRequestDTO;
import cl.plataforma_gimnasio.ms_inscripcion.dto.InscripcionResponseDTO;
import cl.plataforma_gimnasio.ms_inscripcion.dto.PagoResponseDTO;
import cl.plataforma_gimnasio.ms_inscripcion.dto.PlanResponseDTO;
import cl.plataforma_gimnasio.ms_inscripcion.dto.SocioResponseDTO;
import cl.plataforma_gimnasio.ms_inscripcion.exception.ResourceNotFoundException;
import cl.plataforma_gimnasio.ms_inscripcion.model.Inscripcion;
import cl.plataforma_gimnasio.ms_inscripcion.repository.InscripcionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias para InscripcionService")
@SuppressWarnings("rawtypes")
class InscripcionServiceTest {

    @Mock
    private InscripcionRepository inscripcionRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private InscripcionService inscripcionService;

    private Inscripcion inscripcionMock;
    private InscripcionRequestDTO inscripcionRequestDTO;
    private SocioResponseDTO socioResponseMock;
    private PlanResponseDTO planResponseMock;
    private PagoResponseDTO pagoResponseMock;

    private WebClient webClient;
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        inscripcionMock = new Inscripcion();
        inscripcionMock.setIdInscripcion(1);
        inscripcionMock.setSedeInscripcion("Sede Central");
        inscripcionMock.setEstadoInscripcion("Activa");
        inscripcionMock.setIdSocio(10);
        inscripcionMock.setIdPlan(5);
        inscripcionMock.setIdPago(20);

        inscripcionRequestDTO = new InscripcionRequestDTO("Sede Central", "Activa", 5, 10, 20);

        socioResponseMock = new SocioResponseDTO(10, 12345678, "9", "Juan", "Perez", "juan@gimnasio.cl", "987654321", null);
        planResponseMock = new PlanResponseDTO(5, "Plan Anual", 12, 250000, "Acceso Total");
        pagoResponseMock = new PagoResponseDTO(20, 250000, "WebPay", "APROBADO", LocalDateTime.now());

        webClient = mock(WebClient.class);
        requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);
    }

    private void mockWebClientChain() {
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    }

    @Test
    @DisplayName("Debe retornar todas las inscripciones registradas")
    void obtenerTodos_DebeRetornarListaDeInscripciones() {
        when(inscripcionRepository.findAll()).thenReturn(List.of(inscripcionMock));

        List<InscripcionResponseDTO> resultado = inscripcionService.obtenerTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Sede Central", resultado.get(0).getSedeInscripcion());
        verify(inscripcionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay inscripciones")
    void obtenerTodos_DebeRetornarListaVacia_CuandoNoHayRegistros() {
        when(inscripcionRepository.findAll()).thenReturn(Collections.emptyList());

        List<InscripcionResponseDTO> resultado = inscripcionService.obtenerTodos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(inscripcionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe retornar una inscripcion por ID cuando existe")
    void obtenerPorId_DebeRetornarInscripcion_CuandoIdExiste() {
        when(inscripcionRepository.findById(1)).thenReturn(Optional.of(inscripcionMock));

        InscripcionResponseDTO resultado = inscripcionService.obtenerPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getIdInscripcion());
        assertEquals("Activa", resultado.getEstadoInscripcion());
        verify(inscripcionRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException al buscar un ID inexistente")
    void obtenerPorId_DebeLanzarExcepcion_CuandoIdNoExiste() {
        when(inscripcionRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inscripcionService.obtenerPorId(99));
        verify(inscripcionRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debe registrar una inscripcion de forma exitosa validando remotamente socio, plan y pago")
    void guardar_DebeRegistrarInscripcionExitosamente() {
        mockWebClientChain();
        when(responseSpec.bodyToMono(SocioResponseDTO.class)).thenReturn(Mono.just(socioResponseMock));
        when(responseSpec.bodyToMono(PlanResponseDTO.class)).thenReturn(Mono.just(planResponseMock));
        when(responseSpec.bodyToMono(PagoResponseDTO.class)).thenReturn(Mono.just(pagoResponseMock));
        when(inscripcionRepository.save(any(Inscripcion.class))).thenReturn(inscripcionMock);

        InscripcionResponseDTO resultado = inscripcionService.guardar(inscripcionRequestDTO);

        assertNotNull(resultado);
        assertEquals(1, resultado.getIdInscripcion());
        assertEquals(10, resultado.getIdSocio());
        assertEquals(5, resultado.getIdPlan());
        assertEquals(20, resultado.getIdPago());
        verify(inscripcionRepository, times(1)).save(any(Inscripcion.class));
    }

    @Test
    @DisplayName("Debe actualizar una inscripcion existente validando sus referencias externas")
    void actualizar_DebeModificarInscripcion_CuandoIdExiste() {
        mockWebClientChain();
        when(inscripcionRepository.findById(1)).thenReturn(Optional.of(inscripcionMock));
        when(responseSpec.bodyToMono(SocioResponseDTO.class)).thenReturn(Mono.just(socioResponseMock));
        when(responseSpec.bodyToMono(PlanResponseDTO.class)).thenReturn(Mono.just(planResponseMock));
        when(responseSpec.bodyToMono(PagoResponseDTO.class)).thenReturn(Mono.just(pagoResponseMock));
        when(inscripcionRepository.save(any(Inscripcion.class))).thenReturn(inscripcionMock);

        InscripcionResponseDTO resultado = inscripcionService.actualizar(1, inscripcionRequestDTO);

        assertNotNull(resultado);
        assertEquals(1, resultado.getIdInscripcion());
        verify(inscripcionRepository, times(1)).findById(1);
        verify(inscripcionRepository, times(1)).save(any(Inscripcion.class));
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException al actualizar una inscripcion que no existe")
    void actualizar_DebeLanzarExcepcion_CuandoInscripcionNoExiste() {
        when(inscripcionRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inscripcionService.actualizar(99, inscripcionRequestDTO));
        verify(inscripcionRepository, times(1)).findById(99);
        verify(inscripcionRepository, never()).save(any(Inscripcion.class));
    }

    @Test
    @DisplayName("Debe eliminar una inscripcion cuando el ID existe")
    void eliminar_DebeBorrarInscripcion_CuandoIdExiste() {
        when(inscripcionRepository.existsById(1)).thenReturn(true);
        doNothing().when(inscripcionRepository).deleteById(1);

        assertDoesNotThrow(() -> inscripcionService.eliminar(1));
        verify(inscripcionRepository, times(1)).existsById(1);
        verify(inscripcionRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException al intentar eliminar un ID que no existe")
    void eliminar_DebeLanzarExcepcion_CuandoIdNoExiste() {
        when(inscripcionRepository.existsById(99)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> inscripcionService.eliminar(99));
        verify(inscripcionRepository, times(1)).existsById(99);
        verify(inscripcionRepository, never()).deleteById(anyInt());
    }
}