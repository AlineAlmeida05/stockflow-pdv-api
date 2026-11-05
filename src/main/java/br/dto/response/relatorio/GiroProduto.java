package br.dto.response.relatorio;

public record GiroProduto(

        String nome,

        Integer comprado,

        Integer vendido,

        Integer giro

) {}