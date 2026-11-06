package br.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ProdutoResponse(

        UUID id,

        String codigo,

        String nome,

        String categoria,

        BigDecimal custoMedio,

        BigDecimal precoVenda,

        Integer estoqueAtual,

        Integer estoqueMinimo,

        Boolean ativo,

        Boolean promocaoAtiva,

        BigDecimal precoPromocional

) {
}