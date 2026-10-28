package com.sistemas.usuarios.domain.spi;

public interface IRestauranteExternoPort {

    /**
     * Devuelve el id del propietario del restaurante, o null si el restaurante no existe.
     */
    Long obtenerIdPropietario(Long idRestaurante);
}