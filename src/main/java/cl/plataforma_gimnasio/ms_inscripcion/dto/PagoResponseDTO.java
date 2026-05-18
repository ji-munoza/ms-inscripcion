package cl.plataforma_gimnasio.ms_inscripcion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponseDTO {
    private Integer idPago;
    private Integer montoPago;
    private String metodoPago;
    private String estadoPago;
    private LocalDateTime fechaHoraPago;
}