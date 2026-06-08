package com.parcial3.model;

import com.parcial3.model.enums.Competencia;
import jakarta.persistence.*;

@Entity
public class DetalleCompetencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "resultado_id", nullable = false)
    private ResultadoSaberPro resultado;

    @Enumerated(EnumType.STRING)
    private Competencia competencia;

    private Integer puntaje;

    private String nivel;

    public DetalleCompetencia() {}

    public DetalleCompetencia(ResultadoSaberPro resultado, Competencia competencia, Integer puntaje, String nivel) {
        this.resultado = resultado;
        this.competencia = competencia;
        this.puntaje = puntaje;
        this.nivel = nivel;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ResultadoSaberPro getResultado() { return resultado; }
    public void setResultado(ResultadoSaberPro resultado) { this.resultado = resultado; }
    public Competencia getCompetencia() { return competencia; }
    public void setCompetencia(Competencia competencia) { this.competencia = competencia; }
    public Integer getPuntaje() { return puntaje; }
    public void setPuntaje(Integer puntaje) { this.puntaje = puntaje; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
}