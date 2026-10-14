package br.com.stockflow.stockflow_api.dto.response;

import br.com.stockflow.stockflow_api.entity.Perfil;

import java.util.UUID;

public record UsuarioResponse(

        UUID id,

        String nome,

        String email,

        Perfil perfil,

        Boolean ativo,

        UUID tenantId

) {
}