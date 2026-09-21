package br.com.stockflow.stockflow_api.dto;

import br.com.stockflow.stockflow_api.entity.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UsuarioUpdateRequest(

        @NotBlank(
                message = "Nome é obrigatório."
        )
        String nome,

        @Email(
                message = "E-mail inválido."
        )
        @NotBlank(
                message = "E-mail é obrigatório."
        )
        String email,

        String senha,

        @NotNull(
                message = "Perfil é obrigatório."
        )
        Perfil perfil,

        @NotNull(
                message = "Status é obrigatório."
        )
        Boolean ativo,

        UUID tenantId

) {
}