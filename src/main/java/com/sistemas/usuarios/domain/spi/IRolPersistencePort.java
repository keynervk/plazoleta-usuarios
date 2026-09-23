package com.sistemas.usuarios.domain.spi;

import com.sistemas.usuarios.domain.model.Rol;

public interface IRolPersistencePort {

    Rol buscarPorNombre(String nombre);
}