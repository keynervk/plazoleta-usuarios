package com.sistemas.usuarios.domain.usecase;

import com.sistemas.usuarios.domain.api.IUsuarioServicePort;
import com.sistemas.usuarios.domain.exception.NoAutorizadoException;
import com.sistemas.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.sistemas.usuarios.domain.exception.UsuarioYaExisteException;
import com.sistemas.usuarios.domain.exception.ValidacionException;
import com.sistemas.usuarios.domain.model.Rol;
import com.sistemas.usuarios.domain.model.Usuario;
import com.sistemas.usuarios.domain.spi.IPasswordEncoderPort;
import com.sistemas.usuarios.domain.spi.IRestauranteExternoPort;
import com.sistemas.usuarios.domain.spi.IRolPersistencePort;
import com.sistemas.usuarios.domain.spi.IUsuarioPersistencePort;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

public class UsuarioUseCase implements IUsuarioServicePort {

    private static final String ROL_PROPIETARIO = "PROPIETARIO";
    private static final String ROL_CLIENTE = "CLIENTE";
    private static final String ROL_EMPLEADO = "EMPLEADO";
    private static final int EDAD_MINIMA = 18;

    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PATRON_CELULAR = Pattern.compile("^\\+?\\d{1,12}$");
    private static final Pattern PATRON_DOCUMENTO = Pattern.compile("^\\d+$");

    private final IUsuarioPersistencePort usuarioPersistencePort;
    private final IRolPersistencePort rolPersistencePort;
    private final IPasswordEncoderPort passwordEncoderPort;
    private final IRestauranteExternoPort restauranteExternoPort;

    public UsuarioUseCase(IUsuarioPersistencePort usuarioPersistencePort,
                          IRolPersistencePort rolPersistencePort,
                          IPasswordEncoderPort passwordEncoderPort,
                          IRestauranteExternoPort restauranteExternoPort) {
        this.usuarioPersistencePort = usuarioPersistencePort;
        this.rolPersistencePort = rolPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.restauranteExternoPort = restauranteExternoPort;
    }

    @Override
    public void guardarPropietario(Usuario usuario) {
        validarDatosBasicos(usuario);
        if (usuario.getFechaNacimiento() == null) {
            throw new ValidacionException("La fecha de nacimiento es obligatoria");
        }
        validarMayorDeEdad(usuario.getFechaNacimiento());
        guardarConRol(usuario, ROL_PROPIETARIO);
    }

    @Override
    public void guardarCliente(Usuario usuario) {
        validarDatosBasicos(usuario);
        guardarConRol(usuario, ROL_CLIENTE);
    }

    @Override
    public void guardarEmpleado(Usuario usuario, String correoPropietario) {
        validarDatosBasicos(usuario);
        if (usuario.getIdRestaurante() == null) {
            throw new ValidacionException("El id del restaurante es obligatorio para crear un empleado");
        }

        Long idPropietarioRestaurante = restauranteExternoPort.obtenerIdPropietario(usuario.getIdRestaurante());
        if (idPropietarioRestaurante == null) {
            throw new ValidacionException("El restaurante indicado no existe");
        }

        Usuario propietario = usuarioPersistencePort.buscarPorCorreo(correoPropietario);
        if (propietario == null || !idPropietarioRestaurante.equals(propietario.getId())) {
            throw new NoAutorizadoException("Solo puedes crear empleados para un restaurante que te pertenezca");
        }

        guardarConRol(usuario, ROL_EMPLEADO);
    }

    @Override
    public boolean esPropietario(Long idUsuario) {
        if (idUsuario == null) {
            return false;
        }
        Usuario usuario = usuarioPersistencePort.buscarPorId(idUsuario);
        return usuario != null
                && usuario.getRol() != null
                && ROL_PROPIETARIO.equals(usuario.getRol().getNombre());
    }

    @Override
public Long obtenerIdPorCorreo(String correo) {
    Usuario usuario = usuarioPersistencePort.buscarPorCorreo(correo);
    return usuario != null ? usuario.getId() : null;
}

    @Override
    public Usuario buscarPorId(Long id) {
        if (id == null) {
            throw new ValidacionException("El id del usuario es obligatorio");
        }
        Usuario usuario = usuarioPersistencePort.buscarPorId(id);
        if (usuario == null) {
            throw new UsuarioNoEncontradoException("No existe un usuario con ese id");
        }
        return usuario;
    }

    private void guardarConRol(Usuario usuario, String nombreRol) {
        validarQueNoExista(usuario);

        Rol rol = rolPersistencePort.buscarPorNombre(nombreRol);
        if (rol == null) {
            throw new ValidacionException("El rol " + nombreRol + " no existe en el sistema");
        }

        usuario.setRol(rol);
        usuario.setClave(passwordEncoderPort.encriptar(usuario.getClave()));
        usuarioPersistencePort.guardarUsuario(usuario);
    }

    private void validarDatosBasicos(Usuario usuario) {
        validarCamposObligatorios(usuario);
        validarCorreo(usuario.getCorreo());
        validarCelular(usuario.getCelular());
        validarDocumento(usuario.getDocumentoDeIdentidad());
    }

    private void validarCamposObligatorios(Usuario usuario) {
        if (usuario == null) {
            throw new ValidacionException("El usuario es obligatorio");
        }
        if (estaVacio(usuario.getNombre())) {
            throw new ValidacionException("El nombre es obligatorio");
        }
        if (estaVacio(usuario.getApellido())) {
            throw new ValidacionException("El apellido es obligatorio");
        }
        if (estaVacio(usuario.getDocumentoDeIdentidad())) {
            throw new ValidacionException("El documento de identidad es obligatorio");
        }
        if (estaVacio(usuario.getCelular())) {
            throw new ValidacionException("El celular es obligatorio");
        }
        if (estaVacio(usuario.getCorreo())) {
            throw new ValidacionException("El correo es obligatorio");
        }
        if (estaVacio(usuario.getClave())) {
            throw new ValidacionException("La clave es obligatoria");
        }
    }

    private void validarCorreo(String correo) {
        if (!PATRON_CORREO.matcher(correo).matches()) {
            throw new ValidacionException("El correo no tiene una estructura válida");
        }
    }

    private void validarCelular(String celular) {
        if (!PATRON_CELULAR.matcher(celular).matches()) {
            throw new ValidacionException(
                    "El celular debe tener máximo 13 caracteres, solo dígitos y opcionalmente un + al inicio");
        }
    }

    private void validarDocumento(String documento) {
        if (!PATRON_DOCUMENTO.matcher(documento).matches()) {
            throw new ValidacionException("El documento de identidad debe ser únicamente numérico");
        }
    }

    private void validarMayorDeEdad(LocalDate fechaNacimiento) {
        int edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        if (edad < EDAD_MINIMA) {
            throw new ValidacionException("El usuario debe ser mayor de edad");
        }
    }

    private void validarQueNoExista(Usuario usuario) {
        if (usuarioPersistencePort.existePorCorreo(usuario.getCorreo())) {
            throw new UsuarioYaExisteException("Ya existe un usuario con ese correo");
        }
        if (usuarioPersistencePort.existePorDocumento(usuario.getDocumentoDeIdentidad())) {
            throw new UsuarioYaExisteException("Ya existe un usuario con ese documento de identidad");
        }
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}