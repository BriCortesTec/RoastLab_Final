package com.roastlab.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration}") long expiration) {

        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expiration = expiration;
    }

    public String generarToken(String email) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expiration);

        return Jwts.builder()
                .subject(email)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(secretKey)
                .compact();
    }

    public String extraerEmail(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    private <T> T extraerClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return resolver.apply(claims);
    }

    public boolean esTokenValido(String token, UserDetails usuario) {
        try {
            String email = extraerEmail(token);

            return email.equals(usuario.getUsername())
                    && !estaExpirado(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean estaExpirado(String token) {
        Date expiracion = extraerClaim(token, Claims::getExpiration);
        return expiracion.before(new Date());
    }
}
