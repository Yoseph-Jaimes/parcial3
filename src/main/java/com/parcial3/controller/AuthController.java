package com.parcial3.controller;

import com.parcial3.model.Usuario;
import com.parcial3.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String numeroDocumento,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        Usuario usuario = usuarioRepository.findByNumeroDocumentoAndPassword(numeroDocumento, password)
                .orElse(null);

        if (usuario == null) {
            model.addAttribute("error", "Documento o contraseña incorrectos");
            return "login";
        }

        session.setAttribute("usuario", usuario);
        session.setAttribute("rol", usuario.getRol());

        switch (usuario.getRol()) {
            case ADMIN:
                return "redirect:/admin/dashboard";
            case COORDINADOR:
                return "redirect:/coordinador/dashboard";
            case DOCENTE:
                return "redirect:/docente/dashboard";
            case ESTUDIANTE:
                return "redirect:/estudiante/dashboard";
            default:
                return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/cambiar-password")
    public String mostrarCambiarPassword() {
        return "cambiar-password";
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@RequestParam String passwordActual,
                                  @RequestParam String passwordNueva,
                                  @RequestParam String confirmPassword,
                                  HttpSession session,
                                  Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        if (!usuario.getPassword().equals(passwordActual)) {
            model.addAttribute("error", "Contraseña actual incorrecta");
            return "cambiar-password";
        }

        if (!passwordNueva.equals(confirmPassword)) {
            model.addAttribute("error", "Las nuevas contraseñas no coinciden");
            return "cambiar-password";
        }

        if (passwordNueva.length() < 4) {
            model.addAttribute("error", "La contraseña debe tener al menos 4 caracteres");
            return "cambiar-password";
        }

        usuario.setPassword(passwordNueva);
        usuarioRepository.save(usuario);

        model.addAttribute("success", "Contraseña actualizada correctamente");
        return "cambiar-password";
    }
}