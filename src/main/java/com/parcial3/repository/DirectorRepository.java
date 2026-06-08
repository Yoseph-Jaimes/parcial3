package com.parcial3.repository;

import com.parcial3.model.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DirectorRepository extends JpaRepository<Director, Long> {
    Optional<Director> findByNumeroDocumento(String numeroDocumento);
    List<Director> findByCarreraId(Long carreraId);
}