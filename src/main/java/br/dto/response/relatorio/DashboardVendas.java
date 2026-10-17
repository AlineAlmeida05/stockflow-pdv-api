package br.dto.response.relatorio;

import org.springframework.data.domain.Page;

import java.util.List;

public record DashboardVendas(
        ResumoVendas resumo,
        MelhorDia melhorDia,
        List<ReceitaPorFormaPagamento> receitaPorFormaPagamento,
        List<EvolucaoVendas> evolucaoVendas,
        List<TopProduto> top5Produtos,
        Page<VendaHistorico> historico
) {}
