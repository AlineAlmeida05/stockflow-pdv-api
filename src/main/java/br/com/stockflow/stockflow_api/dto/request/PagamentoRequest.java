package br.com.stockflow.stockflow_api.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record PagamentoRequest(

        UUID clienteId,

        BigDecimal valorPago,

        String observacao,

        String formaPagamento

) {
}