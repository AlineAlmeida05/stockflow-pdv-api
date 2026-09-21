package br.com.stockflow.stockflow_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FiadoResponse(

        UUID id,

        UUID clienteId,

        String clienteNome,

        UUID vendaId,

        BigDecimal valorTotal,

        LocalDateTime dataLancamento,

        String status,

        String observacao

) {
}