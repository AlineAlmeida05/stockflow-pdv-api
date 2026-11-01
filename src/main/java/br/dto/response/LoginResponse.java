package br.dto.response;


public record LoginResponse(
        String id,
        String nome,
        String email,
        String perfil,
        String tenantId,
        String tenantNome,
        String token
) {
}