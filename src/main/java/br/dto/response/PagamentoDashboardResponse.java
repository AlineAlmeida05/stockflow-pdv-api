package br.dto.response;

import java.math.BigDecimal;

public record PagamentoDashboardResponse(

        String formaPagamento,

        BigDecimal valor

) {
}