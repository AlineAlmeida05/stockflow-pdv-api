package br.service;

import br.repository.RelatorioVendasRepository;
import br.dto.response.relatorio.DashboardVendas;
import br.dto.response.relatorio.MelhorDia;
import br.dto.response.relatorio.ResumoVendas;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import br.security.UsuarioAutenticadoService;

@Service
public class RelatorioService {

    private final RelatorioVendasRepository relatorioRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private static final int
            LIMITE_HISTORICO = 10;

    public RelatorioService(
            RelatorioVendasRepository relatorioRepository,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.relatorioRepository = relatorioRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    public DashboardVendas gerarDashboard(
            LocalDate inicio,
            LocalDate fim,
            String formaPagamento) {

        UUID tenantId =
                usuarioAutenticadoService
                        .tenantIdAtual();

        LocalDateTime inicioDia = inicio.atStartOfDay();

        LocalDateTime fimDia = fim.atTime(LocalTime.MAX);

        var resumoBase = relatorioRepository.findSummary(
                tenantId,
                inicioDia,
                fimDia
        );
        var receitaPorPagamento = relatorioRepository.findRevenueByPaymentMethod(
                tenantId,
                inicioDia,
                fimDia
        );

        // "forma mais utilizada" não vem pronto do banco — é o primeiro item já ordenado por valor
        String formaComMaiorFaturamento = receitaPorPagamento.isEmpty() ? "-"
                : receitaPorPagamento
                  .getFirst()
                  .formaPagamento();

        var resumo = new ResumoVendas(
                resumoBase.totalVendas(),
                resumoBase.faturamento(),
                resumoBase.ticketMedio(),
                formaComMaiorFaturamento
        );

        var melhoresDias = relatorioRepository.findBestDays(
                tenantId,
                inicioDia,
                fimDia,
                PageRequest.
                        of(0, 1
                        ));
        var melhorDia = melhoresDias.isEmpty()
                ? new MelhorDia(
                        null,
                BigDecimal.ZERO
        )
                : melhoresDias.getFirst();

        var evolucao = relatorioRepository.findSalesEvolution(
                tenantId,
                inicioDia,
                fimDia
        );
        var topProdutos = relatorioRepository.findTopProducts(
                tenantId,
                inicioDia,
                fimDia,
                PageRequest.
                        of(0, 5));

        var historico = relatorioRepository
                .findHistory(
                        tenantId,
                        inicioDia,
                        fimDia,
                        formaPagamento,
                        PageRequest.of(
                                0,
                                LIMITE_HISTORICO
                        ))
                .getContent();

        return new DashboardVendas(
                resumo,
                melhorDia,
                receitaPorPagamento,
                evolucao,
                topProdutos,
                historico
        );
    }
}