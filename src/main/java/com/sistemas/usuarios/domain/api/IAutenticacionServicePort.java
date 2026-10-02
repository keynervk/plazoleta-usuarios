package com.sistemas.usuarios.domain.api;

public interface IAutenticacionServicePort {

    String autenticar(String correo, String clave);
}