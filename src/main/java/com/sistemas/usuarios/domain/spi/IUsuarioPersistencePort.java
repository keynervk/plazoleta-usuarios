package com.sistemas.usuarios.domain.spi;

import com.sistemas.usuarios.domain.model.Usuario;

public interface IUsuarioPersistencePort {

    Usuario guardarUsuario(Usuario usuario);

    boolean existePorCorreo(String correo);

    boolean existePorDocumento(String documentoDeIdentidad);
}