package com.sistemas.usuarios.domain.api;

import com.sistemas.usuarios.domain.model.Usuario;

public interface IUsuarioServicePort {

    void guardarPropietario(Usuario usuario);

    void guardarCliente(Usuario usuario);

    void guardarEmpleado(Usuario usuario, String correoPropietario);

    boolean esPropietario(Long idUsuario);

    Long obtenerIdPorCorreo(String correo);

    Usuario buscarPorId(Long id);
}