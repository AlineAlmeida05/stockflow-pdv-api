package br.com.stockflow.stockflow_api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record VendaRequest(

        @NotBlank
        String formaPagamento,

        UUID clienteId,

        BigDecimal valorRecebido,

        @NotEmpty
        List<@Valid ItemVendaRequest> itens

) {
}