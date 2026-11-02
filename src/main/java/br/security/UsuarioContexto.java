package br.security;

import br.entity.Usuario;
import java.util.UUID;

public record UsuarioContexto(
        Usuario usuario,
        UUID tenantId,
        boolean impersonando
) {
}
