package br.com.stockflow.stockflow_api.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record PromocaoRequest(

        UUID produtoId,

        BigDecimal precoPromocional,

        BigDecimal percentualDesconto,

        String motivo

) {
}