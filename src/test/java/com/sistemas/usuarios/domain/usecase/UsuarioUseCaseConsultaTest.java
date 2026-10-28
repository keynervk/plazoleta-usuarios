package com.sistemas.usuarios.domain.usecase;

import com.sistemas.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.sistemas.usuarios.domain.exception.ValidacionException;
import com.sistemas.usuarios.domain.model.Usuario;
import com.sistemas.usuarios.domain.spi.IPasswordEncoderPort;
import com.sistemas.usuarios.domain.spi.IRestauranteExternoPort;
import com.sistemas.usuarios.domain.spi.IRolPersistencePort;
import com.sistemas.usuarios.domain.spi.IUsuarioPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseConsultaTest {

    @Mock
    private IUsuarioPersistencePort usuarioPersistencePort;

    @Mock
    private IRolPersistencePort rolPersistencePort;

    @Mock
    private IPasswordEncoderPort passwordEncoderPort;

    @Mock
    private IRestauranteExternoPort restauranteExternoPort;

    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    @Test
    void buscarPorId_siExiste_loDevuelve() {
        Usuario esperado = new Usuario();
        esperado.setId(7L);
        esperado.setIdRestaurante(1L);
        when(usuarioPersistencePort.buscarPorId(7L)).thenReturn(esperado);

        assertEquals(esperado, usuarioUseCase.buscarPorId(7L));
    }

    @Test
    void buscarPorId_siNoExiste_lanzaUsuarioNoEncontradoException() {
        when(usuarioPersistencePort.buscarPorId(99L)).thenReturn(null);

        assertThrows(UsuarioNoEncontradoException.class, () -> usuarioUseCase.buscarPorId(99L));
    }

    @Test
    void buscarPorId_sinId_lanzaValidacionException() {
        assertThrows(ValidacionException.class, () -> usuarioUseCase.buscarPorId(null));
    }
}