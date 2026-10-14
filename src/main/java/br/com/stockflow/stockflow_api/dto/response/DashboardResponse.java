package br.com.stockflow.stockflow_api.dto.response;

import java.math.BigDecimal;
import java.util.List;
import br.com.stockflow.stockflow_api.dto.response.PromocaoAtivaResponse;

public record DashboardResponse(

        Integer totalProdutos,

        Integer totalVendas,

        BigDecimal faturamento,

        BigDecimal fiadosEmAberto,

        Integer clientesDevedores,

        Integer produtosComEstoqueBaixo,

        Integer produtosSemEstoque,

        Integer promocoesAtivas,

        List<EvolucaoVendaResponse> evolucaoVendas,

        List<EvolucaoFiadoResponse> evolucaoFiados,

        List<GiroEstoqueResponse> giroEstoque,

        List<PagamentoDashboardResponse> faturamentoPorPagamento,

        List<TopProdutoDashboardResponse> topProdutosVendidos,

        List<PromocaoEficienteResponse>
        promocoesEficientes,

        List<PromocaoAtivaResponse>
        promocoesAtivasDetalhes,

        List<ProdutoPromocionalResponse>
        produtosPromocionaisMaisVendidos,

        Integer totalVendasPromocionais,

        BigDecimal faturamentoPromocional,

        Integer totalPromocoesEficientes,

        List<PromocaoBaixaEfetividadeResponse>
        promocoesBaixaEfetividade


) {
}
