package br.dto.request;

import br.entity.StatusPedidoDelivery;

public record AtualizarStatusPedidoDeliveryRequest(

        StatusPedidoDelivery status

) {
}