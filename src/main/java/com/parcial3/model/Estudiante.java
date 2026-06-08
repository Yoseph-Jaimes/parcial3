package com.parcial3.model;

import com.parcial3.model.enums.EstadoPago;
import com.parcial3.model.enums.Rol;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Estudiante extends Usuario {

    @ManyToOne
    @JoinColumn(name = "carrera_id")
    private Carrera carrera;

    @Enumerated(EnumType.STRING)
    private EstadoPago estadoPago = EstadoPago.PENDIENTE;

    private LocalDateTime fechaAprobacion;

    private Boolean activo = true;

    @OneToMany(mappedBy = "estudiante")
    private List<ResultadoSaberPro> resultados;

    @OneToMany(mappedBy = "estudiante")
    private List<ReciboPago> recibos;

    public Estudiante() {}

    public Estudiante(String numeroDocumento, String primerNombre, String segundoNombre, String primerApellido,
                      String segundoApellido, String email, String telefono, String password, Carrera carrera) {
        super(numeroDocumento, primerNombre, segundoNombre, primerApellido, segundoApellido,
              email, telefono, password, Rol.ESTUDIANTE);
        this.carrera = carrera;
        this.estadoPago = EstadoPago.PENDIENTE;
        this.activo = true;
    }

    // Getters y Setters
    public Carrera getCarrera() { return carrera; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }
    public EstadoPago getEstadoPago() { return estadoPago; }
    public void setEstadoPago(EstadoPago estadoPago) { this.estadoPago = estadoPago; }
    public LocalDateTime getFechaAprobacion() { return fechaAprobacion; }
    public void setFechaAprobacion(LocalDateTime fechaAprobacion) { this.fechaAprobacion = fechaAprobacion; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public List<ResultadoSaberPro> getResultados() { return resultados; }
    public void setResultados(List<ResultadoSaberPro> resultados) { this.resultados = resultados; }
    public List<ReciboPago> getRecibos() { return recibos; }
    public void setRecibos(List<ReciboPago> recibos) { this.recibos = recibos; }

	public void setUltimoPuntaje(Integer ultimoPuntaje) {
		// TODO Auto-generated method stub
		
	}
}