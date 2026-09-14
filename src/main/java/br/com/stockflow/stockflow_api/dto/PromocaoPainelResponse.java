package br.com.stockflow.stockflow_api.dto;

import java.util.List;

public record PromocaoPainelResponse(

        List<ProdutoPromocaoResponse> pendentes,

        List<ProdutoPromocaoResponse> ativas

) {
}