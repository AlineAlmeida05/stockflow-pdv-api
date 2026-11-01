package br.dto.response;

public record PromocaoAtivaResponse(

        String nome,

        Integer estoqueAtual,

        Integer estoqueMinimo

) {
}
