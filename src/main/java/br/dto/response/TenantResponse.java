package br.dto.response;

import java.util.UUID;

public record TenantResponse(

        UUID id,

        String nome,

        String codigoTenant,

        Boolean ativo

) {
}