package pe.edu.pe.Sistema_Matricula.model.postgres;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cursos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código es obligatorio")
    @Column(unique = true, nullable = false, length = 10)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Min(value = 1, message = "Los créditos deben ser al menos 1")
    @Column(nullable = false)
    private Integer creditos;

    @Min(value = 1, message = "El ciclo debe ser al menos 1")
    @Column(nullable = false)
    private Integer ciclo;

    @NotBlank
    @Column(nullable = false)
    private String carrera;

    @Column(nullable = false)
    private Integer horasTeoricas = 0;

    @Column(nullable = false)
    private Integer horasPracticas = 0;

    @Column(nullable = false)
    private Integer vacantesDisponibles = 30;

    @Column(nullable = false)
    private Integer vacantesTotales = 30;

    private String prerequisito;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCurso tipo = TipoCurso.OBLIGATORIO;

    @Column(nullable = false)
    private Boolean activo = true;

    public enum TipoCurso {
        OBLIGATORIO,
        ELECTIVO,
        SEMINARIO
    }
}