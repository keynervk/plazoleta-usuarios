package com.sistemas.usuarios.domain.spi;

public interface IPasswordEncoderPort {

    String encriptar(String claveSinEncriptar);

    boolean coincide(String claveSinEncriptar, String claveEncriptada);
}