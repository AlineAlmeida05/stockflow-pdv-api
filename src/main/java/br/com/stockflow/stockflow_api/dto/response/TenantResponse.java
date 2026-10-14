package br.com.stockflow.stockflow_api.dto.response;

import java.util.UUID;

public record TenantResponse(

        UUID id,

        String nome,

        String codigoTenant,

        Boolean ativo

) {
}