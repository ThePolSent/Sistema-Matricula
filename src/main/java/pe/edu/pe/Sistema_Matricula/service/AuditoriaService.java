package pe.edu.pe.Sistema_Matricula.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.pe.Sistema_Matricula.model.mongo.Auditoria;
import pe.edu.pe.Sistema_Matricula.repository.mongo.AuditoriaRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public void registrarOperacion(String entidad, String operacion, Long entidadId, 
                                   String username, String rol, String descripcion) {
        Auditoria auditoria = new Auditoria();
        auditoria.setEntidad(entidad);
        auditoria.setOperacion(operacion);
        auditoria.setUsername(username);
        auditoria.setRol(rol);
        auditoria.setEntidadAfectadaId(entidadId != null ? entidadId.toString() : null);
        auditoria.setEntidadAfectadaTipo(entidad);
        auditoria.setDescripcion(descripcion);
        auditoria.setFecha(LocalDateTime.now());
        auditoria.setExitoso(true);

        auditoriaRepository.save(auditoria);
    }

    public void registrarOperacionFallida(String entidad, String operacion, Long entidadId,
                                         String username, String rol, String mensajeError) {
        Auditoria auditoria = new Auditoria();
        auditoria.setEntidad(entidad);
        auditoria.setOperacion(operacion);
        auditoria.setUsername(username);
        auditoria.setRol(rol);
        auditoria.setEntidadAfectadaId(entidadId != null ? entidadId.toString() : null);
        auditoria.setEntidadAfectadaTipo(entidad);
        auditoria.setMensajeError(mensajeError);
        auditoria.setFecha(LocalDateTime.now());
        auditoria.setExitoso(false);

        auditoriaRepository.save(auditoria);
    }

    public List<Auditoria> obtenerAuditoriasPorUsuario(Long usuarioId) {
        return auditoriaRepository.findByUsuarioId(usuarioId);
    }

    public List<Auditoria> obtenerAuditoriasPorUsername(String username) {
        return auditoriaRepository.findByUsername(username);
    }

    public List<Auditoria> obtenerAuditoriasPorEntidad(String entidad) {
        return auditoriaRepository.findByEntidad(entidad);
    }

    public List<Auditoria> obtenerOperacionesFallidas() {
        return auditoriaRepository.findByExitosoFalse();
    }

    public List<Auditoria> obtenerAuditoriasPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return auditoriaRepository.findAuditoriasPorRangoFechas(inicio, fin);
    }

    public List<Auditoria> obtenerAuditoriasPorEntidadAfectada(String entidad, String entidadId) {
        return auditoriaRepository.findAuditoriasPorEntidadAfectada(entidad, entidadId);
    }
}