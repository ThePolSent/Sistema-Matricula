package pe.edu.pe.Sistema_Matricula.model.mysql;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "estudiantes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código es obligatorio")
    @Column(unique = true, nullable = false, length = 10)
    private String codigo;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "DNI debe tener 8 dígitos")
    @Column(unique = true, nullable = false, length = 8)
    private String dni;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @NotBlank
    @Column(nullable = false)
    private String direccion;

    @NotBlank
    @Pattern(regexp = "\\d{9}", message = "Teléfono debe tener 9 dígitos")
    @Column(length = 9)
    private String telefono;

    @NotBlank
    @Column(nullable = false)
    private String carrera;

    @Column(nullable = false)
    private Integer ciclo = 1;

    @Column(nullable = false)
    private Double promedioPonderado = 0.0;

    @Column(nullable = false)
    private Integer creditosAprobados = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.ACTIVO;

    public enum Estado {
        ACTIVO,
        INACTIVO,
        EGRESADO,
        RETIRADO
    }
}