package br.dto.request;

import java.util.UUID;

public record CriarPedidoDeliveryItemRequest(

        UUID produtoId,

        Integer quantidade

) {
}