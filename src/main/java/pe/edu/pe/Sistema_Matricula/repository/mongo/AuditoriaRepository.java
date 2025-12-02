package pe.edu.pe.Sistema_Matricula.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.pe.Sistema_Matricula.model.mongo.Matricula;
import pe.edu.pe.Sistema_Matricula.model.mongo.Auditoria;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuditoriaRepository extends MongoRepository<Auditoria, String> {
    List<Auditoria> findByUsuarioId(Long usuarioId);
    List<Auditoria> findByUsername(String username);
    List<Auditoria> findByEntidad(String entidad);
    List<Auditoria> findByOperacion(String operacion);
    List<Auditoria> findByExitosoFalse();
    
    @Query("{ 'fecha': { $gte: ?0, $lte: ?1 } }")
    List<Auditoria> findAuditoriasPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);
    
    @Query("{ 'usuarioId': ?0, 'operacion': ?1 }")
    List<Auditoria> findAuditoriasPorUsuarioYOperacion(Long usuarioId, String operacion);
    
    @Query("{ 'entidad': ?0, 'entidadAfectadaId': ?1 }")
    List<Auditoria> findAuditoriasPorEntidadAfectada(String entidad, String entidadId);
}