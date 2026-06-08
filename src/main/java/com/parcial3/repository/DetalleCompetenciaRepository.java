package com.parcial3.repository;

import com.parcial3.model.DetalleCompetencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DetalleCompetenciaRepository extends JpaRepository<DetalleCompetencia, Long> {
    List<DetalleCompetencia> findByResultadoId(Long resultadoId);
}