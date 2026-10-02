package com.sistemas.usuarios.infrastructure.input.rest;

import com.sistemas.usuarios.application.dto.LoginRequestDto;
import com.sistemas.usuarios.application.dto.LoginResponseDto;
import com.sistemas.usuarios.application.handler.AutenticacionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Autenticacion", description = "Login y generacion de token")
public class AutenticacionController {

    private final AutenticacionHandler autenticacionHandler;

    public AutenticacionController(AutenticacionHandler autenticacionHandler) {
        this.autenticacionHandler = autenticacionHandler;
    }

    @Operation(summary = "Iniciar sesion con correo y clave")
    @ApiResponse(responseCode = "200", description = "Login exitoso, devuelve el token")
    @ApiResponse(responseCode = "401", description = "Correo o clave incorrectos")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(autenticacionHandler.login(dto));
    }
}