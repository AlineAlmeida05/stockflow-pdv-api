package br.dto.response.relatorio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record VendaDashboardResponse(

        UUID id,

        LocalDateTime dataVenda,

        String formaPagamento,

        Integer quantidadeItens,

        BigDecimal valorTotal

) {
}