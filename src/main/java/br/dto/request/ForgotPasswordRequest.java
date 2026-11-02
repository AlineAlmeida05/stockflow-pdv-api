package br.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(

        @Email(
                message = "E-mail inválido."
        )
        @NotBlank(
                message = "E-mail é obrigatório."
        )
        String email

) {
}