package br.com.stockflow.stockflow_api.dto.relatorio;

public record TopProduto(
        String produtoNome,
        long quantidadeVendida
) {}
