package br.com.stockflow.stockflow_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ClienteRequest(

        @NotBlank(
                message = "Nome do cliente é obrigatório."
        )
        String nome,

        @NotBlank(
                message = "Telefone é obrigatório."
        )
        String telefone,

        @Positive(
                message = "Limite de crédito deve ser maior que zero."
        )
        BigDecimal limiteCredito,

        String observacao

) {
}