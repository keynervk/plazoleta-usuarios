package com.sistemas.usuarios.application.mapper;

import com.sistemas.usuarios.application.dto.EmpleadoRequestDto;
import com.sistemas.usuarios.application.dto.UsuarioRequestDto;
import com.sistemas.usuarios.application.dto.UsuarioResponseDto;
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

    public Usuario toModel(EmpleadoRequestDto dto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.nombre());
        usuario.setApellido(dto.apellido());
        usuario.setDocumentoDeIdentidad(dto.documentoDeIdentidad());
        usuario.setCelular(dto.celular());
        usuario.setFechaNacimiento(dto.fechaNacimiento());
        usuario.setCorreo(dto.correo());
        usuario.setClave(dto.clave());
        usuario.setIdRestaurante(dto.idRestaurante());
        return usuario;
    }

    public UsuarioResponseDto toDto(Usuario usuario) {
        String rol = usuario.getRol() != null ? usuario.getRol().getNombre() : null;
        return new UsuarioResponseDto(usuario.getId(), usuario.getNombre(), usuario.getApellido(),
                usuario.getCelular(), usuario.getCorreo(), rol, usuario.getIdRestaurante());
    }
}