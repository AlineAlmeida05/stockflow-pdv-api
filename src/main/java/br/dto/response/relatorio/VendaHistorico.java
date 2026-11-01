package br.dto.response.relatorio;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VendaHistorico(
        LocalDateTime dataHora,
        String formaPagamento,
        int itens,
        BigDecimal valor
) {}
