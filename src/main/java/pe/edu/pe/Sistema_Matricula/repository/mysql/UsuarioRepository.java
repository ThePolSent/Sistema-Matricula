package pe.edu.pe.Sistema_Matricula.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.pe.Sistema_Matricula.model.mysql.Usuario;
import pe.edu.pe.Sistema_Matricula.model.mysql.Estudiante;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<Usuario> findByRol(Usuario.Rol rol);
    List<Usuario> findByActivoTrue();
    
    @Query("SELECT u FROM Usuario u WHERE u.activo = true AND u.rol = :rol")
    List<Usuario> findUsuariosActivosPorRol(Usuario.Rol rol);
}