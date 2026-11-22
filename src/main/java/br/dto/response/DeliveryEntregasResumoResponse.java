package br.dto.response;

public record DeliveryEntregasResumoResponse(

        Long tempoMedioEntrega,

        Long entregasConcluidas,

        Long pedidosEmEntrega,

        Long pedidosAtrasados

) {
}