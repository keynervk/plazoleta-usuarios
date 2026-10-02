package com.sistemas.usuarios.application.handler;

import com.sistemas.usuarios.application.dto.LoginRequestDto;
import com.sistemas.usuarios.application.dto.LoginResponseDto;
import com.sistemas.usuarios.domain.api.IAutenticacionServicePort;
import org.springframework.stereotype.Service;

@Service
public class AutenticacionHandler {

    private final IAutenticacionServicePort autenticacionServicePort;

    public AutenticacionHandler(IAutenticacionServicePort autenticacionServicePort) {
        this.autenticacionServicePort = autenticacionServicePort;
    }

    public LoginResponseDto login(LoginRequestDto dto) {
        String token = autenticacionServicePort.autenticar(dto.correo(), dto.clave());
        return new LoginResponseDto(token);
    }
}