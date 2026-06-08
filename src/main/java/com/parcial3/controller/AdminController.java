package com.parcial3.controller;

import com.parcial3.model.*;
import com.parcial3.model.enums.Rol;
import com.parcial3.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private DocenteRepository docenteRepository;

    @Autowired
    private CarreraRepository carreraRepository;

    @Autowired
    private BeneficioRepository beneficioRepository;

    @Autowired
    private ResolucionBeneficioRepository resolucionBeneficioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;  // ← Agregar este

    private boolean validarAdmin(HttpSession session) {
        return session.getAttribute("usuario") != null &&
               session.getAttribute("rol") == Rol.ADMIN;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!validarAdmin(session)) return "redirect:/login";
        model.addAttribute("usuario", session.getAttribute("usuario"));
        model.addAttribute("docentesCount", docenteRepository.count());
        model.addAttribute("carrerasCount", carreraRepository.count());
        model.addAttribute("beneficiosCount", beneficioRepository.count());
        model.addAttribute("coordinadoresCount", usuarioRepository.findByRol(Rol.COORDINADOR).size());
        return "dashboard-admin";
    }

    // ========== CRUD DOCENTES ==========
    @GetMapping("/docentes")
    public String listarDocentes(Model model, HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        model.addAttribute("docentes", docenteRepository.findAll());
        model.addAttribute("carreras", carreraRepository.findAll());
        return "admin/docentes/listar";
    }

    @GetMapping("/docentes/registrar")
    public String mostrarFormRegistrarDocente(Model model, HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        model.addAttribute("carreras", carreraRepository.findAll());
        return "admin/docentes/registrar";
    }

    @PostMapping("/docentes/registrar")
    public String registrarDocente(@RequestParam String numeroDocumento,
                                    @RequestParam String primerNombre,
                                    @RequestParam(required = false) String segundoNombre,
                                    @RequestParam String primerApellido,
                                    @RequestParam(required = false) String segundoApellido,
                                    @RequestParam String email,
                                    @RequestParam String telefono,
                                    @RequestParam String password,
                                    @RequestParam Long carreraId,
                                    HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        Carrera carrera = carreraRepository.findById(carreraId).orElse(null);
        Docente docente = new Docente(numeroDocumento, primerNombre, segundoNombre,
                primerApellido, segundoApellido, email, telefono, password, carrera);
        docenteRepository.save(docente);
        return "redirect:/admin/docentes";
    }

    @GetMapping("/docentes/editar/{id}")
    public String mostrarFormEditarDocente(@PathVariable Long id, Model model, HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        Docente docente = docenteRepository.findById(id).orElse(null);
        model.addAttribute("docente", docente);
        model.addAttribute("carreras", carreraRepository.findAll());
        return "admin/docentes/editar";
    }

    @PostMapping("/docentes/editar/{id}")
    public String editarDocente(@PathVariable Long id,
                                 @RequestParam String primerNombre,
                                 @RequestParam(required = false) String segundoNombre,
                                 @RequestParam String primerApellido,
                                 @RequestParam(required = false) String segundoApellido,
                                 @RequestParam String email,
                                 @RequestParam String telefono,
                                 @RequestParam Long carreraId,
                                 HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        Docente docente = docenteRepository.findById(id).orElse(null);
        if (docente != null) {
            docente.setPrimerNombre(primerNombre);
            docente.setSegundoNombre(segundoNombre);
            docente.setPrimerApellido(primerApellido);
            docente.setSegundoApellido(segundoApellido);
            docente.setEmail(email);
            docente.setTelefono(telefono);
            Carrera carrera = carreraRepository.findById(carreraId).orElse(null);
            docente.setCarrera(carrera);
            docenteRepository.save(docente);
        }
        return "redirect:/admin/docentes";
    }

    @GetMapping("/docentes/eliminar/{id}")
    public String eliminarDocente(@PathVariable Long id, HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        docenteRepository.deleteById(id);
        return "redirect:/admin/docentes";
    }

    // ========== CRUD CARRERAS ==========
    @GetMapping("/carreras")
    public String listarCarreras(Model model, HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        model.addAttribute("carreras", carreraRepository.findAll());
        return "admin/carreras/listar";
    }

    @GetMapping("/carreras/registrar")
    public String mostrarFormRegistrarCarrera(HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        return "admin/carreras/registrar";
    }

    @PostMapping("/carreras/registrar")
    public String registrarCarrera(@RequestParam String nombre,
                                    @RequestParam String codigo,
                                    @RequestParam String descripcion,
                                    HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        Carrera carrera = new Carrera(nombre, codigo, descripcion);
        carreraRepository.save(carrera);
        return "redirect:/admin/carreras";
    }

    @GetMapping("/carreras/editar/{id}")
    public String mostrarFormEditarCarrera(@PathVariable Long id, Model model, HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        Carrera carrera = carreraRepository.findById(id).orElse(null);
        model.addAttribute("carrera", carrera);
        return "admin/carreras/editar";
    }

    @PostMapping("/carreras/editar/{id}")
    public String editarCarrera(@PathVariable Long id,
                                 @RequestParam String nombre,
                                 @RequestParam String codigo,
                                 @RequestParam String descripcion,
                                 HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        Carrera carrera = carreraRepository.findById(id).orElse(null);
        if (carrera != null) {
            carrera.setNombre(nombre);
            carrera.setCodigo(codigo);
            carrera.setDescripcion(descripcion);
            carreraRepository.save(carrera);
        }
        return "redirect:/admin/carreras";
    }

    @GetMapping("/carreras/eliminar/{id}")
    public String eliminarCarrera(@PathVariable Long id, HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        carreraRepository.deleteById(id);
        return "redirect:/admin/carreras";
    }

    // ========== CRUD COORDINADORES ==========
    @GetMapping("/coordinadores")
    public String listarCoordinadores(Model model, HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        model.addAttribute("coordinadores", usuarioRepository.findByRol(Rol.COORDINADOR));
        return "admin/coordinadores/listar";
    }

    @GetMapping("/coordinadores/registrar")
    public String mostrarFormRegistrarCoordinador(Model model, HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        return "admin/coordinadores/registrar";
    }

    @PostMapping("/coordinadores/registrar")
    public String registrarCoordinador(@RequestParam String numeroDocumento,
                                        @RequestParam String primerNombre,
                                        @RequestParam(required = false) String segundoNombre,
                                        @RequestParam String primerApellido,
                                        @RequestParam(required = false) String segundoApellido,
                                        @RequestParam String email,
                                        @RequestParam String telefono,
                                        @RequestParam String password,
                                        HttpSession session,
                                        Model model) {
        if (!validarAdmin(session)) return "redirect:/login";
        
        // Verificar si ya existe
        if (usuarioRepository.findByNumeroDocumento(numeroDocumento).isPresent()) {
            model.addAttribute("error", "El documento " + numeroDocumento + " ya está registrado");
            return "admin/coordinadores/registrar";
        }
        
        // Verificar si ya existe el email
        if (usuarioRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "El email " + email + " ya está registrado");
            return "admin/coordinadores/registrar";
        }
        
        Usuario coordinador = new Usuario();
        coordinador.setNumeroDocumento(numeroDocumento);
        coordinador.setPrimerNombre(primerNombre);
        coordinador.setSegundoNombre(segundoNombre);
        coordinador.setPrimerApellido(primerApellido);
        coordinador.setSegundoApellido(segundoApellido);
        coordinador.setEmail(email);
        coordinador.setTelefono(telefono);
        coordinador.setPassword(password);
        coordinador.setRol(Rol.COORDINADOR);
        
        usuarioRepository.save(coordinador);
        return "redirect:/admin/coordinadores";
    }

    @GetMapping("/coordinadores/editar/{id}")
    public String mostrarFormEditarCoordinador(@PathVariable Long id, Model model, HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        Usuario coordinador = usuarioRepository.findById(id).orElse(null);
        if (coordinador == null || coordinador.getRol() != Rol.COORDINADOR) {
            return "redirect:/admin/coordinadores";
        }
        model.addAttribute("coordinador", coordinador);
        return "admin/coordinadores/editar";
    }

    @PostMapping("/coordinadores/editar/{id}")
    public String editarCoordinador(@PathVariable Long id,
                                     @RequestParam String primerNombre,
                                     @RequestParam(required = false) String segundoNombre,
                                     @RequestParam String primerApellido,
                                     @RequestParam(required = false) String segundoApellido,
                                     @RequestParam String email,
                                     @RequestParam String telefono,
                                     HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        Usuario coordinador = usuarioRepository.findById(id).orElse(null);
        if (coordinador != null && coordinador.getRol() == Rol.COORDINADOR) {
            coordinador.setPrimerNombre(primerNombre);
            coordinador.setSegundoNombre(segundoNombre);
            coordinador.setPrimerApellido(primerApellido);
            coordinador.setSegundoApellido(segundoApellido);
            coordinador.setEmail(email);
            coordinador.setTelefono(telefono);
            usuarioRepository.save(coordinador);
        }
        return "redirect:/admin/coordinadores";
    }

    @GetMapping("/coordinadores/eliminar/{id}")
    public String eliminarCoordinador(@PathVariable Long id, HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        Usuario coordinador = usuarioRepository.findById(id).orElse(null);
        if (coordinador != null && coordinador.getRol() == Rol.COORDINADOR) {
            usuarioRepository.deleteById(id);
        }
        return "redirect:/admin/coordinadores";
    }

    // ========== BENEFICIOS ==========
    @GetMapping("/beneficios")
    public String listarBeneficios(Model model, HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        return "redirect:/admin/resolucion/beneficios";
    }

    @GetMapping("/beneficios/registrar")
    public String mostrarFormRegistrarBeneficio(HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        return "admin/beneficios/registrar";
    }

    @PostMapping("/beneficios/registrar")
    public String registrarBeneficio(@RequestParam String nombre,
                                      @RequestParam Integer puntajeMinimo,
                                      @RequestParam String descripcion,
                                      HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        Beneficio beneficio = new Beneficio(nombre, puntajeMinimo, descripcion);
        beneficioRepository.save(beneficio);
        return "redirect:/admin/resolucion/beneficios";
    }

    @GetMapping("/beneficios/editar/{id}")
    public String mostrarFormEditarBeneficio(@PathVariable Long id, Model model, HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        Beneficio beneficio = beneficioRepository.findById(id).orElse(null);
        model.addAttribute("beneficio", beneficio);
        return "admin/beneficios/editar";
    }

    @PostMapping("/beneficios/editar/{id}")
    public String editarBeneficio(@PathVariable Long id,
                                   @RequestParam String nombre,
                                   @RequestParam Integer puntajeMinimo,
                                   @RequestParam String descripcion,
                                   HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        Beneficio beneficio = beneficioRepository.findById(id).orElse(null);
        if (beneficio != null) {
            beneficio.setNombre(nombre);
            beneficio.setPuntajeMinimo(puntajeMinimo);
            beneficio.setDescripcion(descripcion);
            beneficioRepository.save(beneficio);
        }
        return "redirect:/admin/resolucion/beneficios";
    }

    @GetMapping("/beneficios/eliminar/{id}")
    public String eliminarBeneficio(@PathVariable Long id, HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        beneficioRepository.deleteById(id);
        return "redirect:/admin/resolucion/beneficios";
    }

    // ========== RESOLUCIÓN DE BENEFICIOS ==========
    @GetMapping("/resolucion/beneficios")
    public String configurarResolucion(Model model, HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        
        List<Beneficio> beneficios = beneficioRepository.findAll();
        model.addAttribute("beneficios", beneficios);
        
        List<ResolucionBeneficio> resoluciones = resolucionBeneficioRepository.findAll();
        ResolucionBeneficio resolucionActiva = resolucionBeneficioRepository.findByActivoTrue().orElse(null);
        
        model.addAttribute("resoluciones", resoluciones);
        model.addAttribute("resolucionActiva", resolucionActiva);
        
        return "admin/resolucion/beneficios";
    }

    @PostMapping("/resolucion/beneficios/nueva")
    public String nuevaResolucion(@RequestParam String descripcion, HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        ResolucionBeneficio resolucion = new ResolucionBeneficio(LocalDate.now(), descripcion, false);
        resolucionBeneficioRepository.save(resolucion);
        return "redirect:/admin/resolucion/beneficios";
    }

    @PostMapping("/resolucion/beneficios/activar")
    public String activarResolucion(@RequestParam Long id, HttpSession session) {
        if (!validarAdmin(session)) return "redirect:/login";
        
        List<ResolucionBeneficio> todas = resolucionBeneficioRepository.findAll();
        for (ResolucionBeneficio r : todas) {
            r.setActivo(false);
            resolucionBeneficioRepository.save(r);
        }
        
        ResolucionBeneficio resolucion = resolucionBeneficioRepository.findById(id).orElse(null);
        if (resolucion != null) {
            resolucion.setActivo(true);
            resolucionBeneficioRepository.save(resolucion);
        }
        return "redirect:/admin/resolucion/beneficios";
    }
}