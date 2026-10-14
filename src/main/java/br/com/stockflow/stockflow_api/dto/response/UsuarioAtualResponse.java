package br.com.stockflow.stockflow_api.dto.response;

public record UsuarioAtualResponse(
        String id,
        String nome,
        String email,
        String perfil,
        String tenantId,
        String tenantNome
) {
}