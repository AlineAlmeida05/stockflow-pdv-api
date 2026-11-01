package br.dto.response.relatorio;

public record TopProduto(
        String produtoNome,
        long quantidadeVendida
) {}
