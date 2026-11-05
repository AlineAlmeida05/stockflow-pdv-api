package br.dto.response.relatorio;

import java.math.BigDecimal;

public record TopProduto(

        String produtoNome,

        long quantidadeVendida,

        BigDecimal faturamento

) {}