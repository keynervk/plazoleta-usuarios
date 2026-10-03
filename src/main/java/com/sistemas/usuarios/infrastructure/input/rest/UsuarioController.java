package com.sistemas.usuarios.infrastructure.input.rest;

import com.sistemas.usuarios.application.dto.UsuarioRequestDto;
import com.sistemas.usuarios.application.handler.UsuarioHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Gestion de cuentas de usuario")
public class UsuarioController {

    private final UsuarioHandler usuarioHandler;

    public UsuarioController(UsuarioHandler usuarioHandler) {
        this.usuarioHandler = usuarioHandler;
    }

    @Operation(summary = "Crear una cuenta de propietario")
    @ApiResponse(responseCode = "201", description = "Propietario creado")
    @ApiResponse(responseCode = "400", description = "Datos invalidos")
    @ApiResponse(responseCode = "409", description = "Correo o documento ya registrado")
    @PostMapping("/propietario")
    public ResponseEntity<Void> guardarPropietario(@RequestBody UsuarioRequestDto dto) {
        usuarioHandler.guardarPropietario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Crear una cuenta de cliente")
    @ApiResponse(responseCode = "201", description = "Cliente creado")
    @ApiResponse(responseCode = "400", description = "Datos invalidos")
    @ApiResponse(responseCode = "409", description = "Correo o documento ya registrado")
    @PostMapping("/cliente")
    public ResponseEntity<Void> guardarCliente(@RequestBody UsuarioRequestDto dto) {
        usuarioHandler.guardarCliente(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Crear una cuenta de empleado (requiere rol PROPIETARIO)")
    @ApiResponse(responseCode = "201", description = "Empleado creado")
    @ApiResponse(responseCode = "400", description = "Datos invalidos")
    @ApiResponse(responseCode = "409", description = "Correo o documento ya registrado")
    @PostMapping("/empleado")
    public ResponseEntity<Void> guardarEmpleado(@RequestBody UsuarioRequestDto dto) {
        usuarioHandler.guardarEmpleado(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Verificar si un usuario tiene rol PROPIETARIO (uso interno entre microservicios)")
    @ApiResponse(responseCode = "200", description = "true si es propietario, false si no")
    @GetMapping("/{id}/es-propietario")
    public ResponseEntity<Boolean> esPropietario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioHandler.esPropietario(id));
    }
}