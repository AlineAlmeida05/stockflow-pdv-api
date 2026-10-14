package br.com.stockflow.stockflow_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProdutoUpdateRequest(

        @NotBlank(
                message = "Nome é obrigatório."
        )
        String nome,

        String categoria,

        String codigoBarras,

        @NotNull(
                message = "Preço de venda é obrigatório."
        )
        @Positive(
                message = "Preço de venda deve ser maior que zero."
        )
        BigDecimal precoVenda,

        @NotNull(
                message = "Estoque atual é obrigatório."
        )
        @PositiveOrZero(
                message = "Estoque atual deve ser maior ou igual a zero."
        )
        Integer estoqueAtual,

        @NotNull(
                message = "Estoque mínimo é obrigatório."
        )
        @Positive(
                message = "Estoque mínimo deve ser maior que zero."
        )
        Integer estoqueMinimo,

        @NotNull(
                message = "Status é obrigatório."
        )
        Boolean ativo

) {
}