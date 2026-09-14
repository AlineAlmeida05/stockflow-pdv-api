package br.com.stockflow.stockflow_api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProdutoPromocaoResponse(

        UUID id,

        String nome,

        Integer estoqueAtual,

        Integer percentualGiro,

        Integer diasEstoque,

        Boolean promocaoAtiva,

        String prioridade,

        String motivo,

        Integer percentualDesconto,

        Integer metaSugestao,

        BigDecimal precoPromocional,

        BigDecimal receitaPotencial,

        BigDecimal economiaUnitaria,

        BigDecimal impactoFinanceiro,

        Integer quantidadeComprada,

        Integer quantidadeVendida,

        Boolean promocaoEficiente,

        String descricaoPromocao

) {
}