package cl.plataforma_gimnasio.ms_inscripcion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocioResponseDTO {
    private Integer idSocio;
    private Integer rutSocio;
    private String dvSocio;
    private String nombreSocio;
    private String apellidoSocio;
    private String correoSocio;
    private String telefonoSocio;
    private DireccionResponseDTO direccion;
}
