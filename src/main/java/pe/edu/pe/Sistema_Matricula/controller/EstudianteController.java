package pe.edu.pe.Sistema_Matricula.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.pe.Sistema_Matricula.model.mysql.Estudiante;
import pe.edu.pe.Sistema_Matricula.service.EstudianteService;
import pe.edu.pe.Sistema_Matricula.service.UsuarioService;

@Controller
@RequestMapping("/estudiante")
@RequiredArgsConstructor
public class EstudianteController {

    private final EstudianteService estudianteService;
    private final UsuarioService usuarioService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        
        var estudiante = estudianteService.buscarPorUsuarioId(usuarioId);
        if (estudiante.isPresent()) {
            model.addAttribute("estudiante", estudiante.get());
        }
        
        return "estudiante/dashboard";
    }

    @GetMapping("/listar")
    public String listar(@RequestParam(required = false) String carrera,
                        @RequestParam(required = false) Integer ciclo,
                        Model model) {
        if (carrera != null && ciclo != null) {
            model.addAttribute("estudiantes", 
                estudianteService.listarPorCarreraYCiclo(carrera, ciclo));
        } else if (carrera != null) {
            model.addAttribute("estudiantes", 
                estudianteService.listarPorCarrera(carrera));
        } else {
            model.addAttribute("estudiantes", estudianteService.listarTodos());
        }
        
        return "estudiante/listar";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("estudiante", new Estudiante());
        model.addAttribute("usuarios", usuarioService.listarPorRol(
            pe.edu.pe.Sistema_Matricula.model.mysql.Usuario.Rol.ESTUDIANTE));
        return "estudiante/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Estudiante estudiante,
                         BindingResult result,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "estudiante/formulario";
        }

        try {
            String username = (String) session.getAttribute("username");
            estudianteService.registrarEstudiante(estudiante, username);
            redirectAttributes.addFlashAttribute("mensaje", 
                "Estudiante registrado exitosamente");
            return "redirect:/estudiante/listar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/estudiante/nuevo";
        }
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        var estudiante = estudianteService.buscarPorId(id);
        if (estudiante.isEmpty()) {
            return "redirect:/estudiante/listar";
        }
        
        model.addAttribute("estudiante", estudiante.get());
        return "estudiante/formulario";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                           @Valid @ModelAttribute Estudiante estudiante,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        try {
            String username = (String) session.getAttribute("username");
            estudianteService.actualizarEstudiante(id, estudiante, username);
            redirectAttributes.addFlashAttribute("mensaje", 
                "Estudiante actualizado exitosamente");
            return "redirect:/estudiante/listar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/estudiante/editar/" + id;
        }
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        var estudiante = estudianteService.buscarPorUsuarioId(usuarioId);
        
        if (estudiante.isPresent()) {
            model.addAttribute("estudiante", estudiante.get());
            return "estudiante/perfil";
        }
        
        return "redirect:/estudiante/dashboard";
    }
}