package com.sistemas.usuarios.infrastructure.output.jpa.adapter;

import com.sistemas.usuarios.domain.spi.IPasswordEncoderPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderAdapter implements IPasswordEncoderPort {

    private final PasswordEncoder passwordEncoder;

    public PasswordEncoderAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encriptar(String claveSinEncriptar) {
        return passwordEncoder.encode(claveSinEncriptar);
    }

    @Override
    public boolean coincide(String claveSinEncriptar, String claveEncriptada) {
        return passwordEncoder.matches(claveSinEncriptar, claveEncriptada);
    }
}