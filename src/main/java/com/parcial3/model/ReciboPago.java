package com.parcial3.model;

import com.parcial3.model.enums.EstadoPago;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ReciboPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    private String nombreArchivo;
    private String rutaArchivo;
    private LocalDateTime fechaCarga;

    @Enumerated(EnumType.STRING)
    private EstadoPago estado = EstadoPago.PENDIENTE;

    private LocalDateTime fechaAprobacion;
    private String observacion;

    // Nuevos campos para guardar el archivo en la BD
    @Lob
    private byte[] archivoData;

    private String archivoTipo;

    public ReciboPago() {}

    public ReciboPago(Estudiante estudiante, String nombreArchivo, String rutaArchivo, LocalDateTime fechaCarga) {
        this.estudiante = estudiante;
        this.nombreArchivo = nombreArchivo;
        this.rutaArchivo = rutaArchivo;
        this.fechaCarga = fechaCarga;
        this.estado = EstadoPago.PENDIENTE;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }

    public LocalDateTime getFechaCarga() { return fechaCarga; }
    public void setFechaCarga(LocalDateTime fechaCarga) { this.fechaCarga = fechaCarga; }

    public EstadoPago getEstado() { return estado; }
    public void setEstado(EstadoPago estado) { this.estado = estado; }

    public LocalDateTime getFechaAprobacion() { return fechaAprobacion; }
    public void setFechaAprobacion(LocalDateTime fechaAprobacion) { this.fechaAprobacion = fechaAprobacion; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public byte[] getArchivoData() { return archivoData; }
    public void setArchivoData(byte[] archivoData) { this.archivoData = archivoData; }

    public String getArchivoTipo() { return archivoTipo; }
    public void setArchivoTipo(String archivoTipo) { this.archivoTipo = archivoTipo; }
}