package pe.edu.pe.Sistema_Matricula.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.pe.Sistema_Matricula.model.postgres.Curso;
import pe.edu.pe.Sistema_Matricula.model.postgres.Docente;
import java.util.List;
import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    Optional<Curso> findByCodigo(String codigo);
    List<Curso> findByCarrera(String carrera);
    List<Curso> findByCiclo(Integer ciclo);
    List<Curso> findByCarreraAndCiclo(String carrera, Integer ciclo);
    List<Curso> findByTipo(Curso.TipoCurso tipo);
    List<Curso> findByActivoTrue();
    boolean existsByCodigo(String codigo);
    
    @Query("SELECT c FROM Curso c WHERE c.activo = true AND c.vacantesDisponibles > 0")
    List<Curso> findCursosConVacantes();
    
    @Query("SELECT c FROM Curso c WHERE c.carrera = :carrera AND c.ciclo = :ciclo AND c.activo = true")
    List<Curso> findCursosActivosPorCarreraYCiclo(String carrera, Integer ciclo);
    
    @Query("SELECT c FROM Curso c WHERE c.creditos >= :minCreditos AND c.creditos <= :maxCreditos")
    List<Curso> findCursosPorRangoCreditos(Integer minCreditos, Integer maxCreditos);
}

