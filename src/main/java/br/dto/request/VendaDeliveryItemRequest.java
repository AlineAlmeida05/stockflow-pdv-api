package br.dto.request;

import java.util.UUID;

public record VendaDeliveryItemRequest(

        UUID produtoId,

        Integer quantidade

) {
}