package com.sistemas.usuarios.infrastructure.configuration;

import com.sistemas.usuarios.infrastructure.output.jpa.entity.RolEntity;
import com.sistemas.usuarios.infrastructure.output.jpa.repository.IRolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RolesInitializer {

    @Bean
    public CommandLineRunner cargarRoles(IRolRepository rolRepository) {
        return args -> {
            crearSiNoExiste(rolRepository, "ADMINISTRADOR", "Administrador de la plataforma");
            crearSiNoExiste(rolRepository, "PROPIETARIO", "Propietario de restaurante");
            crearSiNoExiste(rolRepository, "EMPLEADO", "Empleado de restaurante");
            crearSiNoExiste(rolRepository, "CLIENTE", "Cliente de la plazoleta");
        };
    }

    private void crearSiNoExiste(IRolRepository repository, String nombre, String descripcion) {
        if (repository.findByNombre(nombre).isEmpty()) {
            repository.save(new RolEntity(null, nombre, descripcion));
        }
    }
}