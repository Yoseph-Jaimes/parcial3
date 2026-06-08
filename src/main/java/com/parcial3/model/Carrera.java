package com.parcial3.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    @Column(unique = true)
    private String codigo;

    private String descripcion;

    @OneToMany(mappedBy = "carrera")
    private List<Estudiante> estudiantes;

    @OneToMany(mappedBy = "carrera")
    private List<Docente> docentes;

    @OneToMany(mappedBy = "carrera")
    private List<Director> directores;

    public Carrera() {}

    public Carrera(String nombre, String codigo, String descripcion) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public List<Estudiante> getEstudiantes() { return estudiantes; }
    public void setEstudiantes(List<Estudiante> estudiantes) { this.estudiantes = estudiantes; }
    public List<Docente> getDocentes() { return docentes; }
    public void setDocentes(List<Docente> docentes) { this.docentes = docentes; }
    public List<Director> getDirectores() { return directores; }
    public void setDirectores(List<Director> directores) { this.directores = directores; }
}