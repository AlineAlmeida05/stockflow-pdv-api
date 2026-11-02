package br.repository;

import br.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import br.entity.Usuario;
import java.util.List;

@Repository
public interface PasswordResetTokenRepository
        extends JpaRepository<
        PasswordResetToken,
        UUID> {

    @EntityGraph(attributePaths = "usuario")
    Optional<PasswordResetToken> findByToken(
            String token
    );

    List<PasswordResetToken>
    findByUsuarioAndUtilizadoFalse(
            Usuario usuario
    );

}