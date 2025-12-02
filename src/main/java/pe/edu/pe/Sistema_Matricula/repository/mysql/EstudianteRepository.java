package pe.edu.pe.Sistema_Matricula.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.pe.Sistema_Matricula.model.mysql.Usuario;
import pe.edu.pe.Sistema_Matricula.model.mysql.Estudiante;
import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    Optional<Estudiante> findByCodigo(String codigo);
    Optional<Estudiante> findByDni(String dni);
    Optional<Estudiante> findByUsuarioId(Long usuarioId);
    List<Estudiante> findByCarrera(String carrera);
    List<Estudiante> findByCiclo(Integer ciclo);
    List<Estudiante> findByEstado(Estudiante.Estado estado);
    List<Estudiante> findByCarreraAndCiclo(String carrera, Integer ciclo);
    boolean existsByCodigo(String codigo);
    boolean existsByDni(String dni);
    
    @Query("SELECT e FROM Estudiante e WHERE e.estado = 'ACTIVO' AND e.carrera = :carrera")
    List<Estudiante> findEstudiantesActivosPorCarrera(String carrera);
    
    @Query("SELECT e FROM Estudiante e WHERE e.promedioPonderado >= :promedio")
    List<Estudiante> findEstudiantesPorPromedioMinimo(Double promedio);
}