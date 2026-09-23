package com.sistemas.usuarios.domain.usecase;

import com.sistemas.usuarios.domain.exception.UsuarioYaExisteException;
import com.sistemas.usuarios.domain.exception.ValidacionException;
import com.sistemas.usuarios.domain.model.Rol;
import com.sistemas.usuarios.domain.model.Usuario;
import com.sistemas.usuarios.domain.spi.IPasswordEncoderPort;
import com.sistemas.usuarios.domain.spi.IRolPersistencePort;
import com.sistemas.usuarios.domain.spi.IUsuarioPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseTest {

    @Mock
    private IUsuarioPersistencePort usuarioPersistencePort;

    @Mock
    private IRolPersistencePort rolPersistencePort;

    @Mock
    private IPasswordEncoderPort passwordEncoderPort;

    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    private Usuario usuarioValido() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");
        usuario.setDocumentoDeIdentidad("123456789");
        usuario.setCelular("+573005698325");
        usuario.setFechaNacimiento(LocalDate.now().minusYears(30));
        usuario.setCorreo("juan@correo.com");
        usuario.setClave("1234");
        return usuario;
    }

    // ---------- Caso feliz ----------

    @Test
    void guardarPropietario_conDatosValidos_guardaConRolYClaveEncriptada() {
        Rol rolPropietario = new Rol(2L, "PROPIETARIO", "Propietario de restaurante");
        when(rolPersistencePort.buscarPorNombre("PROPIETARIO")).thenReturn(rolPropietario);
        when(passwordEncoderPort.encriptar("1234")).thenReturn("$2a$hash");

        usuarioUseCase.guardarPropietario(usuarioValido());

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioPersistencePort).guardarUsuario(captor.capture());

        Usuario guardado = captor.getValue();
        assertEquals("PROPIETARIO", guardado.getRol().getNombre());
        assertEquals("$2a$hash", guardado.getClave());
    }

    // ---------- Validaciones de formato ----------

    @ParameterizedTest
    @ValueSource(strings = { "juan", "juan@", "juan@correo", "@correo.com", "juan correo@x.com" })
    void guardarPropietario_conCorreoInvalido_lanzaValidacionException(String correo) {
        Usuario usuario = usuarioValido();
        usuario.setCorreo(correo);

        assertThrows(ValidacionException.class, () -> usuarioUseCase.guardarPropietario(usuario));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @ParameterizedTest
    @ValueSource(strings = { "+5730056983251", "300ABC1234", "+57-300-569", "++573005698" })
    void guardarPropietario_conCelularInvalido_lanzaValidacionException(String celular) {
        Usuario usuario = usuarioValido();
        usuario.setCelular(celular);

        assertThrows(ValidacionException.class, () -> usuarioUseCase.guardarPropietario(usuario));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @ParameterizedTest
    @ValueSource(strings = { "12345abc", "123.456", "-123456", "123 456" })
    void guardarPropietario_conDocumentoNoNumerico_lanzaValidacionException(String documento) {
        Usuario usuario = usuarioValido();
        usuario.setDocumentoDeIdentidad(documento);

        assertThrows(ValidacionException.class, () -> usuarioUseCase.guardarPropietario(usuario));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @Test
    void guardarPropietario_siEsMenorDeEdad_lanzaValidacionException() {
        Usuario usuario = usuarioValido();
        usuario.setFechaNacimiento(LocalDate.now().minusYears(17));

        assertThrows(ValidacionException.class, () -> usuarioUseCase.guardarPropietario(usuario));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @Test
    void guardarPropietario_siTieneExactamente18Anios_guarda() {
        Usuario usuario = usuarioValido();
        usuario.setFechaNacimiento(LocalDate.now().minusYears(18));
        when(rolPersistencePort.buscarPorNombre("PROPIETARIO"))
                .thenReturn(new Rol(2L, "PROPIETARIO", "Propietario"));
        when(passwordEncoderPort.encriptar("1234")).thenReturn("$2a$hash");

        usuarioUseCase.guardarPropietario(usuario);

        verify(usuarioPersistencePort).guardarUsuario(usuario);
    }

    // ---------- Campos obligatorios ----------

    @Test
    void guardarPropietario_sinNombre_lanzaValidacionException() {
        Usuario usuario = usuarioValido();
        usuario.setNombre(" ");

        assertThrows(ValidacionException.class, () -> usuarioUseCase.guardarPropietario(usuario));
    }

    @Test
    void guardarPropietario_sinFechaNacimiento_lanzaValidacionException() {
        Usuario usuario = usuarioValido();
        usuario.setFechaNacimiento(null);

        assertThrows(ValidacionException.class, () -> usuarioUseCase.guardarPropietario(usuario));
    }

    @Test
    void guardarPropietario_sinClave_lanzaValidacionException() {
        Usuario usuario = usuarioValido();
        usuario.setClave(null);

        assertThrows(ValidacionException.class, () -> usuarioUseCase.guardarPropietario(usuario));
    }

    @Test
    void guardarPropietario_conUsuarioNulo_lanzaValidacionException() {
        assertThrows(ValidacionException.class, () -> usuarioUseCase.guardarPropietario(null));
    }

    // ---------- Duplicados ----------

    @Test
    void guardarPropietario_siElCorreoYaExiste_lanzaUsuarioYaExisteException() {
        when(usuarioPersistencePort.existePorCorreo("juan@correo.com")).thenReturn(true);

        assertThrows(UsuarioYaExisteException.class,
                () -> usuarioUseCase.guardarPropietario(usuarioValido()));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @Test
    void guardarPropietario_siElDocumentoYaExiste_lanzaUsuarioYaExisteException() {
        when(usuarioPersistencePort.existePorDocumento("123456789")).thenReturn(true);

        assertThrows(UsuarioYaExisteException.class,
                () -> usuarioUseCase.guardarPropietario(usuarioValido()));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    // ---------- Rol ----------

    @Test
    void guardarPropietario_siNoExisteElRol_lanzaValidacionException() {
        when(rolPersistencePort.buscarPorNombre("PROPIETARIO")).thenReturn(null);

        assertThrows(ValidacionException.class,
                () -> usuarioUseCase.guardarPropietario(usuarioValido()));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }
}