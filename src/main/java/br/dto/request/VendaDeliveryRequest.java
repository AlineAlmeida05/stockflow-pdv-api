package br.dto.request;

import java.util.List;
import java.util.UUID;

public record VendaDeliveryRequest(

        UUID pedidoDeliveryId,

        String clienteNome,

        String formaPagamento,

        List<VendaDeliveryItemRequest> itens

) {
}