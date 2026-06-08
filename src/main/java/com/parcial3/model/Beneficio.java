package com.parcial3.model;

import jakarta.persistence.*;

@Entity
public class Beneficio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    private Integer puntajeMinimo;

    private String descripcion;

    public Beneficio() {}

    public Beneficio(String nombre, Integer puntajeMinimo, String descripcion) {
        this.nombre = nombre;
        this.puntajeMinimo = puntajeMinimo;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getPuntajeMinimo() { return puntajeMinimo; }
    public void setPuntajeMinimo(Integer puntajeMinimo) { this.puntajeMinimo = puntajeMinimo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}