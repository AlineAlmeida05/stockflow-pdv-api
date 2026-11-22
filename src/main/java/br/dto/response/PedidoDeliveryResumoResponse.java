package br.dto.response;

import br.entity.StatusPedidoDelivery;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PedidoDeliveryResumoResponse(

        UUID id,

        String numero,

        String clienteNome,

        String telefone,

        String formaPagamento,

        Integer quantidadeProdutos,

        BigDecimal valorTotal,

        StatusPedidoDelivery status,

        LocalDateTime dataPedido,

        String horaPedido,

        Boolean atrasado

) {
}


