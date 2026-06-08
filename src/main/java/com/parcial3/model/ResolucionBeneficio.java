package com.parcial3.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class ResolucionBeneficio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;

    private String descripcion;

    private Boolean activo = false;

    public ResolucionBeneficio() {}

    public ResolucionBeneficio(LocalDate fecha, String descripcion, Boolean activo) {
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}