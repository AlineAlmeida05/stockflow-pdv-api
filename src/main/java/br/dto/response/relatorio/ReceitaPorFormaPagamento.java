package br.dto.response.relatorio;

import java.math.BigDecimal;

public record ReceitaPorFormaPagamento(
        String formaPagamento,
        BigDecimal valor
) {}
