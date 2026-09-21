package br.com.stockflow.stockflow_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PromocaoResponse(

        UUID id,

        UUID produtoId,

        String produtoNome,

        BigDecimal precoOriginal,

        BigDecimal precoPromocional,

        BigDecimal percentualDesconto,

        String motivo,

        Boolean ativa,

        LocalDateTime dataInicio,

        LocalDateTime dataFim

) {
}