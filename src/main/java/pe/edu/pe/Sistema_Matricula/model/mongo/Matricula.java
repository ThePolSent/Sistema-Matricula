package pe.edu.pe.Sistema_Matricula.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "matriculas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Matricula {

    @Id
    private String id;

    private String codigoMatricula; // Código único generado
    
    private Long estudianteId; // ID del estudiante en MySQL
    private String codigoEstudiante;
    private String nombreEstudiante;
    
    private String periodo; // Ej: "2025-1"
    private Integer ciclo;
    private String carrera;
    
    private List<CursoMatriculado> cursos = new ArrayList<>();
    
    private Integer totalCreditos = 0;
    private Double costoTotal = 0.0;
    
    private EstadoMatricula estado = EstadoMatricula.PENDIENTE;
    
    private LocalDateTime fechaMatricula = LocalDateTime.now();
    private LocalDateTime fechaAprobacion;
    private LocalDateTime fechaCancelacion;
    
    private String observaciones;
    private String aprobadoPor; // Usuario que aprobó
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CursoMatriculado {
        private Long cursoId; // ID del curso en PostgreSQL
        private String codigoCurso;
        private String nombreCurso;
        private Integer creditos;
        private String nombreDocente;
        private String horario;
        private String aula;
    }
    
    public enum EstadoMatricula {
        PENDIENTE,
        APROBADA,
        RECHAZADA,
        CANCELADA,
        EN_CURSO,
        FINALIZADA
    }
}