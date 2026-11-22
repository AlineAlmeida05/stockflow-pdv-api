package br.dto.response;

import java.math.BigDecimal;

public record PedidoDeliveryResumoDashboardResponse(

        Long novos,
        BigDecimal valorNovos,

        Long separando,
        BigDecimal valorSeparando,

        Long prontosParaEntrega,
        BigDecimal valorProntosParaEntrega,

        Long emEntrega,
        BigDecimal valorEmEntrega,

        Long entreguesHoje,
        BigDecimal valorEntregues,

        Long canceladosHoje

) {
}