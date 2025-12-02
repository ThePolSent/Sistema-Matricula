package pe.edu.pe.Sistema_Matricula.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.pe.Sistema_Matricula.model.postgres.Docente;
import pe.edu.pe.Sistema_Matricula.service.DocenteService;
import pe.edu.pe.Sistema_Matricula.service.UsuarioService;

@Controller
@RequestMapping("/docente")
@RequiredArgsConstructor
public class DocenteController {

    private final DocenteService docenteService;
    private final UsuarioService usuarioService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        
        var docente = docenteService.buscarPorUsuarioId(usuarioId);
        if (docente.isPresent()) {
            model.addAttribute("docente", docente.get());
        }
        
        return "docente/dashboard";
    }

    @GetMapping("/listar")
    public String listar(@RequestParam(required = false) String especialidad,
                        Model model) {
        if (especialidad != null) {
            model.addAttribute("docentes", 
                docenteService.listarPorEspecialidad(especialidad));
        } else {
            model.addAttribute("docentes", docenteService.listarTodos());
        }
        
        model.addAttribute("especialidades", new String[]{
            "Programación",
            "Base de Datos",
            "Redes",
            "Matemáticas",
            "Estadística",
            "Gestión"
        });
        
        return "docente/listar";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("docente", new Docente());
        model.addAttribute("especialidades", new String[]{
            "Programación", "Base de Datos", "Redes", "Matemáticas", 
            "Estadística", "Gestión"
        });
        model.addAttribute("grados", new String[]{
            "Bachiller", "Magister", "Doctor"
        });
        model.addAttribute("tiposContrato", Docente.TipoContrato.values());
        return "docente/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Docente docente,
                         BindingResult result,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "docente/formulario";
        }

        try {
            String username = (String) session.getAttribute("username");
            docenteService.registrarDocente(docente, username);
            redirectAttributes.addFlashAttribute("mensaje", 
                "Docente registrado exitosamente");
            return "redirect:/docente/listar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/docente/nuevo";
        }
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        var docente = docenteService.buscarPorId(id);
        if (docente.isEmpty()) {
            return "redirect:/docente/listar";
        }
        
        model.addAttribute("docente", docente.get());
        model.addAttribute("especialidades", new String[]{
            "Programación", "Base de Datos", "Redes", "Matemáticas", 
            "Estadística", "Gestión"
        });
        model.addAttribute("grados", new String[]{
            "Bachiller", "Magister", "Doctor"
        });
        model.addAttribute("tiposContrato", Docente.TipoContrato.values());
        return "docente/formulario";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                           @Valid @ModelAttribute Docente docente,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        try {
            String username = (String) session.getAttribute("username");
            docenteService.actualizarDocente(id, docente, username);
            redirectAttributes.addFlashAttribute("mensaje", 
                "Docente actualizado exitosamente");
            return "redirect:/docente/listar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/docente/editar/" + id;
        }
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        var docente = docenteService.buscarPorUsuarioId(usuarioId);
        
        if (docente.isPresent()) {
            model.addAttribute("docente", docente.get());
            return "docente/perfil";
        }
        
        return "redirect:/docente/dashboard";
    }

    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id,
                               @RequestParam Boolean activo,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            String username = (String) session.getAttribute("username");
            docenteService.cambiarEstado(id, activo, username);
            redirectAttributes.addFlashAttribute("mensaje", 
                "Estado actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/docente/listar";
    }
}