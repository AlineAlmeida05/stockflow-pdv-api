package br.com.stockflow.stockflow_api.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemVendaResponse(

        UUID produtoId,

        String produtoNome,

        Integer quantidade,

        BigDecimal subtotal,

        Boolean promocaoAplicada

) {
}