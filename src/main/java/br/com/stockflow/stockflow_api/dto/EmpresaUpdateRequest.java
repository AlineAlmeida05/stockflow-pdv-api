package br.com.stockflow.stockflow_api.dto;

public record EmpresaUpdateRequest(

        String nomeFantasia,
        String razaoSocial,

        String telefone,
        String email,

        String endereco,
        String cidade,
        String uf,

        String proprietario,

        String logoUrl,

        String slogan,

        String corPrimaria,
        String corSecundaria

) {
}
