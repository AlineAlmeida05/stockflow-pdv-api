package br.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TenantRequest(

        String nome,

        String razaoSocial,

        String cnpj,

        String responsavel,

        String email,

        String telefone,

        String endereco,

        String cidade,

        String uf,

        Boolean ativo,

        String slug,

        String codigoTenant,

        String logoUrl,

        String faviconUrl,

        String corPrimaria,

        String corSecundaria,

        Boolean marketingIaHabilitado,

        Boolean deliveryHabilitado

) {
}