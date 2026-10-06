package br.com.stockflow.stockflow_api.dto.relatorio;

import java.math.BigDecimal;

public record ReceitaPorFormaPagamento(
        String formaPagamento,
        BigDecimal valor
) {}
