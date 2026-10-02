package com.sistemas.usuarios.infrastructure.output.jpa.adapter;

import com.sistemas.usuarios.domain.model.Usuario;
import com.sistemas.usuarios.domain.spi.IUsuarioPersistencePort;
import com.sistemas.usuarios.infrastructure.output.jpa.mapper.UsuarioEntityMapper;
import com.sistemas.usuarios.infrastructure.output.jpa.repository.IUsuarioRepository;
import org.springframework.stereotype.Component;

@Component
public class UsuarioJpaAdapter implements IUsuarioPersistencePort {

    private final IUsuarioRepository usuarioRepository;
    private final UsuarioEntityMapper mapper;

    public UsuarioJpaAdapter(IUsuarioRepository usuarioRepository, UsuarioEntityMapper mapper) {
        this.usuarioRepository = usuarioRepository;
        this.mapper = mapper;
    }

    @Override
    public Usuario guardarUsuario(Usuario usuario) {
        return mapper.toModel(usuarioRepository.save(mapper.toEntity(usuario)));
    }

    @Override
    public boolean existePorCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    @Override
    public boolean existePorDocumento(String documentoDeIdentidad) {
        return usuarioRepository.existsByDocumentoDeIdentidad(documentoDeIdentidad);
    }

    @Override
    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo).map(mapper::toModel).orElse(null);
    }
}