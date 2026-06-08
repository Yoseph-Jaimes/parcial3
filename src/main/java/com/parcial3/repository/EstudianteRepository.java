package com.parcial3.repository;

import com.parcial3.model.Estudiante;
import com.parcial3.model.enums.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    Optional<Estudiante> findByNumeroDocumento(String numeroDocumento);
    List<Estudiante> findByCarreraId(Long carreraId);
    List<Estudiante> findByEstadoPago(EstadoPago estadoPago);
    List<Estudiante> findByActivoTrue();
}