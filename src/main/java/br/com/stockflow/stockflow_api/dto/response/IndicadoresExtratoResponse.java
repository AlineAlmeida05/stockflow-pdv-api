package br.com.stockflow.stockflow_api.dto.response;

import java.math.BigDecimal;

public record IndicadoresExtratoResponse(

        BigDecimal totalRecebido,

        BigDecimal saldoAberto,

        BigDecimal recebimentosHoje,

        Integer clientesDevedores

) {
}