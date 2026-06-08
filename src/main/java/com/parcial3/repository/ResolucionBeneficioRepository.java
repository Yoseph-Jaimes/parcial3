package com.parcial3.repository;

import com.parcial3.model.ResolucionBeneficio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ResolucionBeneficioRepository extends JpaRepository<ResolucionBeneficio, Long> {
    Optional<ResolucionBeneficio> findByActivoTrue();
    List<ResolucionBeneficio> findAllByOrderByFechaDesc();
}