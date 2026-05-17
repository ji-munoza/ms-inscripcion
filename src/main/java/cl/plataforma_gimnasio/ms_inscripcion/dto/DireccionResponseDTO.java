package cl.plataforma_gimnasio.ms_inscripcion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DireccionResponseDTO {
    private Integer idDireccion;
    private String calleDireccion;
    private Integer numeroDireccion;
    private String departamentoDireccion;
    private String comunaDireccion;
    private String regionDireccion;
}
