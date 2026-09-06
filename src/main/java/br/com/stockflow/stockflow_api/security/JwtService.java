package br.com.stockflow.stockflow_api.security;

import br.com.stockflow.stockflow_api.entity.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET =
            "stockflow-pdv-super-secret-key-2026-stockflow-api";

    private final SecretKey key =
            Keys.hmacShaKeyFor(
                    SECRET.getBytes()
            );

    public String gerarToken(
            Usuario usuario) {

        return Jwts.builder()
                .subject(
                        usuario.getEmail()
                )
                .claim(
                        "perfil",
                        usuario.getPerfil().name()
                )
                .claim(
                        "tenantId",
                        usuario.getTenant()
                                .getId()
                                .toString()
                )
                .issuedAt(
                        new Date()
                )
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 1000L * 60 * 60 * 24
                        )
                )
                .signWith(key)
                .compact();

    }

}