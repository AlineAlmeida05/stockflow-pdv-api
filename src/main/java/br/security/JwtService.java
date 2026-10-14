package br.security;

import br.entity.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;


@Service
public class JwtService {

        @Value("${jwt.secret}")
        private String secret;

        private SecretKey getKey() {
                return Keys.hmacShaKeyFor(
                        secret.getBytes()
                );
        }

        public String gerarToken(
                        Usuario usuario) {

                return Jwts.builder()
                                .subject(
                                                usuario.getEmail())
                                .claim(
                                                "perfil",
                                                usuario.getPerfil().name())
                                .claim(
                                                "tenantId",
                                                usuario.getTenant()
                                                                .getId()
                                                                .toString())
                                .issuedAt(
                                                new Date())
                                .expiration(
                                                new Date(
                                                                System.currentTimeMillis()
                                                                                + 1000L * 60 * 60 * 24))
                        .signWith(getKey())
                                .compact();

        }

        public String extrairEmail(
                        String token) {

                return Jwts.parser()
                        .verifyWith(getKey())
                                .build()
                                .parseSignedClaims(token)
                                .getPayload()
                                .getSubject();

        }

        public boolean validarToken(
                        String token) {

                try {

                        Jwts.parser()
                                .verifyWith(getKey())
                                        .build()
                                        .parseSignedClaims(token);

                        return true;

                } catch (JwtException e) {

                        return false;

                }

        }

}