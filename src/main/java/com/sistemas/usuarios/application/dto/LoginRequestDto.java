package com.sistemas.usuarios.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequestDto(
        @Schema(example = "juan@correo.com") String correo,
        @Schema(example = "1234") String clave) {
}