package cl.plataforma_gimnasio.ms_inscripcion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inscripciones")
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inscripcion")
    private Integer idInscripcion;

    @Column(name = "sede_inscripcion", nullable = false, length = 50)
    private String sedeInscripcion;

    @Column(name = "estado_inscripcion", nullable = false, length = 50)
    private String estadoInscripcion;

    @Column(name = "plan_id_plan", nullable = false)
    private Integer idPlan;

    @Column(name = "socio_id_socio", nullable = false)
    private Integer idSocio;

    @Column(name = "pago_id_pago", nullable = false)
    private Integer idPago;
}