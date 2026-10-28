package com.sistemas.usuarios.infrastructure.output.jpa.mapper;

import com.sistemas.usuarios.domain.model.Rol;
import com.sistemas.usuarios.domain.model.Usuario;
import com.sistemas.usuarios.infrastructure.output.jpa.entity.RolEntity;
import com.sistemas.usuarios.infrastructure.output.jpa.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioEntityMapper {

    public UsuarioEntity toEntity(Usuario usuario) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(usuario.getId());
        entity.setNombre(usuario.getNombre());
        entity.setApellido(usuario.getApellido());
        entity.setDocumentoDeIdentidad(usuario.getDocumentoDeIdentidad());
        entity.setCelular(usuario.getCelular());
        entity.setFechaNacimiento(usuario.getFechaNacimiento());
        entity.setCorreo(usuario.getCorreo());
        entity.setClave(usuario.getClave());
        entity.setRol(toEntity(usuario.getRol()));
        entity.setIdRestaurante(usuario.getIdRestaurante());
        return entity;
    }

    public Usuario toModel(UsuarioEntity entity) {
        Usuario usuario = new Usuario(
                entity.getId(),
                entity.getNombre(),
                entity.getApellido(),
                entity.getDocumentoDeIdentidad(),
                entity.getCelular(),
                entity.getFechaNacimiento(),
                entity.getCorreo(),
                entity.getClave(),
                toModel(entity.getRol()));
        usuario.setIdRestaurante(entity.getIdRestaurante());
        return usuario;
    }

    public RolEntity toEntity(Rol rol) {
        if (rol == null) {
            return null;
        }
        return new RolEntity(rol.getId(), rol.getNombre(), rol.getDescripcion());
    }

    public Rol toModel(RolEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Rol(entity.getId(), entity.getNombre(), entity.getDescripcion());
    }
}