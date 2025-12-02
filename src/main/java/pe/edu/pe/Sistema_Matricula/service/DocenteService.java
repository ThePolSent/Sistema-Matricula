package pe.edu.pe.Sistema_Matricula.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.pe.Sistema_Matricula.model.postgres.Docente;
import pe.edu.pe.Sistema_Matricula.repository.postgres.DocenteRepository;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocenteService {

    private final DocenteRepository docenteRepository;
    private final AuditoriaService auditoriaService;

    @Transactional("postgresTransactionManager")
    public Docente registrarDocente(Docente docente, String username) {
        if (docenteRepository.existsByCodigo(docente.getCodigo())) {
            throw new RuntimeException("El código de docente ya existe");
        }
        if (docenteRepository.existsByDni(docente.getDni())) {
            throw new RuntimeException("El DNI ya está registrado");
        }

        Docente guardado = docenteRepository.save(docente);
        
        auditoriaService.registrarOperacion(
            "Docente", "CREATE", guardado.getId(), username, 
            "ADMIN", "Docente registrado: " + guardado.getCodigo()
        );

        return guardado;
    }

    public Optional<Docente> buscarPorId(Long id) {
        return docenteRepository.findById(id);
    }

    public Optional<Docente> buscarPorCodigo(String codigo) {
        return docenteRepository.findByCodigo(codigo);
    }

    public Optional<Docente> buscarPorDni(String dni) {
        return docenteRepository.findByDni(dni);
    }

    public Optional<Docente> buscarPorUsuarioId(Long usuarioId) {
        return docenteRepository.findByUsuarioId(usuarioId);
    }

    public List<Docente> listarTodos() {
        return docenteRepository.findAll();
    }

    public List<Docente> listarActivos() {
        return docenteRepository.findByActivoTrue();
    }

    public List<Docente> listarPorEspecialidad(String especialidad) {
        return docenteRepository.findByEspecialidad(especialidad);
    }

    public List<Docente> listarActivosPorEspecialidad(String especialidad) {
        return docenteRepository.findDocentesActivosPorEspecialidad(especialidad);
    }

    public List<Docente> listarPorTipoContrato(Docente.TipoContrato tipo) {
        return docenteRepository.findByTipoContrato(tipo);
    }

    public List<Docente> listarPorGrados(List<String> grados) {
        return docenteRepository.findDocentesPorGrados(grados);
    }

    @Transactional("postgresTransactionManager")
    public Docente actualizarDocente(Long id, Docente docenteActualizado, String username) {
        Docente docente = docenteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Docente no encontrado"));

        docente.setNombre(docenteActualizado.getNombre());
        docente.setApellido(docenteActualizado.getApellido());
        docente.setEmail(docenteActualizado.getEmail());
        docente.setTelefono(docenteActualizado.getTelefono());
        docente.setEspecialidad(docenteActualizado.getEspecialidad());
        docente.setGradoAcademico(docenteActualizado.getGradoAcademico());
        docente.setTipoContrato(docenteActualizado.getTipoContrato());
        docente.setCurriculum(docenteActualizado.getCurriculum());

        Docente guardado = docenteRepository.save(docente);
        
        auditoriaService.registrarOperacion(
            "Docente", "UPDATE", guardado.getId(), username, 
            "ADMIN", "Docente actualizado: " + guardado.getCodigo()
        );

        return guardado;
    }

    @Transactional("postgresTransactionManager")
    public void cambiarEstado(Long id, Boolean activo, String username) {
        Docente docente = docenteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
        
        docente.setActivo(activo);
        docenteRepository.save(docente);
        
        auditoriaService.registrarOperacion(
            "Docente", "UPDATE", docente.getId(), username, 
            "ADMIN", "Estado cambiado a: " + activo
        );
    }

    @Transactional("postgresTransactionManager")
    public void actualizarTipoContrato(Long id, Docente.TipoContrato tipo, String username) {
        Docente docente = docenteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
        
        docente.setTipoContrato(tipo);
        docenteRepository.save(docente);
        
        auditoriaService.registrarOperacion(
            "Docente", "UPDATE", docente.getId(), username, 
            "ADMIN", "Tipo de contrato cambiado a: " + tipo
        );
    }
}