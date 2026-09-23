package com.sistemas.usuarios.infrastructure.output.jpa.adapter;

import com.sistemas.usuarios.domain.model.Rol;
import com.sistemas.usuarios.domain.spi.IRolPersistencePort;
import com.sistemas.usuarios.infrastructure.output.jpa.mapper.UsuarioEntityMapper;
import com.sistemas.usuarios.infrastructure.output.jpa.repository.IRolRepository;
import org.springframework.stereotype.Component;

@Component
public class RolJpaAdapter implements IRolPersistencePort {

    private final IRolRepository rolRepository;
    private final UsuarioEntityMapper mapper;

    public RolJpaAdapter(IRolRepository rolRepository, UsuarioEntityMapper mapper) {
        this.rolRepository = rolRepository;
        this.mapper = mapper;
    }

    @Override
    public Rol buscarPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre).map(mapper::toModel).orElse(null);
    }
}