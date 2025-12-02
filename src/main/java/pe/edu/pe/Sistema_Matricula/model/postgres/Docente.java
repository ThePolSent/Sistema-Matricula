package pe.edu.pe.Sistema_Matricula.model.postgres;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "docentes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Docente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código es obligatorio")
    @Column(unique = true, nullable = false, length = 10)
    private String codigo;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId; // Referencia al ID del usuario en MySQL

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "DNI debe tener 8 dígitos")
    @Column(unique = true, nullable = false, length = 8)
    private String dni;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    @NotBlank
    @Column(nullable = false)
    private String apellido;

    @NotBlank
    @Column(nullable = false)
    private String email;

    @NotBlank
    @Pattern(regexp = "\\d{9}", message = "Teléfono debe tener 9 dígitos")
    @Column(length = 9)
    private String telefono;

    @NotBlank
    @Column(nullable = false)
    private String especialidad;

    @NotBlank
    @Column(nullable = false)
    private String gradoAcademico;

    @Column(name = "fecha_contratacion")
    private LocalDate fechaContratacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoContrato tipoContrato = TipoContrato.TIEMPO_COMPLETO;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(columnDefinition = "TEXT")
    private String curriculum;

    public enum TipoContrato {
        TIEMPO_COMPLETO,
        TIEMPO_PARCIAL,
        POR_HORAS
    }
}