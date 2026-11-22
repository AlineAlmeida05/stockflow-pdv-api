package br.dto.response;

import br.entity.StatusPedidoDelivery;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record PedidoDeliveryDetalheResponse(

        UUID id,

        String numero,

        String clienteNome,

        String telefone,

        String formaPagamento,

        String endereco,

        String complemento,

        String observacao,

        BigDecimal valorTotal,

        StatusPedidoDelivery status,

        LocalDateTime dataPedido,

        LocalDateTime dataEntrega,

        LocalTime horaSaidaEntrega,

        Long tempoEntregaMinutos,

        UUID vendaId,

        List<PedidoDeliveryItemResponse> itens

) {
}