package com.parcial3.repository;

import com.parcial3.model.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CarreraRepository extends JpaRepository<Carrera, Long> {
    Optional<Carrera> findByNombre(String nombre);
    Optional<Carrera> findByCodigo(String codigo);
}