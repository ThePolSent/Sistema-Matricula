package pe.edu.pe.Sistema_Matricula.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.pe.Sistema_Matricula.model.postgres.Curso;
import pe.edu.pe.Sistema_Matricula.service.CursoService;

@Controller
@RequestMapping("/curso")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    @GetMapping("/listar")
    public String listar(@RequestParam(required = false) String carrera,
                        @RequestParam(required = false) Integer ciclo,
                        Model model) {
        if (carrera != null && ciclo != null) {
            model.addAttribute("cursos", 
                cursoService.listarPorCarreraYCiclo(carrera, ciclo));
        } else if (carrera != null) {
            model.addAttribute("cursos", cursoService.listarPorCarrera(carrera));
        } else {
            model.addAttribute("cursos", cursoService.listarTodos());
        }
        
        model.addAttribute("carreras", new String[]{
            "Ingeniería de Sistemas", 
            "Ingeniería Industrial",
            "Administración",
            "Derecho"
        });
        
        return "curso/listar";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("curso", new Curso());
        model.addAttribute("carreras", new String[]{
            "Ingeniería de Sistemas", 
            "Ingeniería Industrial",
            "Administración",
            "Derecho"
        });
        return "curso/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Curso curso,
                         BindingResult result,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "curso/formulario";
        }

        try {
            String username = (String) session.getAttribute("username");
            cursoService.registrarCurso(curso, username);
            redirectAttributes.addFlashAttribute("mensaje", 
                "Curso registrado exitosamente");
            return "redirect:/curso/listar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/curso/nuevo";
        }
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        var curso = cursoService.buscarPorId(id);
        if (curso.isEmpty()) {
            return "redirect:/curso/listar";
        }
        
        model.addAttribute("curso", curso.get());
        model.addAttribute("carreras", new String[]{
            "Ingeniería de Sistemas", 
            "Ingeniería Industrial",
            "Administración",
            "Derecho"
        });
        return "curso/formulario";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                           @Valid @ModelAttribute Curso curso,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        try {
            String username = (String) session.getAttribute("username");
            cursoService.actualizarCurso(id, curso, username);
            redirectAttributes.addFlashAttribute("mensaje", 
                "Curso actualizado exitosamente");
            return "redirect:/curso/listar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/curso/editar/" + id;
        }
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        var curso = cursoService.buscarPorId(id);
        if (curso.isEmpty()) {
            return "redirect:/curso/listar";
        }
        
        model.addAttribute("curso", curso.get());
        return "curso/detalle";
    }

    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id,
                               @RequestParam Boolean activo,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            String username = (String) session.getAttribute("username");
            cursoService.cambiarEstado(id, activo, username);
            redirectAttributes.addFlashAttribute("mensaje", 
                "Estado actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/curso/listar";
    }

    @GetMapping("/con-vacantes")
    public String cursosConVacantes(Model model) {
        model.addAttribute("cursos", cursoService.listarConVacantes());
        return "curso/listar";
    }
}