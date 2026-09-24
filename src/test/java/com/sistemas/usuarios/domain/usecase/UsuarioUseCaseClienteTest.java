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
class UsuarioUseCaseClienteTest {

    @Mock
    private IUsuarioPersistencePort usuarioPersistencePort;

    @Mock
    private IRolPersistencePort rolPersistencePort;

    @Mock
    private IPasswordEncoderPort passwordEncoderPort;

    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    private Usuario clienteValido() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Maria");
        usuario.setApellido("Lopez");
        usuario.setDocumentoDeIdentidad("555666777");
        usuario.setCelular("+573007778899");
        usuario.setCorreo("maria@correo.com");
        usuario.setClave("1234");
        return usuario;
    }

    @Test
    void guardarCliente_conDatosValidos_guardaConRolClienteYClaveEncriptada() {
        when(rolPersistencePort.buscarPorNombre("CLIENTE"))
                .thenReturn(new Rol(4L, "CLIENTE", "Cliente de la plazoleta"));
        when(passwordEncoderPort.encriptar("1234")).thenReturn("$2a$hash");

        usuarioUseCase.guardarCliente(clienteValido());

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioPersistencePort).guardarUsuario(captor.capture());
        assertEquals("CLIENTE", captor.getValue().getRol().getNombre());
        assertEquals("$2a$hash", captor.getValue().getClave());
    }

    @Test
    void guardarCliente_sinFechaDeNacimiento_guarda() {
        Usuario cliente = clienteValido();
        cliente.setFechaNacimiento(null);
        when(rolPersistencePort.buscarPorNombre("CLIENTE"))
                .thenReturn(new Rol(4L, "CLIENTE", "Cliente"));
        when(passwordEncoderPort.encriptar("1234")).thenReturn("$2a$hash");

        usuarioUseCase.guardarCliente(cliente);

        verify(usuarioPersistencePort).guardarUsuario(cliente);
    }

    @Test
    void guardarCliente_siEsMenorDeEdad_noLoRechaza() {
        Usuario cliente = clienteValido();
        cliente.setFechaNacimiento(LocalDate.now().minusYears(10));
        when(rolPersistencePort.buscarPorNombre("CLIENTE"))
                .thenReturn(new Rol(4L, "CLIENTE", "Cliente"));
        when(passwordEncoderPort.encriptar("1234")).thenReturn("$2a$hash");

        usuarioUseCase.guardarCliente(cliente);

        verify(usuarioPersistencePort).guardarUsuario(cliente);
    }

    @ParameterizedTest
    @ValueSource(strings = {"maria", "maria@", "maria@correo", "@correo.com"})
    void guardarCliente_conCorreoInvalido_lanzaValidacionException(String correo) {
        Usuario cliente = clienteValido();
        cliente.setCorreo(correo);

        assertThrows(ValidacionException.class, () -> usuarioUseCase.guardarCliente(cliente));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"+5730056983251", "300ABC1234", "++573005698"})
    void guardarCliente_conCelularInvalido_lanzaValidacionException(String celular) {
        Usuario cliente = clienteValido();
        cliente.setCelular(celular);

        assertThrows(ValidacionException.class, () -> usuarioUseCase.guardarCliente(cliente));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345abc", "123.456", "-123456"})
    void guardarCliente_conDocumentoNoNumerico_lanzaValidacionException(String documento) {
        Usuario cliente = clienteValido();
        cliente.setDocumentoDeIdentidad(documento);

        assertThrows(ValidacionException.class, () -> usuarioUseCase.guardarCliente(cliente));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @Test
    void guardarCliente_sinNombre_lanzaValidacionException() {
        Usuario cliente = clienteValido();
        cliente.setNombre(" ");

        assertThrows(ValidacionException.class, () -> usuarioUseCase.guardarCliente(cliente));
    }

    @Test
    void guardarCliente_conUsuarioNulo_lanzaValidacionException() {
        assertThrows(ValidacionException.class, () -> usuarioUseCase.guardarCliente(null));
    }

    @Test
    void guardarCliente_siElCorreoYaExiste_lanzaUsuarioYaExisteException() {
        when(usuarioPersistencePort.existePorCorreo("maria@correo.com")).thenReturn(true);

        assertThrows(UsuarioYaExisteException.class,
                () -> usuarioUseCase.guardarCliente(clienteValido()));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @Test
    void guardarCliente_siElDocumentoYaExiste_lanzaUsuarioYaExisteException() {
        when(usuarioPersistencePort.existePorDocumento("555666777")).thenReturn(true);

        assertThrows(UsuarioYaExisteException.class,
                () -> usuarioUseCase.guardarCliente(clienteValido()));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @Test
    void guardarCliente_siNoExisteElRol_lanzaValidacionException() {
        when(rolPersistencePort.buscarPorNombre("CLIENTE")).thenReturn(null);

        assertThrows(ValidacionException.class,
                () -> usuarioUseCase.guardarCliente(clienteValido()));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }
}