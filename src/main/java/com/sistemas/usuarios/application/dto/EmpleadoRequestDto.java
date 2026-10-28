package com.sistemas.usuarios.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record EmpleadoRequestDto(
        @Schema(example = "Carlos") String nombre,
        @Schema(example = "Ramirez") String apellido,
        @Schema(example = "111222333") String documentoDeIdentidad,
        @Schema(example = "+573009998877") String celular,
        @Schema(example = "1995-03-10") LocalDate fechaNacimiento,
        @Schema(example = "carlos@correo.com") String correo,
        @Schema(example = "1234") String clave,
        @Schema(example = "1", description = "Restaurante (del propietario autenticado) al que pertenece el empleado")
        Long idRestaurante) {
}