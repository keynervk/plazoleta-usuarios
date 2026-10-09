package com.sistemas.usuarios.application.handler;

import com.sistemas.usuarios.application.dto.UsuarioRequestDto;
import com.sistemas.usuarios.application.mapper.UsuarioRequestMapper;
import com.sistemas.usuarios.domain.api.IUsuarioServicePort;
import org.springframework.stereotype.Service;

@Service
public class UsuarioHandler {

    private final IUsuarioServicePort usuarioServicePort;
    private final UsuarioRequestMapper mapper;

    public UsuarioHandler(IUsuarioServicePort usuarioServicePort, UsuarioRequestMapper mapper) {
        this.usuarioServicePort = usuarioServicePort;
        this.mapper = mapper;
    }

    public void guardarPropietario(UsuarioRequestDto dto) {
        usuarioServicePort.guardarPropietario(mapper.toModel(dto));
    }

    public void guardarCliente(UsuarioRequestDto dto) {
        usuarioServicePort.guardarCliente(mapper.toModel(dto));
    }

    public void guardarEmpleado(UsuarioRequestDto dto) {
        usuarioServicePort.guardarEmpleado(mapper.toModel(dto));
    }

    public boolean esPropietario(Long id) {
        return usuarioServicePort.esPropietario(id);
    }

    public Long obtenerIdPorCorreo(String correo) {
        return usuarioServicePort.obtenerIdPorCorreo(correo);
    }
}