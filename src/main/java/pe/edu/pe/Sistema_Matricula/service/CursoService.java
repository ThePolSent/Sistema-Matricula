package pe.edu.pe.Sistema_Matricula.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.pe.Sistema_Matricula.model.postgres.Curso;
import pe.edu.pe.Sistema_Matricula.repository.postgres.CursoRepository;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cursoRepository;
    private final AuditoriaService auditoriaService;

    @Transactional("postgresTransactionManager")
    public Curso registrarCurso(Curso curso, String username) {
        if (cursoRepository.existsByCodigo(curso.getCodigo())) {
            throw new RuntimeException("El código de curso ya existe");
        }

        curso.setVacantesDisponibles(curso.getVacantesTotales());
        Curso guardado = cursoRepository.save(curso);
        
        auditoriaService.registrarOperacion(
            "Curso", "CREATE", guardado.getId(), username, 
            "ADMIN", "Curso registrado: " + guardado.getCodigo()
        );

        return guardado;
    }

    public Optional<Curso> buscarPorId(Long id) {
        return cursoRepository.findById(id);
    }

    public Optional<Curso> buscarPorCodigo(String codigo) {
        return cursoRepository.findByCodigo(codigo);
    }

    public List<Curso> listarTodos() {
        return cursoRepository.findAll();
    }

    public List<Curso> listarActivos() {
        return cursoRepository.findByActivoTrue();
    }

    public List<Curso> listarPorCarrera(String carrera) {
        return cursoRepository.findByCarrera(carrera);
    }

    public List<Curso> listarPorCiclo(Integer ciclo) {
        return cursoRepository.findByCiclo(ciclo);
    }

    public List<Curso> listarPorCarreraYCiclo(String carrera, Integer ciclo) {
        return cursoRepository.findByCarreraAndCiclo(carrera, ciclo);
    }

    public List<Curso> listarConVacantes() {
        return cursoRepository.findCursosConVacantes();
    }

    public List<Curso> listarActivosPorCarreraYCiclo(String carrera, Integer ciclo) {
        return cursoRepository.findCursosActivosPorCarreraYCiclo(carrera, ciclo);
    }

    public List<Curso> listarPorRangoCreditos(Integer min, Integer max) {
        return cursoRepository.findCursosPorRangoCreditos(min, max);
    }

    @Transactional("postgresTransactionManager")
    public Curso actualizarCurso(Long id, Curso cursoActualizado, String username) {
        Curso curso = cursoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        curso.setNombre(cursoActualizado.getNombre());
        curso.setDescripcion(cursoActualizado.getDescripcion());
        curso.setCreditos(cursoActualizado.getCreditos());
        curso.setHorasTeoricas(cursoActualizado.getHorasTeoricas());
        curso.setHorasPracticas(cursoActualizado.getHorasPracticas());
        curso.setPrerequisito(cursoActualizado.getPrerequisito());

        Curso guardado = cursoRepository.save(curso);
        
        auditoriaService.registrarOperacion(
            "Curso", "UPDATE", guardado.getId(), username, 
            "ADMIN", "Curso actualizado: " + guardado.getCodigo()
        );

        return guardado;
    }

    @Transactional("postgresTransactionManager")
    public void actualizarVacantes(Long id, Integer vacantes, String username) {
        Curso curso = cursoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        
        curso.setVacantesTotales(vacantes);
        cursoRepository.save(curso);
        
        auditoriaService.registrarOperacion(
            "Curso", "UPDATE", curso.getId(), username, 
            "ADMIN", "Vacantes actualizadas a: " + vacantes
        );
    }

    @Transactional("postgresTransactionManager")
    public boolean reservarVacante(Long id, String username) {
        Curso curso = cursoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        
        if (curso.getVacantesDisponibles() <= 0) {
            auditoriaService.registrarOperacionFallida(
                "Curso", "UPDATE", curso.getId(), username, 
                "ESTUDIANTE", "No hay vacantes disponibles"
            );
            return false;
        }
        
        curso.setVacantesDisponibles(curso.getVacantesDisponibles() - 1);
        cursoRepository.save(curso);
        
        auditoriaService.registrarOperacion(
            "Curso", "UPDATE", curso.getId(), username, 
            "ESTUDIANTE", "Vacante reservada. Disponibles: " + curso.getVacantesDisponibles()
        );
        
        return true;
    }

    @Transactional("postgresTransactionManager")
    public void liberarVacante(Long id, String username) {
        Curso curso = cursoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        
        if (curso.getVacantesDisponibles() < curso.getVacantesTotales()) {
            curso.setVacantesDisponibles(curso.getVacantesDisponibles() + 1);
            cursoRepository.save(curso);
            
            auditoriaService.registrarOperacion(
                "Curso", "UPDATE", curso.getId(), username, 
                "ESTUDIANTE", "Vacante liberada. Disponibles: " + curso.getVacantesDisponibles()
            );
        }
    }

    @Transactional("postgresTransactionManager")
    public void cambiarEstado(Long id, Boolean activo, String username) {
        Curso curso = cursoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        
        curso.setActivo(activo);
        cursoRepository.save(curso);
        
        auditoriaService.registrarOperacion(
            "Curso", "UPDATE", curso.getId(), username, 
            "ADMIN", "Estado cambiado a: " + activo
        );
    }
}