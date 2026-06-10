package com.parcial3.controller;

import com.parcial3.model.*;
import com.parcial3.model.enums.EstadoPago;
import com.parcial3.model.enums.Rol;
import com.parcial3.model.enums.EstadoResultado;
import com.parcial3.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/estudiante")
public class EstudianteController {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private ReciboPagoRepository reciboPagoRepository;

    @Autowired
    private ResultadoSaberProRepository resultadoRepository;

    @Autowired
    private BeneficioRepository beneficioRepository;

    // Usar directorio temporal de Render
    private String uploadDir = "/tmp/recibos";

    private boolean validarEstudiante(HttpSession session) {
        return session.getAttribute("usuario") != null &&
               session.getAttribute("rol") == Rol.ESTUDIANTE;
    }

    private Estudiante getEstudianteSesion(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        return estudianteRepository.findByNumeroDocumento(usuario.getNumeroDocumento()).orElse(null);
    }

    private int getPuntajeMinimoPorCarrera(Estudiante estudiante) {
        if (estudiante.getCarrera() == null) {
            return 120;
        }
        String nombreCarrera = estudiante.getCarrera().getNombre().toLowerCase();
        if (nombreCarrera.contains("tecnologia") || nombreCarrera.contains("tecnológico")) {
            return 80;
        }
        return 120;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!validarEstudiante(session)) return "redirect:/login";
        model.addAttribute("usuario", session.getAttribute("usuario"));
        
        Estudiante estudiante = getEstudianteSesion(session);
        model.addAttribute("estadoPago", estudiante.getEstadoPago().name());
        model.addAttribute("carreraNombre", estudiante.getCarrera() != null ? estudiante.getCarrera().getNombre() : "Sin asignar");
        
        List<ResultadoSaberPro> resultados = resultadoRepository.findByEstudianteIdAndEstado(estudiante.getId(), EstadoResultado.ACTIVO);
        ResultadoSaberPro ultimoResultado = resultados.isEmpty() ? null : resultados.get(resultados.size() - 1);
        model.addAttribute("ultimoPuntaje", ultimoResultado != null ? ultimoResultado.getPuntajeTotal() : null);
        
        return "dashboard-estudiante";
    }

    @GetMapping("/datos-personales")
    public String datosPersonales(HttpSession session, Model model) {
        if (!validarEstudiante(session)) return "redirect:/login";
        Estudiante estudiante = getEstudianteSesion(session);
        model.addAttribute("estudiante", estudiante);
        return "estudiante/datos-personales";
    }

    @GetMapping("/cargar-pago")
    public String mostrarFormCargarPago(HttpSession session, Model model) {
        if (!validarEstudiante(session)) return "redirect:/login";
        Estudiante estudiante = getEstudianteSesion(session);
        List<ReciboPago> recibos = reciboPagoRepository.findByEstudianteId(estudiante.getId());
        boolean puedeSubir = true;
        for (ReciboPago recibo : recibos) {
            if (recibo.getEstado() == EstadoPago.PENDIENTE || recibo.getEstado() == EstadoPago.APROBADO) {
                puedeSubir = false;
                break;
            }
        }
        model.addAttribute("puedeSubir", puedeSubir);
        model.addAttribute("recibos", recibos);
        return "estudiante/cargar-pago";
    }

    @PostMapping("/cargar-pago")
    public String cargarPago(@RequestParam("archivo") MultipartFile archivo, HttpSession session, Model model) {
        if (!validarEstudiante(session)) return "redirect:/login";
        Estudiante estudiante = getEstudianteSesion(session);

        List<ReciboPago> recibos = reciboPagoRepository.findByEstudianteId(estudiante.getId());
        for (ReciboPago recibo : recibos) {
            if (recibo.getEstado() == EstadoPago.PENDIENTE || recibo.getEstado() == EstadoPago.APROBADO) {
                model.addAttribute("error", "Ya tienes un pago pendiente o aprobado");
                model.addAttribute("puedeSubir", false);
                model.addAttribute("recibos", recibos);
                return "estudiante/cargar-pago";
            }
        }

        if (archivo.isEmpty()) {
            model.addAttribute("error", "Debe seleccionar un archivo");
            model.addAttribute("puedeSubir", true);
            model.addAttribute("recibos", recibos);
            return "estudiante/cargar-pago";
        }

        try {
            // Crear directorio si no existe
            File directorio = new File(uploadDir);
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            String nombreOriginal = archivo.getOriginalFilename();
            String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
            String nombreArchivo = UUID.randomUUID().toString() + extension;
            String rutaCompleta = uploadDir + File.separator + nombreArchivo;
            archivo.transferTo(new File(rutaCompleta));

            ReciboPago recibo = new ReciboPago(estudiante, nombreOriginal, rutaCompleta, LocalDateTime.now());
            reciboPagoRepository.save(recibo);

            model.addAttribute("success", "Recibo cargado exitosamente. Espera aprobación del coordinador.");
            model.addAttribute("puedeSubir", false);
            
            recibos = reciboPagoRepository.findByEstudianteId(estudiante.getId());
            model.addAttribute("recibos", recibos);
        } catch (IOException e) {
            model.addAttribute("error", "Error al guardar el archivo: " + e.getMessage());
            model.addAttribute("puedeSubir", true);
            model.addAttribute("recibos", recibos);
        }

        return "estudiante/cargar-pago";
    }

    @GetMapping("/ultimo-resultado")
    public String ultimoResultado(HttpSession session, Model model) {
        if (!validarEstudiante(session)) return "redirect:/login";
        Estudiante estudiante = getEstudianteSesion(session);

        if (estudiante.getEstadoPago() != EstadoPago.APROBADO) {
            model.addAttribute("error", "Debes estar aprobado para ver tus resultados");
            return "estudiante/ultimo-resultado";
        }

        List<ResultadoSaberPro> resultados = resultadoRepository.findByEstudianteIdAndEstado(estudiante.getId(), EstadoResultado.ACTIVO);
        ResultadoSaberPro ultimoResultado = resultados.isEmpty() ? null : resultados.get(resultados.size() - 1);
        
        if (ultimoResultado != null) {
            int puntajeMinimo = getPuntajeMinimoPorCarrera(estudiante);
            boolean reprobado = ultimoResultado.getPuntajeTotal() < puntajeMinimo;
            model.addAttribute("reprobado", reprobado);
            model.addAttribute("puntajeMinimo", puntajeMinimo);
            model.addAttribute("carreraNombre", estudiante.getCarrera() != null ? estudiante.getCarrera().getNombre() : "Sin carrera");
        }
        
        model.addAttribute("resultado", ultimoResultado);
        return "estudiante/ultimo-resultado";
    }

    @GetMapping("/todos-resultados")
    public String todosResultados(HttpSession session, Model model) {
        if (!validarEstudiante(session)) return "redirect:/login";
        Estudiante estudiante = getEstudianteSesion(session);

        if (estudiante.getEstadoPago() != EstadoPago.APROBADO) {
            model.addAttribute("error", "Debes estar aprobado para ver tus resultados");
            return "estudiante/todos-resultados";
        }

        List<ResultadoSaberPro> resultados = resultadoRepository.findByEstudianteIdAndEstado(estudiante.getId(), EstadoResultado.ACTIVO);
        model.addAttribute("resultados", resultados);
        return "estudiante/todos-resultados";
    }

    @GetMapping("/resultado/{id}")
    public String verResultadoDetalle(@PathVariable Long id, HttpSession session, Model model) {
        if (!validarEstudiante(session)) return "redirect:/login";
        Estudiante estudiante = getEstudianteSesion(session);

        if (estudiante.getEstadoPago() != EstadoPago.APROBADO) {
            model.addAttribute("error", "Debes estar aprobado para ver tus resultados");
            return "redirect:/estudiante/todos-resultados";
        }

        ResultadoSaberPro resultado = resultadoRepository.findById(id).orElse(null);

        if (resultado == null || !resultado.getEstudiante().getId().equals(estudiante.getId())) {
            return "redirect:/estudiante/todos-resultados";
        }

        model.addAttribute("resultado", resultado);
        return "estudiante/ver-resultado";
    }

    @GetMapping("/mis-beneficios")
    public String misBeneficios(HttpSession session, Model model) {
        if (!validarEstudiante(session)) return "redirect:/login";
        Estudiante estudiante = getEstudianteSesion(session);

        if (estudiante.getEstadoPago() != EstadoPago.APROBADO) {
            model.addAttribute("error", "Debes estar aprobado para ver tus beneficios");
            return "estudiante/mis-beneficios";
        }

        List<ResultadoSaberPro> resultados = resultadoRepository.findByEstudianteIdAndEstado(estudiante.getId(), EstadoResultado.ACTIVO);
        ResultadoSaberPro ultimoResultado = resultados.isEmpty() ? null : resultados.get(resultados.size() - 1);
        
        int puntajeMinimoAprobar = getPuntajeMinimoPorCarrera(estudiante);
        boolean reprobado = false;
        
        if (ultimoResultado == null) {
            model.addAttribute("sinResultados", true);
        } else if (ultimoResultado.getPuntajeTotal() < puntajeMinimoAprobar) {
            reprobado = true;
            model.addAttribute("reprobado", true);
            model.addAttribute("puntajeObtenido", ultimoResultado.getPuntajeTotal());
            model.addAttribute("puntajeMinimo", puntajeMinimoAprobar);
            model.addAttribute("carreraNombre", estudiante.getCarrera() != null ? estudiante.getCarrera().getNombre() : "Sin carrera");
        } else {
            List<Beneficio> todosBeneficios = beneficioRepository.findAll();
            List<Beneficio> beneficiosObtenidos = new ArrayList<>();
            for (Beneficio beneficio : todosBeneficios) {
                if (ultimoResultado.getPuntajeTotal() >= beneficio.getPuntajeMinimo()) {
                    beneficiosObtenidos.add(beneficio);
                }
            }
            model.addAttribute("beneficios", beneficiosObtenidos);
        }
        
        model.addAttribute("reprobado", reprobado);
        model.addAttribute("ultimoResultado", ultimoResultado);
        model.addAttribute("puntajeTotal", ultimoResultado != null ? ultimoResultado.getPuntajeTotal() : null);
        model.addAttribute("carrera", estudiante.getCarrera());
        
        return "estudiante/mis-beneficios";
    }
}