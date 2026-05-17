package cl.plataforma_gimnasio.ms_inscripcion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionResponseDTO {
    private Integer idInscripcion;
    private String sedeInscripcion;
    private String estadoInscripcion;
    private Integer idPlan;
    private Integer idSocio;
    private Integer idPago;
}