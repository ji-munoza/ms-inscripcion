package cl.plataforma_gimnasio.ms_inscripcion.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionRequestDTO {

    @NotBlank(message = "La sede de la inscripcion es obligatoria.")
    @Size(min = 3, max = 50, message = "La sede debe tener entre 3 y 50 caracteres.")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ0-9.\\s]+$", message = "La sede solo puede contener letras, numeros, espacios y puntos.")
    private String sedeInscripcion;

    @NotBlank(message = "El estado de la inscripcion es obligatorio.")
    @Size(min = 3, max = 50, message = "El estado debe tener entre 3 y 50 caracteres.")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El estado solo puede contener letras y espacios.")
    private String estadoInscripcion;

    @NotNull(message = "El ID del plan es obligatorio.")
    @Min(value = 1, message = "El ID del plan debe ser igual o mayor a 1.")
    private Integer idPlan;

    @NotNull(message = "El ID del socio es obligatorio.")
    @Min(value = 1, message = "El ID del socio debe ser igual o mayor a 1.")
    private Integer idSocio;

    @NotNull(message = "El ID del pago es obligatorio.")
    @Min(value = 1, message = "El ID del pago debe ser igual o mayor a 1.")
    private Integer idPago;
}