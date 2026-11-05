package br.dto.response.relatorio;

import java.util.UUID;

public record ProdutoEstoqueBaixo(

        UUID id,

        String nome,

        Integer estoqueAtual,

        Integer estoqueMinimo

) {}