package com.parcial3.controller;

import com.parcial3.model.*;
import com.parcial3.model.enums.Rol;
import com.parcial3.model.enums.EstadoResultado;
import com.parcial3.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/docente")
public class DocenteController {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private CarreraRepository carreraRepository;

    @Autowired
    private BeneficioRepository beneficioRepository;

    @Autowired
    private ResultadoSaberProRepository resultadoRepository;

    private boolean validarDocente(HttpSession session) {
        return session.getAttribute("usuario") != null &&
               session.getAttribute("rol") == Rol.DOCENTE;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!validarDocente(session)) return "redirect:/login";
        model.addAttribute("usuario", session.getAttribute("usuario"));
        model.addAttribute("estudiantesCount", estudianteRepository.count());
        model.addAttribute("carrerasCount", carreraRepository.count());
        return "dashboard-docente";
    }

    // ========== CONSULTAR POR CARRERA ==========
    @GetMapping("/consultar/por-carrera")
    public String consultarPorCarrera(Model model, HttpSession session) {
        if (!validarDocente(session)) return "redirect:/login";
        model.addAttribute("carreras", carreraRepository.findAll());
        return "docente/consultar/por-carrera";
    }

    @PostMapping("/consultar/por-carrera")
    public String resultadoPorCarrera(@RequestParam Long carreraId, Model model, HttpSession session) {
        if (!validarDocente(session)) return "redirect:/login";
        List<Estudiante> estudiantes = estudianteRepository.findByCarreraId(carreraId);
        Carrera carrera = carreraRepository.findById(carreraId).orElse(null);
        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("carrera", carrera);
        return "docente/consultar/resultado-por-carrera";
    }

    // ========== CONSULTAR POR CÉDULA ==========
    @GetMapping("/consultar/por-cedula")
    public String consultarPorCedula() {
        return "docente/consultar/por-cedula";
    }

    @PostMapping("/consultar/por-cedula")
    public String resultadoPorCedula(@RequestParam String numeroDocumento, Model model, HttpSession session) {
        if (!validarDocente(session)) return "redirect:/login";
        Estudiante estudiante = estudianteRepository.findByNumeroDocumento(numeroDocumento).orElse(null);
        
        if (estudiante == null) {
            model.addAttribute("error", "Estudiante no encontrado con documento: " + numeroDocumento);
            return "docente/consultar/por-cedula";
        }
        
        // Obtener último resultado
        List<ResultadoSaberPro> resultados = resultadoRepository.findByEstudianteIdAndEstado(estudiante.getId(), EstadoResultado.ACTIVO);
        ResultadoSaberPro ultimoResultado = resultados.isEmpty() ? null : resultados.get(resultados.size() - 1);
        
        model.addAttribute("estudiante", estudiante);
        model.addAttribute("ultimoResultado", ultimoResultado);
        return "docente/consultar/resultado-por-cedula";
    }

    // ========== INFORMES ==========
    @GetMapping("/informes/total")
    public String informeTotal(Model model, HttpSession session) {
        if (!validarDocente(session)) return "redirect:/login";
        List<Estudiante> estudiantes = estudianteRepository.findByActivoTrue();
        
        // Agregar último puntaje a cada estudiante
        for (Estudiante e : estudiantes) {
            List<ResultadoSaberPro> resultados = resultadoRepository.findByEstudianteIdAndEstado(e.getId(), EstadoResultado.ACTIVO);
            Integer ultimoPuntaje = resultados.isEmpty() ? null : resultados.get(resultados.size() - 1).getPuntajeTotal();
            e.setUltimoPuntaje(ultimoPuntaje);
        }
        
        model.addAttribute("estudiantes", estudiantes);
        return "docente/informes/informe-total";
    }

    @GetMapping("/informes/unico/{id}")
    public String informeUnico(@PathVariable Long id, Model model, HttpSession session) {
        if (!validarDocente(session)) return "redirect:/login";
        Estudiante estudiante = estudianteRepository.findById(id).orElse(null);
        
        if (estudiante == null) {
            return "redirect:/docente/informes/total";
        }
        
        // Obtener último resultado
        List<ResultadoSaberPro> resultados = resultadoRepository.findByEstudianteIdAndEstado(estudiante.getId(), EstadoResultado.ACTIVO);
        ResultadoSaberPro ultimoResultado = resultados.isEmpty() ? null : resultados.get(resultados.size() - 1);
        
        model.addAttribute("estudiante", estudiante);
        model.addAttribute("ultimoResultado", ultimoResultado);
        return "docente/informes/informe-unico";
    }

    @GetMapping("/informes/beneficios")
    public String informeBeneficios(Model model, HttpSession session) {
        if (!validarDocente(session)) return "redirect:/login";
        
        List<Beneficio> beneficios = beneficioRepository.findAll();
        
        // Crear lista con los datos necesarios incluyendo el último puntaje
        List<Map<String, Object>> estudiantesConPuntaje = new ArrayList<>();
        List<Estudiante> estudiantes = estudianteRepository.findByActivoTrue();
        
        for (Estudiante e : estudiantes) {
            Map<String, Object> dato = new HashMap<>();
            dato.put("id", e.getId());
            dato.put("numeroDocumento", e.getNumeroDocumento());
            dato.put("primerNombre", e.getPrimerNombre());
            dato.put("segundoNombre", e.getSegundoNombre());
            dato.put("primerApellido", e.getPrimerApellido());
            dato.put("segundoApellido", e.getSegundoApellido());
            dato.put("email", e.getEmail());
            dato.put("telefono", e.getTelefono());
            dato.put("carrera", e.getCarrera());
            
            // Obtener el último puntaje del estudiante
            List<ResultadoSaberPro> resultados = resultadoRepository.findByEstudianteIdAndEstado(e.getId(), EstadoResultado.ACTIVO);
            Integer ultimoPuntaje = resultados.isEmpty() ? null : resultados.get(resultados.size() - 1).getPuntajeTotal();
            dato.put("ultimoPuntaje", ultimoPuntaje);
            
            estudiantesConPuntaje.add(dato);
        }
        
        model.addAttribute("beneficios", beneficios);
        model.addAttribute("estudiantes", estudiantesConPuntaje);
        model.addAttribute("estudiantesEmpty", estudiantesConPuntaje.isEmpty());
        
        return "docente/informes/informe-beneficios";
    }
}