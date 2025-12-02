package pe.edu.pe.Sistema_Matricula.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.pe.Sistema_Matricula.model.mongo.Matricula;
import pe.edu.pe.Sistema_Matricula.model.mysql.Estudiante;
import pe.edu.pe.Sistema_Matricula.model.postgres.Curso;
import pe.edu.pe.Sistema_Matricula.repository.mongo.MatriculaRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final EstudianteService estudianteService;
    private final CursoService cursoService;
    private final AuditoriaService auditoriaService;

    @Transactional
    public Matricula generarMatricula(Long estudianteId, List<Long> cursosIds, String periodo, String username) {
        // Validar estudiante
        Estudiante estudiante = estudianteService.buscarPorId(estudianteId)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        if (!estudiante.getEstado().equals(Estudiante.Estado.ACTIVO)) {
            throw new RuntimeException("El estudiante no está activo");
        }

        // Verificar si ya tiene matrícula en el periodo
        List<Matricula> matriculasExistentes = matriculaRepository
            .findByEstudianteIdAndPeriodo(estudianteId, periodo);
        
        if (!matriculasExistentes.isEmpty()) {
            throw new RuntimeException("El estudiante ya tiene una matrícula en este período");
        }

        // Crear matrícula
        Matricula matricula = new Matricula();
        matricula.setCodigoMatricula(generarCodigoMatricula());
        matricula.setEstudianteId(estudianteId);
        matricula.setCodigoEstudiante(estudiante.getCodigo());
        matricula.setNombreEstudiante(estudiante.getUsuario().getNombre() + " " + 
                                       estudiante.getUsuario().getApellido());
        matricula.setPeriodo(periodo);
        matricula.setCiclo(estudiante.getCiclo());
        matricula.setCarrera(estudiante.getCarrera());
        matricula.setFechaMatricula(LocalDateTime.now());
        matricula.setEstado(Matricula.EstadoMatricula.PENDIENTE);

        List<Matricula.CursoMatriculado> cursosMatriculados = new ArrayList<>();
        int totalCreditos = 0;

        // Procesar cada curso
        for (Long cursoId : cursosIds) {
            Curso curso = cursoService.buscarPorId(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado: " + cursoId));

            // Validar vacantes
            if (!cursoService.reservarVacante(cursoId, username)) {
                throw new RuntimeException("No hay vacantes para el curso: " + curso.getNombre());
            }

            // Agregar curso a la matrícula
            Matricula.CursoMatriculado cursoMatriculado = new Matricula.CursoMatriculado();
            cursoMatriculado.setCursoId(cursoId);
            cursoMatriculado.setCodigoCurso(curso.getCodigo());
            cursoMatriculado.setNombreCurso(curso.getNombre());
            cursoMatriculado.setCreditos(curso.getCreditos());
            cursoMatriculado.setHorario("Por asignar");
            cursoMatriculado.setAula("Por asignar");
            cursoMatriculado.setNombreDocente("Por asignar");

            cursosMatriculados.add(cursoMatriculado);
            totalCreditos += curso.getCreditos();
        }

        matricula.setCursos(cursosMatriculados);
        matricula.setTotalCreditos(totalCreditos);
        matricula.setCostoTotal(totalCreditos * 150.0); // S/. 150 por crédito

        Matricula guardada = matriculaRepository.save(matricula);

        auditoriaService.registrarOperacion(
            "Matricula", "CREATE", null, username, 
            "ESTUDIANTE", "Matrícula generada: " + guardada.getCodigoMatricula() + 
            " - Cursos: " + cursosIds.size()
        );

        return guardada;
    }

    public Optional<Matricula> buscarPorId(String id) {
        return matriculaRepository.findById(id);
    }

    public Optional<Matricula> buscarPorCodigo(String codigo) {
        return matriculaRepository.findByCodigoMatricula(codigo);
    }

    public List<Matricula> listarPorEstudiante(Long estudianteId) {
        return matriculaRepository.findByEstudianteId(estudianteId);
    }

    public List<Matricula> listarPorPeriodo(String periodo) {
        return matriculaRepository.findByPeriodo(periodo);
    }

    public List<Matricula> listarPorEstado(Matricula.EstadoMatricula estado) {
        return matriculaRepository.findByEstado(estado);
    }

    public List<Matricula> listarActivasPorEstudiante(Long estudianteId) {
        return matriculaRepository.findMatriculasActivasPorEstudiante(estudianteId);
    }

    public List<Matricula> listarAprobadasPorPeriodo(String periodo) {
        return matriculaRepository.findMatriculasAprobadasPorPeriodo(periodo);
    }

    public List<Matricula> listarPorCurso(Long cursoId) {
        return matriculaRepository.findMatriculasPorCurso(cursoId);
    }

    public List<Matricula> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return matriculaRepository.findMatriculasPorRangoFechas(inicio, fin);
    }

    @Transactional
    public Matricula aprobarMatricula(String matriculaId, String aprobadoPor) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
            .orElseThrow(() -> new RuntimeException("Matrícula no encontrada"));

        if (!matricula.getEstado().equals(Matricula.EstadoMatricula.PENDIENTE)) {
            throw new RuntimeException("Solo se pueden aprobar matrículas pendientes");
        }

        matricula.setEstado(Matricula.EstadoMatricula.APROBADA);
        matricula.setFechaAprobacion(LocalDateTime.now());
        matricula.setAprobadoPor(aprobadoPor);

        Matricula guardada = matriculaRepository.save(matricula);

        auditoriaService.registrarOperacion(
            "Matricula", "UPDATE", null, aprobadoPor, 
            "ADMIN", "Matrícula aprobada: " + guardada.getCodigoMatricula()
        );

        return guardada;
    }

    @Transactional
    public Matricula rechazarMatricula(String matriculaId, String observaciones, String rechazadoPor) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
            .orElseThrow(() -> new RuntimeException("Matrícula no encontrada"));

        if (!matricula.getEstado().equals(Matricula.EstadoMatricula.PENDIENTE)) {
            throw new RuntimeException("Solo se pueden rechazar matrículas pendientes");
        }

        // Liberar vacantes
        for (Matricula.CursoMatriculado curso : matricula.getCursos()) {
            cursoService.liberarVacante(curso.getCursoId(), rechazadoPor);
        }

        matricula.setEstado(Matricula.EstadoMatricula.RECHAZADA);
        matricula.setObservaciones(observaciones);

        Matricula guardada = matriculaRepository.save(matricula);

        auditoriaService.registrarOperacion(
            "Matricula", "UPDATE", null, rechazadoPor, 
            "ADMIN", "Matrícula rechazada: " + guardada.getCodigoMatricula()
        );

        return guardada;
    }

    @Transactional
    public Matricula cancelarMatricula(String matriculaId, String canceladoPor) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
            .orElseThrow(() -> new RuntimeException("Matrícula no encontrada"));

        // Liberar vacantes
        for (Matricula.CursoMatriculado curso : matricula.getCursos()) {
            cursoService.liberarVacante(curso.getCursoId(), canceladoPor);
        }

        matricula.setEstado(Matricula.EstadoMatricula.CANCELADA);
        matricula.setFechaCancelacion(LocalDateTime.now());

        Matricula guardada = matriculaRepository.save(matricula);

        auditoriaService.registrarOperacion(
            "Matricula", "UPDATE", null, canceladoPor, 
            "ESTUDIANTE", "Matrícula cancelada: " + guardada.getCodigoMatricula()
        );

        return guardada;
    }

    private String generarCodigoMatricula() {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "MAT-" + fecha + "-" + random;
    }
}