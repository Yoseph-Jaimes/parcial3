package com.parcial3.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class LogActividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String usuario;
    private String accion;
    private String detalles;
    private LocalDateTime fecha;
    private String ip;

    public LogActividad() {}

    public LogActividad(String usuario, String accion, String detalles, String ip) {
        this.usuario = usuario;
        this.accion = accion;
        this.detalles = detalles;
        this.fecha = LocalDateTime.now();
        this.ip = ip;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public String getDetalles() { return detalles; }
    public void setDetalles(String detalles) { this.detalles = detalles; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
}