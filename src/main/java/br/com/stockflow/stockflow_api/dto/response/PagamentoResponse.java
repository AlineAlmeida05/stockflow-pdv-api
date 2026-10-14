package br.com.stockflow.stockflow_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PagamentoResponse(

        UUID id,

        UUID clienteId,

        String clienteNome,

        BigDecimal valorPago,

        String usuarioNome,

        LocalDateTime dataPagamento,

        String observacao,

        String formaPagamento

) {
}
