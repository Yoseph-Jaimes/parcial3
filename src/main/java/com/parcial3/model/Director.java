package com.parcial3.model;

import com.parcial3.model.enums.Rol;
import jakarta.persistence.*;

@Entity
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Director extends Usuario {

    @ManyToOne
    @JoinColumn(name = "carrera_id")
    private Carrera carrera;

    public Director() {}

    public Director(String numeroDocumento, String primerNombre, String segundoNombre, String primerApellido,
                    String segundoApellido, String email, String telefono, String password, Carrera carrera) {
        super(numeroDocumento, primerNombre, segundoNombre, primerApellido, segundoApellido,
              email, telefono, password, Rol.ADMIN);
        this.carrera = carrera;
    }

    public Carrera getCarrera() { return carrera; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }
}