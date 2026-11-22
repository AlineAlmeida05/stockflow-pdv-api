package br.dto.response;

import java.math.BigDecimal;

public record PedidoDeliveryItemResponse(

        String produto,

        Integer quantidade,

        BigDecimal precoUnitario,

        BigDecimal subtotal

) {
}