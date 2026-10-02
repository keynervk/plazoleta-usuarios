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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseEmpleadoTest {

    @Mock
    private IUsuarioPersistencePort usuarioPersistencePort;

    @Mock
    private IRolPersistencePort rolPersistencePort;

    @Mock
    private IPasswordEncoderPort passwordEncoderPort;

    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    private Usuario empleadoValido() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Carlos");
        usuario.setApellido("Ramirez");
        usuario.setDocumentoDeIdentidad("111222333");
        usuario.setCelular("+573009998877");
        usuario.setCorreo("carlos@correo.com");
        usuario.setClave("1234");
        return usuario;
    }

    @Test
    void guardarEmpleado_conDatosValidos_guardaConRolEmpleadoYClaveEncriptada() {
        when(rolPersistencePort.buscarPorNombre("EMPLEADO"))
                .thenReturn(new Rol(3L, "EMPLEADO", "Empleado de restaurante"));
        when(passwordEncoderPort.encriptar("1234")).thenReturn("$2a$hash");

        usuarioUseCase.guardarEmpleado(empleadoValido());

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioPersistencePort).guardarUsuario(captor.capture());
        assertEquals("EMPLEADO", captor.getValue().getRol().getNombre());
        assertEquals("$2a$hash", captor.getValue().getClave());
    }

    @Test
    void guardarEmpleado_sinFechaDeNacimiento_guarda() {
        Usuario empleado = empleadoValido();
        empleado.setFechaNacimiento(null);
        when(rolPersistencePort.buscarPorNombre("EMPLEADO"))
                .thenReturn(new Rol(3L, "EMPLEADO", "Empleado"));
        when(passwordEncoderPort.encriptar("1234")).thenReturn("$2a$hash");

        usuarioUseCase.guardarEmpleado(empleado);

        verify(usuarioPersistencePort).guardarUsuario(empleado);
    }

    @ParameterizedTest
    @ValueSource(strings = {"carlos", "carlos@", "@correo.com"})
    void guardarEmpleado_conCorreoInvalido_lanzaValidacionException(String correo) {
        Usuario empleado = empleadoValido();
        empleado.setCorreo(correo);

        assertThrows(ValidacionException.class, () -> usuarioUseCase.guardarEmpleado(empleado));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"+5730056983251", "300ABC1234"})
    void guardarEmpleado_conCelularInvalido_lanzaValidacionException(String celular) {
        Usuario empleado = empleadoValido();
        empleado.setCelular(celular);

        assertThrows(ValidacionException.class, () -> usuarioUseCase.guardarEmpleado(empleado));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345abc", "-123456"})
    void guardarEmpleado_conDocumentoNoNumerico_lanzaValidacionException(String documento) {
        Usuario empleado = empleadoValido();
        empleado.setDocumentoDeIdentidad(documento);

        assertThrows(ValidacionException.class, () -> usuarioUseCase.guardarEmpleado(empleado));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @Test
    void guardarEmpleado_conUsuarioNulo_lanzaValidacionException() {
        assertThrows(ValidacionException.class, () -> usuarioUseCase.guardarEmpleado(null));
    }

    @Test
    void guardarEmpleado_siElCorreoYaExiste_lanzaUsuarioYaExisteException() {
        when(usuarioPersistencePort.existePorCorreo("carlos@correo.com")).thenReturn(true);

        assertThrows(UsuarioYaExisteException.class,
                () -> usuarioUseCase.guardarEmpleado(empleadoValido()));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @Test
    void guardarEmpleado_siElDocumentoYaExiste_lanzaUsuarioYaExisteException() {
        when(usuarioPersistencePort.existePorDocumento("111222333")).thenReturn(true);

        assertThrows(UsuarioYaExisteException.class,
                () -> usuarioUseCase.guardarEmpleado(empleadoValido()));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @Test
    void guardarEmpleado_siNoExisteElRol_lanzaValidacionException() {
        when(rolPersistencePort.buscarPorNombre("EMPLEADO")).thenReturn(null);

        assertThrows(ValidacionException.class,
                () -> usuarioUseCase.guardarEmpleado(empleadoValido()));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }
}