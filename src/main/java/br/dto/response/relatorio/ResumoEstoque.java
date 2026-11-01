package br.dto.response.relatorio;

public record ResumoEstoque(

        long totalProdutos,

        long semEstoque,

        long estoqueBaixo,

        long reposicao

) {
}