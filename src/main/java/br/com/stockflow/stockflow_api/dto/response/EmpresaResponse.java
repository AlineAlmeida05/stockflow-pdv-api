package br.com.stockflow.stockflow_api.dto.response;

import java.time.LocalDate;

public record EmpresaResponse(

        Long id,

        String nomeFantasia,
        String razaoSocial,
        String cnpj,

        String telefone,
        String email,

        String endereco,
        String cidade,
        String uf,

        String proprietario,

        String logoUrl,

        String slogan,

        String corPrimaria,
        String corSecundaria,

        String versaoSistema,

        LocalDate dataImplantacao

) {
}
