package com.parcial3.repository;

import com.parcial3.model.LogActividad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LogActividadRepository extends JpaRepository<LogActividad, Long> {
    List<LogActividad> findByUsuarioOrderByFechaDesc(String usuario);
    List<LogActividad> findAllByOrderByFechaDesc();
}