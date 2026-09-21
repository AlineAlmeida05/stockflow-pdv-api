package br.com.stockflow.stockflow_api.dto.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record VendaRequest(

        String formaPagamento,

        UUID clienteId,

        BigDecimal valorRecebido,

        List<ItemVendaRequest> itens

) {
}