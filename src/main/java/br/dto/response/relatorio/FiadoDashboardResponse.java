package br.dto.response.relatorio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FiadoDashboardResponse(

        UUID id,

        String clienteNome,

        BigDecimal valorTotal,

        LocalDateTime dataLancamento

) {
}
