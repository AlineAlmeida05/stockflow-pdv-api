package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.entity.RefreshToken;
import br.com.stockflow.stockflow_api.entity.Usuario;
import br.com.stockflow.stockflow_api.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository) {

        this.refreshTokenRepository =
                refreshTokenRepository;
    }

    public RefreshToken criarToken(
            Usuario usuario) {

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setToken(
                UUID.randomUUID().toString());

        refreshToken.setUsuario(
                usuario);

        refreshToken.setExpiracao(
                Instant.now()
                        .plus(
                                7,
                                ChronoUnit.DAYS));

        return refreshTokenRepository.save(
                refreshToken);
    }

    public Optional<RefreshToken> buscarPorToken(
            String token) {

        return refreshTokenRepository
                .findByToken(token);
    }

    public boolean expirado(
            RefreshToken refreshToken) {

        return refreshToken
                .getExpiracao()
                .isBefore(
                        Instant.now());
    }

    public void remover(
            RefreshToken refreshToken) {

        refreshTokenRepository.delete(
                refreshToken);
    }
}

