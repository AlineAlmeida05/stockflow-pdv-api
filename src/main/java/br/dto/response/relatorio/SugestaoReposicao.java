package br.dto.response.relatorio;

import java.util.UUID;

public record SugestaoReposicao(

        UUID id,

        String nome,

        Integer estoqueAtual,

        Integer estoqueMinimo,

        Integer quantidadeReposicao

) {
}