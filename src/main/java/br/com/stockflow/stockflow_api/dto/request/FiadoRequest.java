package br.com.stockflow.stockflow_api.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record FiadoRequest(

        UUID clienteId,

        UUID vendaId,

        BigDecimal valorTotal,

        String observacao

) {
}