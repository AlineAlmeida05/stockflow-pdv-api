package br.com.stockflow.stockflow_api.dto.request;

import br.com.stockflow.stockflow_api.entity.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UsuarioCreateRequest(

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

        @NotBlank(
                message = "Senha é obrigatória."
        )
        String senha,

        @NotNull(
                message = "Perfil é obrigatório."
        )
        Perfil perfil,

        @NotNull(
                message = "Tenant é obrigatório."
        )
        UUID tenantId

) {
}