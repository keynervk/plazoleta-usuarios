package com.sistemas.usuarios.domain.usecase;

import com.sistemas.usuarios.domain.api.IUsuarioServicePort;
import com.sistemas.usuarios.domain.exception.UsuarioYaExisteException;
import com.sistemas.usuarios.domain.exception.ValidacionException;
import com.sistemas.usuarios.domain.model.Rol;
import com.sistemas.usuarios.domain.model.Usuario;
import com.sistemas.usuarios.domain.spi.IPasswordEncoderPort;
import com.sistemas.usuarios.domain.spi.IRolPersistencePort;
import com.sistemas.usuarios.domain.spi.IUsuarioPersistencePort;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

public class UsuarioUseCase implements IUsuarioServicePort {

    private static final String ROL_PROPIETARIO = "PROPIETARIO";
    private static final int EDAD_MINIMA = 18;

    private static final Pattern PATRON_CORREO = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PATRON_CELULAR = Pattern.compile("^\\+?\\d{1,12}$");
    private static final Pattern PATRON_DOCUMENTO = Pattern.compile("^\\d+$");

    private final IUsuarioPersistencePort usuarioPersistencePort;
    private final IRolPersistencePort rolPersistencePort;
    private final IPasswordEncoderPort passwordEncoderPort;

    public UsuarioUseCase(IUsuarioPersistencePort usuarioPersistencePort,
            IRolPersistencePort rolPersistencePort,
            IPasswordEncoderPort passwordEncoderPort) {
        this.usuarioPersistencePort = usuarioPersistencePort;
        this.rolPersistencePort = rolPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public void guardarPropietario(Usuario usuario) {
        validarCamposObligatorios(usuario);
        validarCorreo(usuario.getCorreo());
        validarCelular(usuario.getCelular());
        validarDocumento(usuario.getDocumentoDeIdentidad());
        validarMayorDeEdad(usuario.getFechaNacimiento());
        validarQueNoExista(usuario);

        Rol rolPropietario = rolPersistencePort.buscarPorNombre(ROL_PROPIETARIO);
        if (rolPropietario == null) {
            throw new ValidacionException("El rol PROPIETARIO no existe en el sistema");
        }

        usuario.setRol(rolPropietario);
        usuario.setClave(passwordEncoderPort.encriptar(usuario.getClave()));
        usuarioPersistencePort.guardarUsuario(usuario);
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
        if (usuario.getFechaNacimiento() == null) {
            throw new ValidacionException("La fecha de nacimiento es obligatoria");
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