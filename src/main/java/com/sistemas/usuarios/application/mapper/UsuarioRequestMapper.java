package com.sistemas.usuarios.application.mapper;

import com.sistemas.usuarios.application.dto.UsuarioRequestDto;
import com.sistemas.usuarios.domain.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioRequestMapper {

    public Usuario toModel(UsuarioRequestDto dto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.nombre());
        usuario.setApellido(dto.apellido());
        usuario.setDocumentoDeIdentidad(dto.documentoDeIdentidad());
        usuario.setCelular(dto.celular());
        usuario.setFechaNacimiento(dto.fechaNacimiento());
        usuario.setCorreo(dto.correo());
        usuario.setClave(dto.clave());
        return usuario;
    }
}