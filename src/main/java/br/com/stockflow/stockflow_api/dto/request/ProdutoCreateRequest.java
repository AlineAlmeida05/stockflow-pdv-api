package br.com.stockflow.stockflow_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProdutoCreateRequest(

        @NotBlank(
                message = "Nome é obrigatório."
        )
        String nome,

        String categoria,

        String codigoBarras,

        @NotNull(
                message = "Preço de venda é obrigatório."
        )
        @PositiveOrZero(
                message = "Preço de venda deve ser maior ou igual a zero."
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
        @PositiveOrZero(
                message = "Estoque mínimo deve ser maior ou igual a zero."
        )
        Integer estoqueMinimo

) {
}