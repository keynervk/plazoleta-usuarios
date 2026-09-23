package com.sistemas.usuarios.infrastructure.exceptionhandler;

import com.sistemas.usuarios.domain.exception.UsuarioYaExisteException;
import com.sistemas.usuarios.domain.exception.ValidacionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ErrorResponse(int status, String mensaje) {
    }

    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(ValidacionException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, ex.getMessage()));
    }

    @ExceptionHandler(UsuarioYaExisteException.class)
    public ResponseEntity<ErrorResponse> handleYaExiste(UsuarioYaExisteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(409, ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonInvalido(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, "El cuerpo de la peticion es invalido (revisa el formato de fecha yyyy-MM-dd)"));
    }
}