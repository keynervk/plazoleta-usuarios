package com.sistemas.usuarios.domain.spi;

import com.sistemas.usuarios.domain.model.Usuario;

public interface ITokenPort {

    String generarToken(Usuario usuario);
}