package com.parcial3.repository;

import com.parcial3.model.ReciboPago;
import com.parcial3.model.enums.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReciboPagoRepository extends JpaRepository<ReciboPago, Long> {
    List<ReciboPago> findByEstudianteId(Long estudianteId);
    List<ReciboPago> findByEstado(EstadoPago estado);
    List<ReciboPago> findByEstudianteIdAndEstado(Long estudianteId, EstadoPago estado);
}