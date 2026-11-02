package br.service;

import br.entity.PasswordResetToken;
import br.entity.Usuario;
import br.repository.PasswordResetTokenRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;


    public PasswordResetTokenService(
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailService emailService) {

        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
    }

    public PasswordResetToken criarToken(
            Usuario usuario) {

        PasswordResetToken token = new PasswordResetToken();

        token.setToken(UUID.randomUUID().toString());

        token.setUsuario(usuario);

        token.setExpiracao(
                Instant.now()
                        .plus(
                                1,
                                ChronoUnit.HOURS
                        )
        );

        token.setUtilizado(false);

        return passwordResetTokenRepository.save(token);

    }

    public Optional<PasswordResetToken> buscarPorToken(
            String token) {

        return passwordResetTokenRepository.findByToken(token);

    }

    public boolean expirado(
            PasswordResetToken token) {

        return token.getExpiracao()
                .isBefore(
                        Instant.now()
                );

    }

    public boolean utilizado(
            PasswordResetToken token) {

        return Boolean.TRUE.equals(
                token.getUtilizado()
        );

    }

    public void marcarComoUtilizado(
            PasswordResetToken token) {

        token.setUtilizado(true);

        passwordResetTokenRepository.save(token);

    }

    public boolean tokenValido(
            PasswordResetToken token) {

        return !expirado(token) && !utilizado(token);

    }

}