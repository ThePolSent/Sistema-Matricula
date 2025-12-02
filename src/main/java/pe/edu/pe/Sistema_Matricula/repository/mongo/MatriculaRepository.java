package pe.edu.pe.Sistema_Matricula.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.pe.Sistema_Matricula.model.mongo.Matricula;
import pe.edu.pe.Sistema_Matricula.model.mongo.Auditoria;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// ==================== MATRICULA REPOSITORY ====================
@Repository
public interface MatriculaRepository extends MongoRepository<Matricula, String> {
    Optional<Matricula> findByCodigoMatricula(String codigoMatricula);
    List<Matricula> findByEstudianteId(Long estudianteId);
    List<Matricula> findByCodigoEstudiante(String codigoEstudiante);
    List<Matricula> findByPeriodo(String periodo);
    List<Matricula> findByEstado(Matricula.EstadoMatricula estado);
    List<Matricula> findByCarrera(String carrera);
    List<Matricula> findByEstudianteIdAndPeriodo(Long estudianteId, String periodo);
    
    @Query("{ 'estudianteId': ?0, 'estado': { $in: ['APROBADA', 'EN_CURSO'] } }")
    List<Matricula> findMatriculasActivasPorEstudiante(Long estudianteId);
    
    @Query("{ 'periodo': ?0, 'estado': 'APROBADA' }")
    List<Matricula> findMatriculasAprobadasPorPeriodo(String periodo);
    
    @Query("{ 'fechaMatricula': { $gte: ?0, $lte: ?1 } }")
    List<Matricula> findMatriculasPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);
    
    @Query("{ 'cursos.cursoId': ?0 }")
    List<Matricula> findMatriculasPorCurso(Long cursoId);
}