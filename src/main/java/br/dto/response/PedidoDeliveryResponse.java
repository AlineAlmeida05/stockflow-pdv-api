package br.dto.response;

import br.entity.StatusPedidoDelivery;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PedidoDeliveryResponse(

        UUID id,

        String numero,

        Long sequencial,

        String clienteNome,

        String telefone,

        String formaPagamento,

        BigDecimal valorTotal,

        StatusPedidoDelivery status,

        LocalDateTime dataPedido,

        LocalDateTime dataEntrega,

        String horaPedido

) {
}