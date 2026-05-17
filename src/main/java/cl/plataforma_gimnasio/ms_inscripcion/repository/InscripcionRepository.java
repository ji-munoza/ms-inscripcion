package cl.plataforma_gimnasio.ms_inscripcion.repository;

import cl.plataforma_gimnasio.ms_inscripcion.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {
}