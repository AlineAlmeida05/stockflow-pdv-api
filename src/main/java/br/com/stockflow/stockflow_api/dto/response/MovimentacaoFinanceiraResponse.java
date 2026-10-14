package br.com.stockflow.stockflow_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimentacaoFinanceiraResponse(

        String tipo,

        String clienteNome,

        BigDecimal valor,

        LocalDateTime data,

        String usuarioNome,

        String formaPagamento

) {
}
