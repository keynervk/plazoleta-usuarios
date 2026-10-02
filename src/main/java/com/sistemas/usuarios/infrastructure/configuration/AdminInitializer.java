package com.sistemas.usuarios.infrastructure.configuration;

import com.sistemas.usuarios.infrastructure.output.jpa.entity.RolEntity;
import com.sistemas.usuarios.infrastructure.output.jpa.entity.UsuarioEntity;
import com.sistemas.usuarios.infrastructure.output.jpa.repository.IRolRepository;
import com.sistemas.usuarios.infrastructure.output.jpa.repository.IUsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class AdminInitializer {

    private static final String CORREO_ADMIN = "admin@plazoleta.com";
    private static final String CLAVE_ADMIN = "admin1234";

    @Bean
    public CommandLineRunner crearAdminPorDefecto(IUsuarioRepository usuarioRepository,
                                                  IRolRepository rolRepository,
                                                  PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.existsByCorreo(CORREO_ADMIN)) {
                return;
            }

            RolEntity rolAdmin = rolRepository.findByNombre("ADMINISTRADOR")
                    .orElseThrow(() -> new IllegalStateException("El rol ADMINISTRADOR no existe"));

            UsuarioEntity admin = new UsuarioEntity();
            admin.setNombre("Admin");
            admin.setApellido("Plazoleta");
            admin.setDocumentoDeIdentidad("000000000");
            admin.setCelular("+573000000000");
            admin.setFechaNacimiento(LocalDate.of(1990, 1, 1));
            admin.setCorreo(CORREO_ADMIN);
            admin.setClave(passwordEncoder.encode(CLAVE_ADMIN));
            admin.setRol(rolAdmin);

            usuarioRepository.save(admin);
        };
    }
}