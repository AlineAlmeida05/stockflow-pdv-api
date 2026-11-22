package br.dto.request;

import java.util.List;

public record CriarPedidoDeliveryRequest(

        String clienteNome,

        String telefone,

        String endereco,

        String complemento,

        String observacao,

        String formaPagamento,

        List<CriarPedidoDeliveryItemRequest> itens



) {
}