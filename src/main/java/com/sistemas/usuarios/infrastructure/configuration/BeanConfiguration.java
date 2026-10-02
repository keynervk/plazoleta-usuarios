package com.sistemas.usuarios.infrastructure.configuration;

import com.sistemas.usuarios.domain.api.IAutenticacionServicePort;
import com.sistemas.usuarios.domain.api.IUsuarioServicePort;
import com.sistemas.usuarios.domain.spi.IPasswordEncoderPort;
import com.sistemas.usuarios.domain.spi.IRolPersistencePort;
import com.sistemas.usuarios.domain.spi.ITokenPort;
import com.sistemas.usuarios.domain.spi.IUsuarioPersistencePort;
import com.sistemas.usuarios.domain.usecase.AutenticacionUseCase;
import com.sistemas.usuarios.domain.usecase.UsuarioUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public IUsuarioServicePort usuarioServicePort(IUsuarioPersistencePort usuarioPersistencePort,
                                                  IRolPersistencePort rolPersistencePort,
                                                  IPasswordEncoderPort passwordEncoderPort) {
        return new UsuarioUseCase(usuarioPersistencePort, rolPersistencePort, passwordEncoderPort);
    }

    @Bean
    public IAutenticacionServicePort autenticacionServicePort(IUsuarioPersistencePort usuarioPersistencePort,
                                                               IPasswordEncoderPort passwordEncoderPort,
                                                               ITokenPort tokenPort) {
        return new AutenticacionUseCase(usuarioPersistencePort, passwordEncoderPort, tokenPort);
    }
}