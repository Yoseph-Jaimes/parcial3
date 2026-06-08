package com.parcial3.repository;

import com.parcial3.model.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DocenteRepository extends JpaRepository<Docente, Long> {
    Optional<Docente> findByNumeroDocumento(String numeroDocumento);
    List<Docente> findByCarreraId(Long carreraId);
}