package br.com.stockflow.stockflow_api.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ClienteResumoResponse(

        UUID clienteId,

        String nome,

        BigDecimal limiteCredito,

        BigDecimal saldoDevedor,

        BigDecimal creditoDisponivel,

        String status,

        Integer diasSemPagamento

) {
}