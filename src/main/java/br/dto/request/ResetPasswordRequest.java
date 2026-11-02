package br.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(

        @NotBlank(
                message = "Token é obrigatório."
        )
        String token,

        @NotBlank(
                message = "Nova senha é obrigatória."
        )
        String novaSenha

) {
}