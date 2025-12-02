package pe.edu.pe.Sistema_Matricula.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.pe.Sistema_Matricula.model.mysql.Estudiante;
import pe.edu.pe.Sistema_Matricula.model.mysql.Usuario;
import pe.edu.pe.Sistema_Matricula.repository.mysql.EstudianteRepository;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final UsuarioService usuarioService;
    private final AuditoriaService auditoriaService;

    @Transactional("mysqlTransactionManager")
    public Estudiante registrarEstudiante(Estudiante estudiante, String username) {
        // Validar si ya existe
        if (estudianteRepository.existsByCodigo(estudiante.getCodigo())) {
            throw new RuntimeException("El código de estudiante ya existe");
        }
        if (estudianteRepository.existsByDni(estudiante.getDni())) {
            throw new RuntimeException("El DNI ya está registrado");
        }

        // Validar usuario
        Usuario usuario = estudiante.getUsuario();
        if (usuario == null || usuario.getId() == null) {
            throw new RuntimeException("Debe asociar un usuario válido");
        }

        Estudiante guardado = estudianteRepository.save(estudiante);
        
        auditoriaService.registrarOperacion(
            "Estudiante", "CREATE", guardado.getId(), username, 
            "ADMIN", "Estudiante registrado: " + guardado.getCodigo()
        );

        return guardado;
    }

    public Optional<Estudiante> buscarPorId(Long id) {
        return estudianteRepository.findById(id);
    }

    public Optional<Estudiante> buscarPorCodigo(String codigo) {
        return estudianteRepository.findByCodigo(codigo);
    }

    public Optional<Estudiante> buscarPorDni(String dni) {
        return estudianteRepository.findByDni(dni);
    }

    public Optional<Estudiante> buscarPorUsuarioId(Long usuarioId) {
        return estudianteRepository.findByUsuarioId(usuarioId);
    }

    public List<Estudiante> listarTodos() {
        return estudianteRepository.findAll();
    }

    public List<Estudiante> listarPorCarrera(String carrera) {
        return estudianteRepository.findByCarrera(carrera);
    }

    public List<Estudiante> listarPorCiclo(Integer ciclo) {
        return estudianteRepository.findByCiclo(ciclo);
    }

    public List<Estudiante> listarPorCarreraYCiclo(String carrera, Integer ciclo) {
        return estudianteRepository.findByCarreraAndCiclo(carrera, ciclo);
    }

    public List<Estudiante> listarPorEstado(Estudiante.Estado estado) {
        return estudianteRepository.findByEstado(estado);
    }

    public List<Estudiante> listarActivosPorCarrera(String carrera) {
        return estudianteRepository.findEstudiantesActivosPorCarrera(carrera);
    }

    public List<Estudiante> listarPorPromedioMinimo(Double promedio) {
        return estudianteRepository.findEstudiantesPorPromedioMinimo(promedio);
    }

    @Transactional("mysqlTransactionManager")
    public Estudiante actualizarEstudiante(Long id, Estudiante estudianteActualizado, String username) {
        Estudiante estudiante = estudianteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        estudiante.setDireccion(estudianteActualizado.getDireccion());
        estudiante.setTelefono(estudianteActualizado.getTelefono());
        estudiante.setCarrera(estudianteActualizado.getCarrera());
        estudiante.setCiclo(estudianteActualizado.getCiclo());

        Estudiante guardado = estudianteRepository.save(estudiante);
        
        auditoriaService.registrarOperacion(
            "Estudiante", "UPDATE", guardado.getId(), username, 
            "ADMIN", "Estudiante actualizado: " + guardado.getCodigo()
        );

        return guardado;
    }

    @Transactional("mysqlTransactionManager")
    public void actualizarPromedio(Long id, Double promedio, String username) {
        Estudiante estudiante = estudianteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        
        estudiante.setPromedioPonderado(promedio);
        estudianteRepository.save(estudiante);
        
        auditoriaService.registrarOperacion(
            "Estudiante", "UPDATE", estudiante.getId(), username, 
            "ADMIN", "Promedio actualizado a: " + promedio
        );
    }

    @Transactional("mysqlTransactionManager")
    public void actualizarCreditos(Long id, Integer creditos, String username) {
        Estudiante estudiante = estudianteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        
        estudiante.setCreditosAprobados(creditos);
        estudianteRepository.save(estudiante);
        
        auditoriaService.registrarOperacion(
            "Estudiante", "UPDATE", estudiante.getId(), username, 
            "ADMIN", "Créditos actualizados a: " + creditos
        );
    }

    @Transactional("mysqlTransactionManager")
    public void cambiarEstado(Long id, Estudiante.Estado estado, String username) {
        Estudiante estudiante = estudianteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        
        estudiante.setEstado(estado);
        estudianteRepository.save(estudiante);
        
        auditoriaService.registrarOperacion(
            "Estudiante", "UPDATE", estudiante.getId(), username, 
            "ADMIN", "Estado cambiado a: " + estado
        );
    }
}