package br.com.stockflow.stockflow_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TenantRequest(

        @NotBlank
        String nome,

        String responsavel,

        String email,

        String cidade,

        Boolean ativo,

        String slug,

        String codigoTenant,

        String logoUrl,

        String faviconUrl,

        String corPrimaria,

        String corSecundaria

) {
}