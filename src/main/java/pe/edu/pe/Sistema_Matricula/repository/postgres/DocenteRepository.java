package pe.edu.pe.Sistema_Matricula.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.pe.Sistema_Matricula.model.postgres.Curso;
import pe.edu.pe.Sistema_Matricula.model.postgres.Docente;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, Long> {
    Optional<Docente> findByCodigo(String codigo);
    Optional<Docente> findByDni(String dni);
    Optional<Docente> findByUsuarioId(Long usuarioId);
    List<Docente> findByEspecialidad(String especialidad);
    List<Docente> findByActivoTrue();
    List<Docente> findByTipoContrato(Docente.TipoContrato tipoContrato);
    boolean existsByCodigo(String codigo);
    boolean existsByDni(String dni);
    
    @Query("SELECT d FROM Docente d WHERE d.activo = true AND d.especialidad = :especialidad")
    List<Docente> findDocentesActivosPorEspecialidad(String especialidad);
    
    @Query("SELECT d FROM Docente d WHERE d.gradoAcademico IN :grados")
    List<Docente> findDocentesPorGrados(List<String> grados);
}