package com.parcial3.controller;

import com.parcial3.model.*;
import com.parcial3.model.enums.*;
import com.parcial3.repository.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/coordinador")
public class CoordinadorController {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private CarreraRepository carreraRepository;

    @Autowired
    private ResultadoSaberProRepository resultadoRepository;

    @Autowired
    private DetalleCompetenciaRepository detalleCompetenciaRepository;

    @Autowired
    private ReciboPagoRepository reciboPagoRepository;

    @Autowired
    private BeneficioRepository beneficioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private boolean validarCoordinador(HttpSession session) {
        return session.getAttribute("usuario") != null &&
               session.getAttribute("rol") == Rol.COORDINADOR;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!validarCoordinador(session)) return "redirect:/login";
        model.addAttribute("usuario", session.getAttribute("usuario"));
        
        model.addAttribute("estudiantesCount", estudianteRepository.count());
        model.addAttribute("pagosPendientesCount", reciboPagoRepository.findByEstado(EstadoPago.PENDIENTE).size());
        model.addAttribute("resultadosCount", resultadoRepository.count());
        model.addAttribute("aprobadosCount", estudianteRepository.findByEstadoPago(EstadoPago.APROBADO).size());
        
        return "dashboard-coordinador";
    }

    // ========== CRUD ESTUDIANTES ==========
    @GetMapping("/estudiantes")
    public String listarEstudiantes(Model model, HttpSession session) {
        if (!validarCoordinador(session)) return "redirect:/login";
        model.addAttribute("estudiantes", estudianteRepository.findAll());
        model.addAttribute("carreras", carreraRepository.findAll());
        return "coordinador/estudiantes/listar";
    }

    @GetMapping("/estudiantes/registrar")
    public String mostrarFormRegistrarEstudiante(Model model, HttpSession session) {
        if (!validarCoordinador(session)) return "redirect:/login";
        model.addAttribute("carreras", carreraRepository.findAll());
        return "coordinador/estudiantes/registrar";
    }

    @PostMapping("/estudiantes/registrar")
    public String registrarEstudiante(@RequestParam String numeroDocumento,
                                       @RequestParam String primerNombre,
                                       @RequestParam(required = false) String segundoNombre,
                                       @RequestParam String primerApellido,
                                       @RequestParam(required = false) String segundoApellido,
                                       @RequestParam String email,
                                       @RequestParam String telefono,
                                       @RequestParam String password,
                                       @RequestParam Long carreraId,
                                       HttpSession session) {
        if (!validarCoordinador(session)) return "redirect:/login";
        Carrera carrera = carreraRepository.findById(carreraId).orElse(null);
        Estudiante estudiante = new Estudiante(numeroDocumento, primerNombre, segundoNombre,
                primerApellido, segundoApellido, email, telefono, password, carrera);
        estudianteRepository.save(estudiante);
        return "redirect:/coordinador/estudiantes";
    }

    @GetMapping("/estudiantes/editar/{id}")
    public String mostrarFormEditarEstudiante(@PathVariable Long id, Model model, HttpSession session) {
        if (!validarCoordinador(session)) return "redirect:/login";
        Estudiante estudiante = estudianteRepository.findById(id).orElse(null);
        model.addAttribute("estudiante", estudiante);
        model.addAttribute("carreras", carreraRepository.findAll());
        return "coordinador/estudiantes/editar";
    }

    @PostMapping("/estudiantes/editar/{id}")
    public String editarEstudiante(@PathVariable Long id,
                                    @RequestParam String primerNombre,
                                    @RequestParam(required = false) String segundoNombre,
                                    @RequestParam String primerApellido,
                                    @RequestParam(required = false) String segundoApellido,
                                    @RequestParam String email,
                                    @RequestParam String telefono,
                                    @RequestParam Long carreraId,
                                    HttpSession session) {
        if (!validarCoordinador(session)) return "redirect:/login";
        Estudiante estudiante = estudianteRepository.findById(id).orElse(null);
        if (estudiante != null) {
            estudiante.setPrimerNombre(primerNombre);
            estudiante.setSegundoNombre(segundoNombre);
            estudiante.setPrimerApellido(primerApellido);
            estudiante.setSegundoApellido(segundoApellido);
            estudiante.setEmail(email);
            estudiante.setTelefono(telefono);
            Carrera carrera = carreraRepository.findById(carreraId).orElse(null);
            estudiante.setCarrera(carrera);
            estudianteRepository.save(estudiante);
        }
        return "redirect:/coordinador/estudiantes";
    }

    @GetMapping("/estudiantes/eliminar/{id}")
    public String eliminarEstudiante(@PathVariable Long id, HttpSession session) {
        if (!validarCoordinador(session)) return "redirect:/login";
        estudianteRepository.deleteById(id);
        return "redirect:/coordinador/estudiantes";
    }

    // ========== APROBAR PAGOS ==========
    @GetMapping("/estudiantes/aprobar-pagos")
    public String listarPagosPendientes(Model model, HttpSession session) {
        if (!validarCoordinador(session)) return "redirect:/login";
        List<ReciboPago> recibosPendientes = reciboPagoRepository.findByEstado(EstadoPago.PENDIENTE);
        model.addAttribute("recibos", recibosPendientes);
        return "coordinador/estudiantes/aprobar-pago";
    }

    @PostMapping("/estudiantes/aprobar-pago/{id}")
    public String aprobarPago(@PathVariable Long id,
                              @RequestParam String accion,
                              @RequestParam(required = false) String observacion,
                              HttpSession session) {
        if (!validarCoordinador(session)) return "redirect:/login";
        ReciboPago recibo = reciboPagoRepository.findById(id).orElse(null);
        if (recibo != null) {
            if ("aprobar".equals(accion)) {
                recibo.setEstado(EstadoPago.APROBADO);
                recibo.setFechaAprobacion(LocalDateTime.now());
                Estudiante estudiante = recibo.getEstudiante();
                estudiante.setEstadoPago(EstadoPago.APROBADO);
                estudiante.setFechaAprobacion(LocalDateTime.now());
                estudianteRepository.save(estudiante);
            } else if ("rechazar".equals(accion)) {
                recibo.setEstado(EstadoPago.RECHAZADO);
                recibo.setObservacion(observacion);
                recibo.setFechaAprobacion(LocalDateTime.now());
            }
            reciboPagoRepository.save(recibo);
        }
        return "redirect:/coordinador/estudiantes/aprobar-pagos";
    }

    // ========== CRUD RESULTADOS ==========
    @GetMapping("/resultados")
    public String listarResultados(Model model, HttpSession session) {
        if (!validarCoordinador(session)) return "redirect:/login";
        model.addAttribute("resultados", resultadoRepository.findAll());
        return "coordinador/resultados/listar";
    }

    @GetMapping("/resultados/registrar")
    public String mostrarFormRegistrarResultado(Model model, HttpSession session) {
        if (!validarCoordinador(session)) return "redirect:/login";
        List<Estudiante> estudiantesAprobados = estudianteRepository.findByEstadoPago(EstadoPago.APROBADO);
        model.addAttribute("estudiantes", estudiantesAprobados);
        model.addAttribute("competencias", Competencia.values());
        return "coordinador/resultados/registrar";
    }

    @PostMapping("/resultados/registrar")
    public String registrarResultado(@RequestParam Long estudianteId,
                                      @RequestParam String fechaExamen,
                                      @RequestParam Integer puntajeTotal,
                                      @RequestParam(value = "puntajes", required = false) List<Integer> puntajes,
                                      HttpSession session,
                                      Model model) {
        if (!validarCoordinador(session)) return "redirect:/login";
        
        System.out.println("=== REGISTRANDO RESULTADO ===");
        System.out.println("Estudiante ID: " + estudianteId);
        System.out.println("Fecha: " + fechaExamen);
        System.out.println("Puntaje Total: " + puntajeTotal);
        System.out.println("Puntajes recibidos: " + puntajes);
        
        Estudiante estudiante = estudianteRepository.findById(estudianteId).orElse(null);
        if (estudiante == null) {
            System.out.println("ERROR: Estudiante no encontrado");
            model.addAttribute("error", "Estudiante no encontrado");
            model.addAttribute("estudiantes", estudianteRepository.findByEstadoPago(EstadoPago.APROBADO));
            model.addAttribute("competencias", Competencia.values());
            return "coordinador/resultados/registrar";
        }
        
        if (puntajes == null || puntajes.size() < 8) {
            System.out.println("ERROR: No se recibieron los puntajes de las competencias");
            model.addAttribute("error", "Debe ingresar todos los puntajes de las competencias");
            model.addAttribute("estudiantes", estudianteRepository.findByEstadoPago(EstadoPago.APROBADO));
            model.addAttribute("competencias", Competencia.values());
            return "coordinador/resultados/registrar";
        }
        
        try {
            // Guardar resultado
            ResultadoSaberPro resultado = new ResultadoSaberPro(estudiante, LocalDate.parse(fechaExamen), puntajeTotal);
            resultadoRepository.save(resultado);
            System.out.println("Resultado guardado con ID: " + resultado.getId());
            
            // Guardar competencias (son 8 fijas)
            Competencia[] competenciasArray = Competencia.values();
            for (int i = 0; i < competenciasArray.length && i < puntajes.size(); i++) {
                Integer puntaje = puntajes.get(i);
                String nivel = calcularNivel(puntaje);
                
                DetalleCompetencia detalle = new DetalleCompetencia(resultado, competenciasArray[i], puntaje, nivel);
                detalleCompetenciaRepository.save(detalle);
                System.out.println("Guardado: " + competenciasArray[i].getNombre() + " = " + puntaje + " pts - " + nivel);
            }
            
            System.out.println("=== RESULTADO REGISTRADO EXITOSAMENTE ===");
            
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al guardar: " + e.getMessage());
            model.addAttribute("estudiantes", estudianteRepository.findByEstadoPago(EstadoPago.APROBADO));
            model.addAttribute("competencias", Competencia.values());
            return "coordinador/resultados/registrar";
        }
        
        return "redirect:/coordinador/resultados";
    }

    @GetMapping("/resultados/editar/{id}")
    public String mostrarFormEditarResultado(@PathVariable Long id, Model model, HttpSession session) {
        if (!validarCoordinador(session)) return "redirect:/login";
        ResultadoSaberPro resultado = resultadoRepository.findById(id).orElse(null);
        model.addAttribute("resultado", resultado);
        model.addAttribute("competencias", Competencia.values());
        return "coordinador/resultados/editar";
    }

    @PostMapping("/resultados/editar/{id}")
    public String editarResultado(@PathVariable Long id,
                                   @RequestParam Integer puntajeTotal,
                                   @RequestParam(value = "puntajes", required = false) List<Integer> puntajes,
                                   HttpSession session,
                                   Model model) {
        if (!validarCoordinador(session)) return "redirect:/login";
        
        ResultadoSaberPro resultado = resultadoRepository.findById(id).orElse(null);
        if (resultado == null) {
            return "redirect:/coordinador/resultados";
        }
        
        try {
            resultado.setPuntajeTotal(puntajeTotal);
            resultadoRepository.save(resultado);
            
            List<DetalleCompetencia> detalles = detalleCompetenciaRepository.findByResultadoId(resultado.getId());
            for (int i = 0; i < detalles.size() && i < puntajes.size(); i++) {
                DetalleCompetencia detalle = detalles.get(i);
                detalle.setPuntaje(puntajes.get(i));
                detalle.setNivel(calcularNivel(puntajes.get(i)));
                detalleCompetenciaRepository.save(detalle);
            }
            
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            model.addAttribute("error", "Error al editar: " + e.getMessage());
        }
        
        return "redirect:/coordinador/resultados";
    }

    @GetMapping("/resultados/eliminar/{id}")
    public String eliminarResultado(@PathVariable Long id, HttpSession session) {
        if (!validarCoordinador(session)) return "redirect:/login";
        resultadoRepository.deleteById(id);
        return "redirect:/coordinador/resultados";
    }

    @GetMapping("/resultados/ver/{id}")
    public String verResultado(@PathVariable Long id, Model model, HttpSession session) {
        if (!validarCoordinador(session)) return "redirect:/login";
        ResultadoSaberPro resultado = resultadoRepository.findById(id).orElse(null);
        model.addAttribute("resultado", resultado);
        return "coordinador/resultados/ver";
    }

    // ========== INFORMES ==========
    @GetMapping("/informes/total")
    public String informeTotal(Model model, HttpSession session) {
        if (!validarCoordinador(session)) return "redirect:/login";
        List<Estudiante> estudiantes = estudianteRepository.findByActivoTrue();
        model.addAttribute("estudiantes", estudiantes);
        return "coordinador/informes/informe-total";
    }

    @GetMapping("/informes/unico")
    public String buscarUnico(@RequestParam(required = false) String documento, Model model, HttpSession session) {
        if (!validarCoordinador(session)) return "redirect:/login";
        
        if (documento != null && !documento.isEmpty()) {
            Estudiante estudiante = estudianteRepository.findByNumeroDocumento(documento).orElse(null);
            if (estudiante == null) {
                model.addAttribute("error", "Estudiante no encontrado con documento: " + documento);
            } else {
                model.addAttribute("estudiante", estudiante);
                List<ResultadoSaberPro> resultados = resultadoRepository.findByEstudianteIdAndEstado(estudiante.getId(), EstadoResultado.ACTIVO);
                ResultadoSaberPro ultimoResultado = resultados.isEmpty() ? null : resultados.get(resultados.size() - 1);
                model.addAttribute("ultimoResultado", ultimoResultado);
            }
        }
        
        return "coordinador/informes/informe-unico";
    }

    @GetMapping("/informes/beneficios")
    public String informeBeneficios(Model model, HttpSession session) {
        if (!validarCoordinador(session)) return "redirect:/login";
        List<Beneficio> beneficios = beneficioRepository.findAll();
        
        List<Map<String, Object>> estudiantesConPuntaje = new ArrayList<>();
        
        for (Estudiante e : estudianteRepository.findByActivoTrue()) {
            Map<String, Object> dato = new HashMap<>();
            dato.put("id", e.getId());
            dato.put("numeroDocumento", e.getNumeroDocumento());
            dato.put("primerNombre", e.getPrimerNombre());
            dato.put("primerApellido", e.getPrimerApellido());
            dato.put("carrera", e.getCarrera());
            List<ResultadoSaberPro> resultados = resultadoRepository.findByEstudianteIdAndEstado(e.getId(), EstadoResultado.ACTIVO);
            Integer ultimoPuntaje = resultados.isEmpty() ? null : resultados.get(resultados.size() - 1).getPuntajeTotal();
            dato.put("ultimoPuntaje", ultimoPuntaje);
            estudiantesConPuntaje.add(dato);
        }
        
        model.addAttribute("beneficios", beneficios);
        model.addAttribute("estudiantes", estudiantesConPuntaje);
        return "coordinador/informes/informe-beneficios";
    }

    // ========== EXPORTAR CSV ==========
    @GetMapping("/informes/exportar-csv")
    public void exportarCSV(HttpServletResponse response, HttpSession session) throws IOException {
        if (!validarCoordinador(session)) return;
        
        List<Estudiante> estudiantes = estudianteRepository.findByActivoTrue();
        
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=informe_saber_pro.csv");
        
        PrintWriter writer = response.getWriter();
        writer.println("\uFEFFDocumento,Estudiante,Carrera,Puntaje Total");
        
        for (Estudiante e : estudiantes) {
            List<ResultadoSaberPro> resultados = resultadoRepository.findByEstudianteIdAndEstado(e.getId(), EstadoResultado.ACTIVO);
            Integer ultimoPuntaje = resultados.isEmpty() ? null : resultados.get(resultados.size() - 1).getPuntajeTotal();
            writer.println("\"" + e.getNumeroDocumento() + "\",\"" + e.getPrimerNombre() + " " + e.getPrimerApellido() + "\",\"" + 
                (e.getCarrera() != null ? e.getCarrera().getNombre() : "Sin asignar") + "\"," + (ultimoPuntaje != null ? ultimoPuntaje : 0));
        }
        writer.flush();
    }

    // ========== CARGA MASIVA ==========
    @GetMapping("/estudiantes/carga-masiva")
    public String mostrarCargaMasiva(Model model, HttpSession session) {
        if (!validarCoordinador(session)) return "redirect:/login";
        model.addAttribute("carreras", carreraRepository.findAll());
        return "coordinador/estudiantes/carga-masiva";
    }

    @PostMapping("/estudiantes/carga-masiva")
    public String cargaMasivaEstudiantes(@RequestParam("archivo") MultipartFile archivo, 
                                          HttpSession session, 
                                          Model model) {
        if (!validarCoordinador(session)) return "redirect:/login";
        
        int registrados = 0;
        int errores = 0;
        StringBuilder erroresMsg = new StringBuilder();
        
        try {
            String contenido = new String(archivo.getBytes());
            String[] lineas = contenido.split("\\r?\\n");
            
            for (int i = 1; i < lineas.length; i++) {
                String linea = lineas[i].trim();
                if (linea.isEmpty()) continue;
                
                String[] partes = linea.split(",");
                if (partes.length < 5) {
                    errores++;
                    erroresMsg.append("Línea ").append(i + 1).append(": Formato incorrecto\n");
                    continue;
                }
                
                try {
                    String documento = partes[0].trim().replace("\"", "");
                    String primerNombre = partes[1].trim().replace("\"", "");
                    String primerApellido = partes[2].trim().replace("\"", "");
                    String email = partes[3].trim().replace("\"", "");
                    Long carreraId = Long.parseLong(partes[4].trim().replace("\"", ""));
                    
                    if (usuarioRepository.findByNumeroDocumento(documento).isPresent()) {
                        errores++;
                        erroresMsg.append("Línea ").append(i + 1).append(": Documento ").append(documento).append(" ya existe\n");
                        continue;
                    }
                    
                    Carrera carrera = carreraRepository.findById(carreraId).orElse(null);
                    if (carrera == null) {
                        errores++;
                        erroresMsg.append("Línea ").append(i + 1).append(": Carrera ID ").append(carreraId).append(" no encontrada\n");
                        continue;
                    }
                    
                    Estudiante estudiante = new Estudiante(documento, primerNombre, null, primerApellido, null, email, "300000000", "123456", carrera);
                    estudianteRepository.save(estudiante);
                    registrados++;
                    
                } catch (Exception e) {
                    errores++;
                    erroresMsg.append("Línea ").append(i + 1).append(": ").append(e.getMessage()).append("\n");
                }
            }
        } catch (IOException e) {
            model.addAttribute("error", "Error al leer el archivo");
        }
        
        model.addAttribute("success", "Carga completada: " + registrados + " registrados, " + errores + " errores");
        if (errores > 0) {
            model.addAttribute("erroresDetalle", erroresMsg.toString());
        }
        model.addAttribute("carreras", carreraRepository.findAll());
        
        return "coordinador/estudiantes/carga-masiva";
    }

    // ========== ESTADÍSTICAS PARA GRÁFICOS ==========
    @GetMapping("/api/distribucion-puntajes")
    @ResponseBody
    public Map<String, Object> getDistribucionPuntajes(HttpSession session) {
        if (!validarCoordinador(session)) return new HashMap<>();
        
        List<Estudiante> estudiantes = estudianteRepository.findByActivoTrue();
        int[] rangos = {0, 0, 0, 0, 0};
        
        for (Estudiante e : estudiantes) {
            List<ResultadoSaberPro> resultados = resultadoRepository.findByEstudianteIdAndEstado(e.getId(), EstadoResultado.ACTIVO);
            if (!resultados.isEmpty()) {
                int puntaje = resultados.get(resultados.size() - 1).getPuntajeTotal();
                if (puntaje < 150) rangos[0]++;
                else if (puntaje < 180) rangos[1]++;
                else if (puntaje < 210) rangos[2]++;
                else if (puntaje < 240) rangos[3]++;
                else rangos[4]++;
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("rangos", new String[]{"<150", "150-179", "180-209", "210-239", "240+"});
        response.put("cantidades", rangos);
        return response;
    }

    @GetMapping("/api/estadisticas-carreras")
    @ResponseBody
    public Map<String, Object> getEstadisticasCarreras(HttpSession session) {
        if (!validarCoordinador(session)) return new HashMap<>();
        
        List<Carrera> carreras = carreraRepository.findAll();
        List<String> nombres = new ArrayList<>();
        List<Double> promedios = new ArrayList<>();
        
        for (Carrera c : carreras) {
            List<Estudiante> estudiantes = estudianteRepository.findByCarreraId(c.getId());
            double suma = 0;
            int count = 0;
            for (Estudiante e : estudiantes) {
                List<ResultadoSaberPro> resultados = resultadoRepository.findByEstudianteIdAndEstado(e.getId(), EstadoResultado.ACTIVO);
                if (!resultados.isEmpty()) {
                    suma += resultados.get(resultados.size() - 1).getPuntajeTotal();
                    count++;
                }
            }
            if (count > 0) {
                nombres.add(c.getNombre());
                promedios.add(suma / count);
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("carreras", nombres);
        response.put("promedios", promedios);
        return response;
    }

    // ========== MÉTODO AUXILIAR ==========
    private String calcularNivel(Integer puntaje) {
        if (puntaje == null) return "Nivel 1";
        if (puntaje >= 191) return "Nivel 4";
        if (puntaje >= 156) return "Nivel 3";
        if (puntaje >= 126) return "Nivel 2";
        return "Nivel 1";
    }
}