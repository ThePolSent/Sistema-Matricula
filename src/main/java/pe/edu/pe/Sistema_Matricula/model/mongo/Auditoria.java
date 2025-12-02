package pe.edu.pe.Sistema_Matricula.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "auditoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Auditoria {

    @Id
    private String id;

    private String entidad; // Usuario, Estudiante, Curso, Docente, Matricula
    private String operacion; // CREATE, UPDATE, DELETE, LOGIN, LOGOUT
    
    private Long usuarioId;
    private String username;
    private String rol;
    
    private String entidadAfectadaId;
    private String entidadAfectadaTipo;
    
    private Map<String, Object> datosAnteriores;
    private Map<String, Object> datosNuevos;
    
    private LocalDateTime fecha = LocalDateTime.now();
    
    private String ipAddress;
    private String userAgent;
    
    private String descripcion;
    private Boolean exitoso = true;
    private String mensajeError;
}