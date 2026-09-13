package br.com.stockflow.stockflow_api.dto;

import java.util.UUID;

public record ProdutoPromocaoResponse(

        UUID id,

        String nome,

        Integer estoqueAtual,

        Integer percentualGiro,

        Integer diasEstoque,

        Boolean promocaoAtiva,

        String prioridade

) {
}