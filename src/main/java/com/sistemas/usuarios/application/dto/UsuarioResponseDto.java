package com.sistemas.usuarios.application.dto;

public record UsuarioResponseDto(
        Long id,
        String nombre,
        String apellido,
        String celular,
        String correo,
        String rol,
        Long idRestaurante) {
}