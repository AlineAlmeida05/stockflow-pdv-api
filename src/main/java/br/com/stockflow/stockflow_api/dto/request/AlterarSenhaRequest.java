package br.com.stockflow.stockflow_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record AlterarSenhaRequest(

        @NotBlank(
                message = "Senha atual é obrigatória."
        )
        String senhaAtual,

        @NotBlank(
                message = "Nova senha é obrigatória."
        )
        @Size(
                min = 6,
                message = "A senha deve possuir pelo menos 6 caracteres."
        )
        String novaSenha,

        @NotBlank(
                message = "Confirmação de senha é obrigatória."
        )
        String confirmarSenha

) {
}