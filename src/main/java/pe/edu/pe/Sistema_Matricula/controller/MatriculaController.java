package pe.edu.pe.Sistema_Matricula.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.pe.Sistema_Matricula.model.mongo.Matricula;
import pe.edu.pe.Sistema_Matricula.service.*;

import java.util.List;

@Controller
@RequestMapping("/matricula")
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaService matriculaService;
    private final EstudianteService estudianteService;
    private final CursoService cursoService;

    @GetMapping("/nueva")
    public String nuevaForm(HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        var estudiante = estudianteService.buscarPorUsuarioId(usuarioId);
        
        if (estudiante.isEmpty()) {
            return "redirect:/estudiante/dashboard";
        }
        
        model.addAttribute("estudiante", estudiante.get());
        model.addAttribute("cursos", cursoService.listarActivosPorCarreraYCiclo(
            estudiante.get().getCarrera(), 
            estudiante.get().getCiclo()));
        
        return "matricula/formulario";
    }

    @PostMapping("/generar")
    public String generar(@RequestParam Long estudianteId,
                         @RequestParam List<Long> cursosIds,
                         @RequestParam String periodo,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        try {
            String username = (String) session.getAttribute("username");
            Matricula matricula = matriculaService.generarMatricula(
                estudianteId, cursosIds, periodo, username);
            
            redirectAttributes.addFlashAttribute("mensaje", 
                "Matrícula generada exitosamente. Código: " + matricula.getCodigoMatricula());
            return "redirect:/matricula/detalle/" + matricula.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/matricula/nueva";
        }
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable String id, Model model) {
        var matricula = matriculaService.buscarPorId(id);
        if (matricula.isEmpty()) {
            return "redirect:/matricula/listar";
        }
        
        model.addAttribute("matricula", matricula.get());
        return "matricula/detalle";
    }

    @GetMapping("/listar")
    public String listar(@RequestParam(required = false) String periodo,
                        @RequestParam(required = false) String estado,
                        HttpSession session,
                        Model model) {
        String rol = (String) session.getAttribute("rol");
        
        if ("ESTUDIANTE".equals(rol)) {
            Long usuarioId = (Long) session.getAttribute("usuarioId");
            var estudiante = estudianteService.buscarPorUsuarioId(usuarioId);
            if (estudiante.isPresent()) {
                model.addAttribute("matriculas", 
                    matriculaService.listarPorEstudiante(estudiante.get().getId()));
            }
        } else if (periodo != null) {
            model.addAttribute("matriculas", 
                matriculaService.listarPorPeriodo(periodo));
        } else if (estado != null) {
            model.addAttribute("matriculas", 
                matriculaService.listarPorEstado(
                    Matricula.EstadoMatricula.valueOf(estado)));
        } else {
            model.addAttribute("matriculas", 
                matriculaService.listarPorEstado(
                    Matricula.EstadoMatricula.PENDIENTE));
        }
        
        return "matricula/listar";
    }

    @PostMapping("/aprobar/{id}")
    public String aprobar(@PathVariable String id,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        try {
            String username = (String) session.getAttribute("username");
            matriculaService.aprobarMatricula(id, username);
            redirectAttributes.addFlashAttribute("mensaje", 
                "Matrícula aprobada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/matricula/detalle/" + id;
    }

    @PostMapping("/rechazar/{id}")
    public String rechazar(@PathVariable String id,
                          @RequestParam String observaciones,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        try {
            String username = (String) session.getAttribute("username");
            matriculaService.rechazarMatricula(id, observaciones, username);
            redirectAttributes.addFlashAttribute("mensaje", 
                "Matrícula rechazada");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/matricula/detalle/" + id;
    }

    @PostMapping("/cancelar/{id}")
    public String cancelar(@PathVariable String id,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        try {
            String username = (String) session.getAttribute("username");
            matriculaService.cancelarMatricula(id, username);
            redirectAttributes.addFlashAttribute("mensaje", 
                "Matrícula cancelada");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/matricula/listar";
    }

    @GetMapping("/mis-matriculas")
    public String misMatriculas(HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        var estudiante = estudianteService.buscarPorUsuarioId(usuarioId);
        
        if (estudiante.isPresent()) {
            model.addAttribute("matriculas", 
                matriculaService.listarPorEstudiante(estudiante.get().getId()));
            model.addAttribute("estudiante", estudiante.get());
        }
        
        return "matricula/mis-matriculas";
    }
}