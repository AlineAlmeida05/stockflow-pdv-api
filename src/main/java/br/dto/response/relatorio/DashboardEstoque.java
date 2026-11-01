package br.dto.response.relatorio;

import java.util.List;

public record DashboardEstoque(

        ResumoEstoque resumo,

        List<ProdutoSemMovimentacao> produtosSemMovimentacao,

        List<SugestaoReposicao> sugestoesReposicao

) {
}
