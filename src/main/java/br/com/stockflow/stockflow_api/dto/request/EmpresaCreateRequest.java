package br.com.stockflow.stockflow_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmpresaCreateRequest(

        @NotBlank
        String nomeFantasia,

        @NotBlank
        String razaoSocial,

        @NotBlank
        String cnpj,

        @NotBlank
        String telefone,

        @Email
        @NotBlank
        String email,

        @NotBlank
        String endereco,

        @NotBlank
        String cidade,

        @NotBlank
        String uf,

        @NotBlank
        String proprietario,

        String logoUrl,

        String slogan,

        String corPrimaria,

        String corSecundaria

) {
}
