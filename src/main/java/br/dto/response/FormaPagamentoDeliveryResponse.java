package br.dto.response;

import java.math.BigDecimal;

public record FormaPagamentoDeliveryResponse(

        String formaPagamento,

        Long quantidade,

        BigDecimal valor

) {
}