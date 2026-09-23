package com.sistemas.usuarios.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record UsuarioRequestDto(
        @Schema(example = "Juan") String nombre,
        @Schema(example = "Perez") String apellido,
        @Schema(example = "123456789") String documentoDeIdentidad,
        @Schema(example = "+573005698325") String celular,
        @Schema(example = "1990-05-20") LocalDate fechaNacimiento,
        @Schema(example = "juan@correo.com") String correo,
        @Schema(example = "1234") String clave) {
}