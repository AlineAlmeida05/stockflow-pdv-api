package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.dto.relatorio.*;
import br.com.stockflow.stockflow_api.repository.RelatorioVendasRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Service
public class RelatorioService {

    private final RelatorioVendasRepository relatorioRepository;

    public RelatorioService(RelatorioVendasRepository relatorioRepository) {
        this.relatorioRepository = relatorioRepository;
    }

    public DashboardVendas gerarDashboard(UUID inquilinoId, LocalDate inicio, LocalDate fim, String formaPagamento) {
        LocalDateTime inicioDia = inicio.atStartOfDay();
        LocalDateTime fimDia = fim.atTime(LocalTime.MAX);

        var resumoBase = relatorioRepository.findSummary(inquilinoId, inicioDia, fimDia);
        var receitaPorPagamento = relatorioRepository.findRevenueByPaymentMethod(inquilinoId, inicioDia, fimDia);

        // "forma mais utilizada" não vem pronto do banco — é o primeiro item já ordenado por valor
        String formaMaisUtilizada = receitaPorPagamento.isEmpty() ? "-" : receitaPorPagamento.getFirst().formaPagamento();
        var resumo = new ResumoVendas(
                resumoBase.totalVendas(),
                resumoBase.faturamento(),
                resumoBase.ticketMedio(),
                formaMaisUtilizada
        );

        var melhoresDias = relatorioRepository.findBestDays(inquilinoId, inicioDia, fimDia, PageRequest.of(0, 1));
        var melhorDia = melhoresDias.isEmpty()
                ? new MelhorDia(null, BigDecimal.ZERO)
                : melhoresDias.getFirst();

        var evolucao = relatorioRepository.findSalesEvolution(inquilinoId, inicioDia, fimDia);
        var topProdutos = relatorioRepository.findTopProducts(inquilinoId, inicioDia, fimDia, PageRequest.of(0, 5));

        var historico = relatorioRepository.findHistory(
                inquilinoId, inicioDia, fimDia, formaPagamento, PageRequest.of(0, 9));

        return new DashboardVendas(resumo, melhorDia, receitaPorPagamento, evolucao, topProdutos, historico);
    }
}