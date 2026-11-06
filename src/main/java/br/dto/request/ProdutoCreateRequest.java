package br.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record ProdutoCreateRequest(

        @NotBlank(
                message = "Nome é obrigatório."
        )
        String nome,

        String categoria,

        @NotNull(
                message = "Preço de venda é obrigatório."
        )
        @Positive(
                message = "Preço de venda deve ser maior que zero."
        )
        BigDecimal precoVenda,

        @NotNull(
                message = "Estoque inicial é obrigatório."
        )
        @PositiveOrZero(
                message = "Estoque inicial deve ser maior ou igual a zero."
        )
        Integer estoqueInicial,

        @NotNull(
                message = "Estoque mínimo é obrigatório."
        )
        @PositiveOrZero(
                message = "Estoque mínimo deve ser maior ou igual a zero."
        )
        Integer estoqueMinimo

) {
}