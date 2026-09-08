package br.com.stockflow.stockflow_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MovimentacaoEstoqueResponse(

        UUID id,

        String produtoNome,

        String tipo,

        Integer quantidade,

        BigDecimal precoCompra,

        String observacao,

        String usuarioNome,

        LocalDateTime dataMovimentacao

) {
}