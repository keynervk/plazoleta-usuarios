package com.sistemas.usuarios.domain.usecase;

import com.sistemas.usuarios.domain.api.IAutenticacionServicePort;
import com.sistemas.usuarios.domain.exception.CredencialesInvalidasException;
import com.sistemas.usuarios.domain.model.Usuario;
import com.sistemas.usuarios.domain.spi.IPasswordEncoderPort;
import com.sistemas.usuarios.domain.spi.ITokenPort;
import com.sistemas.usuarios.domain.spi.IUsuarioPersistencePort;

public class AutenticacionUseCase implements IAutenticacionServicePort {

    private final IUsuarioPersistencePort usuarioPersistencePort;
    private final IPasswordEncoderPort passwordEncoderPort;
    private final ITokenPort tokenPort;

    public AutenticacionUseCase(IUsuarioPersistencePort usuarioPersistencePort,
                                IPasswordEncoderPort passwordEncoderPort,
                                ITokenPort tokenPort) {
        this.usuarioPersistencePort = usuarioPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenPort = tokenPort;
    }

    @Override
    public String autenticar(String correo, String clave) {
        if (correo == null || correo.isBlank() || clave == null || clave.isBlank()) {
            throw new CredencialesInvalidasException("Correo y clave son obligatorios");
        }

        Usuario usuario = usuarioPersistencePort.buscarPorCorreo(correo);
        if (usuario == null) {
            throw new CredencialesInvalidasException("Correo o clave incorrectos");
        }

        if (!passwordEncoderPort.coincide(clave, usuario.getClave())) {
            throw new CredencialesInvalidasException("Correo o clave incorrectos");
        }

        return tokenPort.generarToken(usuario);
    }
}