package pe.edu.pe.Sistema_Matricula.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.pe.Sistema_Matricula.model.mysql.Usuario;
import pe.edu.pe.Sistema_Matricula.service.UsuarioService;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UsuarioService usuarioService;

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("mensaje", "Sesión cerrada exitosamente");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                       @RequestParam String password,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuario = usuarioService.autenticar(username, password);
        
        if (usuario.isPresent()) {
            session.setAttribute("usuarioId", usuario.get().getId());
            session.setAttribute("username", usuario.get().getUsername());
            session.setAttribute("rol", usuario.get().getRol().name());
            session.setAttribute("nombreCompleto", 
                usuario.get().getNombre() + " " + usuario.get().getApellido());
            
            return "redirect:/dashboard";
        }
        
        redirectAttributes.addFlashAttribute("error", "Credenciales inválidas");
        return "redirect:/login?error=true";
    }

    @GetMapping("/registro")
    public String registroPage(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute Usuario usuario,
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "registro";
        }

        try {
            usuario.setRol(Usuario.Rol.ESTUDIANTE); // Por defecto
            usuarioService.registrarUsuario(usuario);
            redirectAttributes.addFlashAttribute("mensaje", 
                "Registro exitoso. Puede iniciar sesión.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registro";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String rol = (String) session.getAttribute("rol");
        String username = (String) session.getAttribute("username");
        
        model.addAttribute("usuario", username);
        model.addAttribute("rol", rol);
        
        if ("ADMIN".equals(rol)) {
            return "admin/dashboard";
        } else if ("ESTUDIANTE".equals(rol)) {
            return "estudiante/dashboard";
        } else if ("DOCENTE".equals(rol)) {
            return "docente/dashboard";
        }
        
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}