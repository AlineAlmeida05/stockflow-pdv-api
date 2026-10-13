package br.dto;

public record PromocaoMetrics(

        int quantidadeComprada,

        int quantidadeVendida,

        int percentualGiro,

        long diasEstoque,

        String motivo,

        String prioridade,

        int percentualDesconto,

        int metaSugestao

) {
}