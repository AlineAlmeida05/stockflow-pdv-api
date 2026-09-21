package br.com.stockflow.stockflow_api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record VendaDetalhesResponse(

        UUID id,

        LocalDateTime dataVenda,

        String formaPagamento,

        BigDecimal valorTotal,

        Integer quantidadeItens,

        String clienteNome,

        String status,

        String motivoCancelamento,

        LocalDateTime dataCancelamento,

        String usuarioCancelamento,

        String usuarioNome,

        List<ItemVendaResponse> itens

) {
}