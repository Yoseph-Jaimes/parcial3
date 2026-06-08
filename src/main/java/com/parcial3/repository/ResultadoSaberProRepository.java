package com.parcial3.repository;

import com.parcial3.model.ResultadoSaberPro;
import com.parcial3.model.enums.EstadoResultado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResultadoSaberProRepository extends JpaRepository<ResultadoSaberPro, Long> {
    List<ResultadoSaberPro> findByEstudianteId(Long estudianteId);
    List<ResultadoSaberPro> findByEstudianteIdAndEstado(Long estudianteId, EstadoResultado estado);
    List<ResultadoSaberPro> findByEstado(EstadoResultado estado);
}