package br.com.stockflow.stockflow_api.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record PromocaoRequest(

        @NotNull
        UUID produtoId,

        @Positive
        BigDecimal precoPromocional,

        @PositiveOrZero
        BigDecimal percentualDesconto,

        String motivo

) {
}