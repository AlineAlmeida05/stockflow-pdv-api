package br.com.stockflow.stockflow_api.dto.response;

import java.math.BigDecimal;

public record DashboardResponse(

        Integer totalProdutos,

        Integer totalVendas,

        BigDecimal faturamento,

        BigDecimal fiadosEmAberto,

        Integer clientesDevedores,

        Integer produtosComEstoqueBaixo,

        Integer produtosSemEstoque,

        Integer promocoesAtivas

) {
}
