package br.com.stockflow.stockflow_api.dto.request;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record FiadoRequest(

        @NotNull
        UUID clienteId,

        UUID vendaId,

        @NotNull
        @PositiveOrZero
        BigDecimal valorTotal,

        String observacao

) {
}