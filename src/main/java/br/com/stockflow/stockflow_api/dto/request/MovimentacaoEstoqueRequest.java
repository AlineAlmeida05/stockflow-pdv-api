package br.com.stockflow.stockflow_api.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MovimentacaoEstoqueRequest(

        @NotNull
        UUID produtoId,

        @NotBlank
        String tipo,

        @NotNull
        @Positive
        Integer quantidade,

        BigDecimal precoCompra,

        String observacao

) {
}