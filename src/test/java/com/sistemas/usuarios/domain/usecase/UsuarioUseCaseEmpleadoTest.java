package com.sistemas.usuarios.domain.usecase;

import com.sistemas.usuarios.domain.exception.NoAutorizadoException;
import com.sistemas.usuarios.domain.exception.UsuarioYaExisteException;
import com.sistemas.usuarios.domain.exception.ValidacionException;
import com.sistemas.usuarios.domain.model.Rol;
import com.sistemas.usuarios.domain.model.Usuario;
import com.sistemas.usuarios.domain.spi.IPasswordEncoderPort;
import com.sistemas.usuarios.domain.spi.IRestauranteExternoPort;
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

    private static final String CORREO_PROPIETARIO = "juan@correo.com";
    private static final Long ID_PROPIETARIO = 5L;
    private static final Long ID_RESTAURANTE = 1L;

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

    private Usuario empleadoValido() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Carlos");
        usuario.setApellido("Ramirez");
        usuario.setDocumentoDeIdentidad("111222333");
        usuario.setCelular("+573009998877");
        usuario.setCorreo("carlos@correo.com");
        usuario.setClave("1234");
        usuario.setIdRestaurante(ID_RESTAURANTE);
        return usuario;
    }

    private Usuario propietario(Long id) {
        Usuario propietario = new Usuario();
        propietario.setId(id);
        propietario.setCorreo(CORREO_PROPIETARIO);
        return propietario;
    }

    private void elRestauranteEsDelPropietarioAutenticado() {
        when(restauranteExternoPort.obtenerIdPropietario(ID_RESTAURANTE)).thenReturn(ID_PROPIETARIO);
        when(usuarioPersistencePort.buscarPorCorreo(CORREO_PROPIETARIO)).thenReturn(propietario(ID_PROPIETARIO));
    }

    @Test
    void guardarEmpleado_conDatosValidos_guardaConRolEmpleadoRestauranteYClaveEncriptada() {
        elRestauranteEsDelPropietarioAutenticado();
        when(rolPersistencePort.buscarPorNombre("EMPLEADO"))
                .thenReturn(new Rol(3L, "EMPLEADO", "Empleado de restaurante"));
        when(passwordEncoderPort.encriptar("1234")).thenReturn("$2a$hash");

        usuarioUseCase.guardarEmpleado(empleadoValido(), CORREO_PROPIETARIO);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioPersistencePort).guardarUsuario(captor.capture());
        assertEquals("EMPLEADO", captor.getValue().getRol().getNombre());
        assertEquals("$2a$hash", captor.getValue().getClave());
        assertEquals(ID_RESTAURANTE, captor.getValue().getIdRestaurante());
    }

    @Test
    void guardarEmpleado_sinFechaDeNacimiento_guarda() {
        Usuario empleado = empleadoValido();
        empleado.setFechaNacimiento(null);
        elRestauranteEsDelPropietarioAutenticado();
        when(rolPersistencePort.buscarPorNombre("EMPLEADO"))
                .thenReturn(new Rol(3L, "EMPLEADO", "Empleado"));
        when(passwordEncoderPort.encriptar("1234")).thenReturn("$2a$hash");

        usuarioUseCase.guardarEmpleado(empleado, CORREO_PROPIETARIO);

        verify(usuarioPersistencePort).guardarUsuario(empleado);
    }

    @Test
    void guardarEmpleado_sinIdRestaurante_lanzaValidacionException() {
        Usuario empleado = empleadoValido();
        empleado.setIdRestaurante(null);

        assertThrows(ValidacionException.class, () -> usuarioUseCase.guardarEmpleado(empleado, CORREO_PROPIETARIO));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @Test
    void guardarEmpleado_siElRestauranteNoExiste_lanzaValidacionException() {
        when(restauranteExternoPort.obtenerIdPropietario(ID_RESTAURANTE)).thenReturn(null);

        assertThrows(ValidacionException.class,
                () -> usuarioUseCase.guardarEmpleado(empleadoValido(), CORREO_PROPIETARIO));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @Test
    void guardarEmpleado_siElRestauranteEsDeOtroPropietario_lanzaNoAutorizadoException() {
        when(restauranteExternoPort.obtenerIdPropietario(ID_RESTAURANTE)).thenReturn(99L);
        when(usuarioPersistencePort.buscarPorCorreo(CORREO_PROPIETARIO)).thenReturn(propietario(ID_PROPIETARIO));

        assertThrows(NoAutorizadoException.class,
                () -> usuarioUseCase.guardarEmpleado(empleadoValido(), CORREO_PROPIETARIO));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @Test
    void guardarEmpleado_siElPropietarioAutenticadoNoExiste_lanzaNoAutorizadoException() {
        when(restauranteExternoPort.obtenerIdPropietario(ID_RESTAURANTE)).thenReturn(ID_PROPIETARIO);
        when(usuarioPersistencePort.buscarPorCorreo(CORREO_PROPIETARIO)).thenReturn(null);

        assertThrows(NoAutorizadoException.class,
                () -> usuarioUseCase.guardarEmpleado(empleadoValido(), CORREO_PROPIETARIO));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"carlos", "carlos@", "@correo.com"})
    void guardarEmpleado_conCorreoInvalido_lanzaValidacionException(String correo) {
        Usuario empleado = empleadoValido();
        empleado.setCorreo(correo);

        assertThrows(ValidacionException.class, () -> usuarioUseCase.guardarEmpleado(empleado, CORREO_PROPIETARIO));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"+5730056983251", "300ABC1234"})
    void guardarEmpleado_conCelularInvalido_lanzaValidacionException(String celular) {
        Usuario empleado = empleadoValido();
        empleado.setCelular(celular);

        assertThrows(ValidacionException.class, () -> usuarioUseCase.guardarEmpleado(empleado, CORREO_PROPIETARIO));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345abc", "-123456"})
    void guardarEmpleado_conDocumentoNoNumerico_lanzaValidacionException(String documento) {
        Usuario empleado = empleadoValido();
        empleado.setDocumentoDeIdentidad(documento);

        assertThrows(ValidacionException.class, () -> usuarioUseCase.guardarEmpleado(empleado, CORREO_PROPIETARIO));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @Test
    void guardarEmpleado_conUsuarioNulo_lanzaValidacionException() {
        assertThrows(ValidacionException.class, () -> usuarioUseCase.guardarEmpleado(null, CORREO_PROPIETARIO));
    }

    @Test
    void guardarEmpleado_siElCorreoYaExiste_lanzaUsuarioYaExisteException() {
        elRestauranteEsDelPropietarioAutenticado();
        when(usuarioPersistencePort.existePorCorreo("carlos@correo.com")).thenReturn(true);

        assertThrows(UsuarioYaExisteException.class,
                () -> usuarioUseCase.guardarEmpleado(empleadoValido(), CORREO_PROPIETARIO));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @Test
    void guardarEmpleado_siElDocumentoYaExiste_lanzaUsuarioYaExisteException() {
        elRestauranteEsDelPropietarioAutenticado();
        when(usuarioPersistencePort.existePorDocumento("111222333")).thenReturn(true);

        assertThrows(UsuarioYaExisteException.class,
                () -> usuarioUseCase.guardarEmpleado(empleadoValido(), CORREO_PROPIETARIO));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }

    @Test
    void guardarEmpleado_siNoExisteElRol_lanzaValidacionException() {
        elRestauranteEsDelPropietarioAutenticado();
        when(rolPersistencePort.buscarPorNombre("EMPLEADO")).thenReturn(null);

        assertThrows(ValidacionException.class,
                () -> usuarioUseCase.guardarEmpleado(empleadoValido(), CORREO_PROPIETARIO));
        verify(usuarioPersistencePort, never()).guardarUsuario(any());
    }
}