package br.dto.response;

import java.math.BigDecimal;

public record IndicadoresExtratoResponse(

        BigDecimal totalRecebido,

        BigDecimal saldoAberto,

        BigDecimal recebimentosHoje,

        Integer clientesDevedores

) {
}