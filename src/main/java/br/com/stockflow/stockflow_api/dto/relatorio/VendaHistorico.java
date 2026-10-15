package br.com.stockflow.stockflow_api.dto.relatorio;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VendaHistorico(
        LocalDateTime dataHora,
        String formaPagamento,
        int itens,
        BigDecimal valor
) {}
