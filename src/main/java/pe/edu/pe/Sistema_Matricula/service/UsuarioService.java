package pe.edu.pe.Sistema_Matricula.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.pe.Sistema_Matricula.model.mysql.Usuario;
import pe.edu.pe.Sistema_Matricula.repository.mysql.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    @Transactional("mysqlTransactionManager")
    public Usuario registrarUsuario(Usuario usuario) {
        // Validar si ya existe
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new RuntimeException("El username ya existe");
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya existe");
        }

        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setActivo(true);

        Usuario guardado = usuarioRepository.save(usuario);
        
        // Registrar en auditoría
        auditoriaService.registrarOperacion(
            "Usuario", "CREATE", guardado.getId(), guardado.getUsername(), 
            usuario.getRol().name(), "Usuario registrado exitosamente"
        );

        return guardado;
    }

    @Transactional("mysqlTransactionManager")
    public Optional<Usuario> autenticar(String username, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        
        if (usuario.isPresent() && usuario.get().getActivo()) {
            if (passwordEncoder.matches(password, usuario.get().getPassword())) {
                // Actualizar último acceso
                usuario.get().setUltimoAcceso(LocalDateTime.now());
                usuarioRepository.save(usuario.get());
                
                // Registrar login
                auditoriaService.registrarOperacion(
                    "Usuario", "LOGIN", usuario.get().getId(), username, 
                    usuario.get().getRol().name(), "Login exitoso"
                );
                
                return usuario;
            }
        }
        
        // Registrar intento fallido
        auditoriaService.registrarOperacionFallida(
            "Usuario", "LOGIN", null, username, null, "Intento de login fallido"
        );
        
        return Optional.empty();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    public List<Usuario> listarPorRol(Usuario.Rol rol) {
        return usuarioRepository.findByRol(rol);
    }

    @Transactional("mysqlTransactionManager")
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombre(usuarioActualizado.getNombre());
        usuario.setApellido(usuarioActualizado.getApellido());
        usuario.setEmail(usuarioActualizado.getEmail());
        
        Usuario guardado = usuarioRepository.save(usuario);
        
        auditoriaService.registrarOperacion(
            "Usuario", "UPDATE", guardado.getId(), guardado.getUsername(), 
            guardado.getRol().name(), "Usuario actualizado"
        );
        
        return guardado;
    }

    @Transactional("mysqlTransactionManager")
    public void cambiarEstado(Long id, Boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setActivo(activo);
        usuarioRepository.save(usuario);
        
        auditoriaService.registrarOperacion(
            "Usuario", "UPDATE", usuario.getId(), usuario.getUsername(), 
            usuario.getRol().name(), "Estado cambiado a: " + activo
        );
    }

    @Transactional("mysqlTransactionManager")
    public void cambiarPassword(Long id, String passwordAntigua, String passwordNueva) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(passwordAntigua, usuario.getPassword())) {
            throw new RuntimeException("La contraseña antigua no coincide");
        }

        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
        
        auditoriaService.registrarOperacion(
            "Usuario", "UPDATE", usuario.getId(), usuario.getUsername(), 
            usuario.getRol().name(), "Contraseña cambiada"
        );
    }
}