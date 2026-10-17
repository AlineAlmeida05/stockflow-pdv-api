package br.dto.response.relatorio;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FiadoResumoDTO(

        String clienteNome,

        BigDecimal valor,

        String status,

        LocalDateTime dataLancamento

) {
}