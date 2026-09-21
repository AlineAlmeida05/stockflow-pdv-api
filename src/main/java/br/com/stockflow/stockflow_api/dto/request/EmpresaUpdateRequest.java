package br.com.stockflow.stockflow_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmpresaUpdateRequest(
        @NotBlank(
                message = "Nome é obrigatório."
        )
        String nomeFantasia,

        @NotBlank(
                message = "Razão Social é obrigatório."
        )
        String razaoSocial,

        @NotBlank(
                message = "Telefone é obrigatório."
        )
        String telefone,

        @Email(
                message = "E-mail inválido."
        )

        @NotBlank(
                message = "E-mail é obrigatório."
        )
        String email,


        @NotBlank(
                message = "Endereço é obrigatório."
        )
        String endereco,

        @NotBlank(
                message = "Cidade é obrigatória."
        )
        String cidade,

        @NotBlank(
                message = "UF é obrigatória."
        )
        String uf,

        @NotBlank(
                message = "Proprietário é obrigatório."
        )
        String proprietario,

        @NotBlank(
                message = "Logo é obrigatória."
        )
        String logoUrl,

        @NotBlank(
                message = "Slogan é obrigatório."
        )
        String slogan,

        @NotBlank(
                message = "Cor primária é obrigatória."
        )
        String corPrimaria,

        @NotBlank(
                message = "Cor Secundária é obrigatória."
        )
        String corSecundaria

) {
}
