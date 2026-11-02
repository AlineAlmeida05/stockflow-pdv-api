package br.dto.response;

import java.util.UUID;

public record ImpersonacaoResponse(
        String token,
        UUID tenantId,
        boolean impersonando
) {
}