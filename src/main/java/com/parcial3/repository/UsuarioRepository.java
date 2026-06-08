package com.parcial3.repository;

import com.parcial3.model.Usuario;
import com.parcial3.model.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNumeroDocumento(String numeroDocumento);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByNumeroDocumentoAndPassword(String numeroDocumento, String password);
    List<Usuario> findByRol(Rol rol);
}