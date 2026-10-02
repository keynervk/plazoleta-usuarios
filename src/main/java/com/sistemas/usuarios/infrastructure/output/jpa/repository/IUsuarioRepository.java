package com.sistemas.usuarios.infrastructure.output.jpa.repository;

import com.sistemas.usuarios.infrastructure.output.jpa.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    boolean existsByCorreo(String correo);

    boolean existsByDocumentoDeIdentidad(String documentoDeIdentidad);

    Optional<UsuarioEntity> findByCorreo(String correo);
}