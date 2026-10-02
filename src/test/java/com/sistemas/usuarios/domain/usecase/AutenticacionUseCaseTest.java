package com.sistemas.usuarios.domain.usecase;

import com.sistemas.usuarios.domain.exception.CredencialesInvalidasException;
import com.sistemas.usuarios.domain.model.Rol;
import com.sistemas.usuarios.domain.model.Usuario;
import com.sistemas.usuarios.domain.spi.IPasswordEncoderPort;
import com.sistemas.usuarios.domain.spi.ITokenPort;
import com.sistemas.usuarios.domain.spi.IUsuarioPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutenticacionUseCaseTest {

    @Mock
    private IUsuarioPersistencePort usuarioPersistencePort;

    @Mock
    private IPasswordEncoderPort passwordEncoderPort;

    @Mock
    private ITokenPort tokenPort;

    @InjectMocks
    private AutenticacionUseCase autenticacionUseCase;

    private Usuario usuarioGuardado() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setCorreo("juan@correo.com");
        usuario.setClave("$2a$10$hashDeMentira");
        usuario.setRol(new Rol(2L, "PROPIETARIO", "Propietario"));
        return usuario;
    }

    @Test
    void autenticar_conCredencialesCorrectas_devuelveElToken() {
        Usuario usuario = usuarioGuardado();
        when(usuarioPersistencePort.buscarPorCorreo("juan@correo.com")).thenReturn(usuario);
        when(passwordEncoderPort.coincide("1234", usuario.getClave())).thenReturn(true);
        when(tokenPort.generarToken(usuario)).thenReturn("token-de-prueba");

        String token = autenticacionUseCase.autenticar("juan@correo.com", "1234");

        assertEquals("token-de-prueba", token);
        verify(tokenPort).generarToken(usuario);
    }

    @Test
    void autenticar_siElCorreoNoExiste_lanzaCredencialesInvalidasException() {
        when(usuarioPersistencePort.buscarPorCorreo("noexiste@correo.com")).thenReturn(null);

        assertThrows(CredencialesInvalidasException.class,
                () -> autenticacionUseCase.autenticar("noexiste@correo.com", "1234"));
    }

    @Test
    void autenticar_conClaveIncorrecta_lanzaCredencialesInvalidasException() {
        Usuario usuario = usuarioGuardado();
        when(usuarioPersistencePort.buscarPorCorreo("juan@correo.com")).thenReturn(usuario);
        when(passwordEncoderPort.coincide("claveMala", usuario.getClave())).thenReturn(false);

        assertThrows(CredencialesInvalidasException.class,
                () -> autenticacionUseCase.autenticar("juan@correo.com", "claveMala"));
    }

    @Test
    void autenticar_conCorreoVacio_lanzaCredencialesInvalidasException() {
        assertThrows(CredencialesInvalidasException.class,
                () -> autenticacionUseCase.autenticar(" ", "1234"));
    }

    @Test
    void autenticar_conClaveVacia_lanzaCredencialesInvalidasException() {
        assertThrows(CredencialesInvalidasException.class,
                () -> autenticacionUseCase.autenticar("juan@correo.com", " "));
    }

    @Test
    void autenticar_conCorreoNulo_lanzaCredencialesInvalidasException() {
        assertThrows(CredencialesInvalidasException.class,
                () -> autenticacionUseCase.autenticar(null, "1234"));
    }
}