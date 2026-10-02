package com.sistemas.usuarios.infrastructure.output.jwt;

import com.sistemas.usuarios.domain.model.Usuario;
import com.sistemas.usuarios.domain.spi.ITokenPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtAdapter implements ITokenPort {

    private final SecretKey llave;
    private final long expiracionMinutos;

    public JwtAdapter(@Value("${app.jwt.secret}") String secreto,
                      @Value("${app.jwt.expiracion-minutos}") long expiracionMinutos) {
        this.llave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.expiracionMinutos = expiracionMinutos;
    }

    @Override
    public String generarToken(Usuario usuario) {
        Instant ahora = Instant.now();
        Instant expiracion = ahora.plusSeconds(expiracionMinutos * 60);

        return Jwts.builder()
                .subject(usuario.getCorreo())
                .claim("id", usuario.getId())
                .claim("rol", usuario.getRol().getNombre())
                .claim("nombre", usuario.getNombre())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(expiracion))
                .signWith(llave)
                .compact();
    }

    public Claims validarYObtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith(llave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}