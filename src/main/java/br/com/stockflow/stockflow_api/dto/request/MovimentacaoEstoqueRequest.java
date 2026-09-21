package br.com.stockflow.stockflow_api.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record MovimentacaoEstoqueRequest(

        UUID produtoId,

        String tipo,

        Integer quantidade,

        BigDecimal precoCompra,

        String observacao

) {
}