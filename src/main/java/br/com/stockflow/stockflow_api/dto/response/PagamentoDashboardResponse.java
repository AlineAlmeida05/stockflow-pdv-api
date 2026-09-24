package br.com.stockflow.stockflow_api.dto.response;

import java.math.BigDecimal;

public record PagamentoDashboardResponse(

        String formaPagamento,

        BigDecimal valor

) {
}