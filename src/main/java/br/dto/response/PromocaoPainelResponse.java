package br.dto.response;

import java.util.List;

public record PromocaoPainelResponse(

        List<ProdutoPromocaoResponse> pendentes,

        List<ProdutoPromocaoResponse> ativas

) {
}