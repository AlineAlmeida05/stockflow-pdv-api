package br.com.stockflow.stockflow_api.dto.response;

public record PromocaoAtivaResponse(

        String nome,

        Integer estoqueAtual,

        Integer estoqueMinimo

) {
}
