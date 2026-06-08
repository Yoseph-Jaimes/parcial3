package com.parcial3.model;

import com.parcial3.model.enums.EstadoResultado;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class ResultadoSaberPro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    private LocalDate fechaExamen;

    private Integer puntajeTotal;

    @Enumerated(EnumType.STRING)
    private EstadoResultado estado = EstadoResultado.ACTIVO;

    @OneToMany(mappedBy = "resultado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCompetencia> detalleCompetencias;

    public ResultadoSaberPro() {}

    public ResultadoSaberPro(Estudiante estudiante, LocalDate fechaExamen, Integer puntajeTotal) {
        this.estudiante = estudiante;
        this.fechaExamen = fechaExamen;
        this.puntajeTotal = puntajeTotal;
        this.estado = EstadoResultado.ACTIVO;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }
    public LocalDate getFechaExamen() { return fechaExamen; }
    public void setFechaExamen(LocalDate fechaExamen) { this.fechaExamen = fechaExamen; }
    public Integer getPuntajeTotal() { return puntajeTotal; }
    public void setPuntajeTotal(Integer puntajeTotal) { this.puntajeTotal = puntajeTotal; }
    public EstadoResultado getEstado() { return estado; }
    public void setEstado(EstadoResultado estado) { this.estado = estado; }
    public List<DetalleCompetencia> getDetalleCompetencias() { return detalleCompetencias; }
    public void setDetalleCompetencias(List<DetalleCompetencia> detalleCompetencias) { this.detalleCompetencias = detalleCompetencias; }
}