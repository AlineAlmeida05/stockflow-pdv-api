package br.dto.response;

public record UsuarioAtualResponse(
        String id,
        String nome,
        String email,
        String perfil,
        String tenantId,
        String tenantNome
) {
}