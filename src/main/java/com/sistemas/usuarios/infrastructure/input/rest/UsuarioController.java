package com.sistemas.usuarios.infrastructure.input.rest;

import com.sistemas.usuarios.application.dto.EmpleadoRequestDto;
import com.sistemas.usuarios.application.dto.UsuarioRequestDto;
import com.sistemas.usuarios.application.dto.UsuarioResponseDto;
import com.sistemas.usuarios.application.handler.UsuarioHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

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

    @Operation(summary = "Crear una cuenta de empleado para un restaurante propio (requiere rol PROPIETARIO)")
    @ApiResponse(responseCode = "201", description = "Empleado creado")
    @ApiResponse(responseCode = "400", description = "Datos invalidos o restaurante inexistente")
    @ApiResponse(responseCode = "403", description = "El restaurante no pertenece al propietario autenticado")
    @ApiResponse(responseCode = "409", description = "Correo o documento ya registrado")
    @PostMapping("/empleado")
    public ResponseEntity<Void> guardarEmpleado(@RequestBody EmpleadoRequestDto dto) {
        String correoPropietario = SecurityContextHolder.getContext().getAuthentication().getName();
        usuarioHandler.guardarEmpleado(dto, correoPropietario);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Verificar si un usuario tiene rol PROPIETARIO (uso interno entre microservicios)")
    @ApiResponse(responseCode = "200", description = "true si es propietario, false si no")
    @GetMapping("/{id}/es-propietario")
    public ResponseEntity<Boolean> esPropietario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioHandler.esPropietario(id));
    }

    @Operation(summary = "Obtener el id de un usuario a partir de su correo (uso interno entre microservicios)")
@ApiResponse(responseCode = "200", description = "Id del usuario, o null si no existe")
@GetMapping("/id-por-correo")
public ResponseEntity<Long> obtenerIdPorCorreo(@RequestParam String correo) {
    return ResponseEntity.ok(usuarioHandler.obtenerIdPorCorreo(correo));
}

    @Operation(summary = "Consultar datos basicos de un usuario: celular, rol y restaurante del empleado "
            + "(uso entre microservicios, requiere rol ADMINISTRADOR, PROPIETARIO o EMPLEADO)")
    @ApiResponse(responseCode = "200", description = "Datos del usuario")
    @ApiResponse(responseCode = "404", description = "El usuario no existe")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioHandler.buscarPorId(id));
    }
}