package br.service;

import br.dto.response.relatorio.*;
import br.repository.FiadoRepository;
import br.repository.PagamentoRepository;
import br.security.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;
import br.entity.Fiado;
import br.entity.Pagamento;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class RelatorioFiadosService {

    private final FiadoRepository fiadoRepository;
    private final PagamentoRepository pagamentoRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public RelatorioFiadosService(
            FiadoRepository fiadoRepository,
            PagamentoRepository pagamentoRepository,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.fiadoRepository = fiadoRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    public DashboardFiados gerarDashboard() {

        UUID tenantId =
                usuarioAutenticadoService
                        .tenantIdAtual();

        var fiados =
                fiadoRepository
                        .findByTenantId(
                                tenantId
                        );

        var fiadosPendentes =

                fiados.stream()

                        .filter(
                                fiado ->
                                        "pendente"
                                                .equalsIgnoreCase(
                                                        fiado.getStatus()
                                                )
                        )

                        .toList();

        var pagamentos =
                pagamentoRepository
                        .findByTenantId(
                                tenantId
                        );

        BigDecimal totalFiado =
                fiadosPendentes.stream()
                        .map(Fiado::getValorTotal)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalPago =
                pagamentos.stream()
                        .map(
                                Pagamento::getValorPago
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal valorEmAberto =
                totalFiado.subtract(
                        totalPago
                );

        BigDecimal valorRecebido =
                pagamentos.stream()
                        .map(
                                Pagamento::getValorPago
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );
        long pendentes =
                fiadosPendentes.size();


        long pagos =
                fiados.stream()
                        .filter(
                                fiado ->
                                        "quitado".equalsIgnoreCase(
                                                fiado.getStatus()
                                        )
                        )
                        .count();

        long clientesDevedores =
                fiadosPendentes.stream()
                        .map(
                                fiado ->
                                        fiado.getCliente()
                                                .getId()
                        )
                        .distinct()
                        .count();

        var resumo =

                new ResumoFiados(

                        totalFiado,

                        valorEmAberto,

                        valorRecebido,

                        pendentes,

                        pagos,

                        clientesDevedores

                );

        var maioresDevedores =

                fiadosPendentes.stream()

                        .map(
                                Fiado::getCliente
                        )

                        .distinct()

                        .map(cliente -> {

                            BigDecimal totalFiadoCliente =
                                    fiadosPendentes.stream()
                                            .filter(
                                                    fiado ->
                                                            fiado.getCliente()
                                                                    .getId()
                                                                    .equals(
                                                                            cliente.getId()
                                                                    )
                                            )
                                            .map(
                                                    Fiado::getValorTotal
                                            )
                                            .reduce(
                                                    BigDecimal.ZERO,
                                                    BigDecimal::add
                                            );

                            BigDecimal totalPagoCliente =
                                    pagamentos.stream()
                                            .filter(
                                                    pagamento ->
                                                            pagamento.getCliente()
                                                                    .getId()
                                                                    .equals(
                                                                            cliente.getId()
                                                                    )
                                            )
                                            .map(
                                                    Pagamento::getValorPago
                                            )
                                            .reduce(
                                                    BigDecimal.ZERO,
                                                    BigDecimal::add
                                            );

                            BigDecimal saldoDevedor =

                                    totalFiadoCliente.subtract(
                                            totalPagoCliente
                                    );

                            return new DevedorDTO(

                                    cliente.getNome(),

                                    saldoDevedor

                            );

                        })

                        .filter(
                                devedor ->
                                        devedor.total()
                                                .compareTo(
                                                        BigDecimal.ZERO
                                                ) > 0
                        )

                        .sorted(
                                (a, b) ->
                                        b.total()
                                                .compareTo(
                                                        a.total()
                                                )
                        )

                        .limit(5)

                        .toList();



        var ultimosLancamentos =

                fiados.stream()

                        .sorted(
                                (a, b) ->
                                        b.getDataLancamento()
                                                .compareTo(
                                                        a.getDataLancamento()
                                                )
                        )

                        .limit(10)

                        .map(
                                fiado ->
                                        new FiadoResumoDTO(

                                                fiado.getCliente()
                                                        .getNome(),

                                                fiado.getValorTotal(),

                                                fiado.getStatus(),

                                                fiado.getDataLancamento()

                                        )
                        )

                        .toList();

        var evolucaoFiados =

                fiadosPendentes.stream()

                        .collect(

                                java.util.stream.Collectors
                                        .groupingBy(

                                                fiado ->
                                                        fiado.getDataLancamento()
                                                                .toLocalDate(),

                                                java.util.stream.Collectors
                                                        .mapping(

                                                                Fiado::getValorTotal,

                                                                java.util.stream.Collectors
                                                                        .reducing(
                                                                                BigDecimal.ZERO,
                                                                                BigDecimal::add
                                                                        )
                                                        )
                                        )
                        )

                        .entrySet()

                        .stream()

                        .map(entry ->

                                new EvolucaoFiados(

                                        entry.getKey(),

                                        entry.getValue()

                                )
                        )

                        .sorted(
                                java.util.Comparator
                                        .comparing(
                                                EvolucaoFiados::data
                                        )
                        )

                        .toList();


        return new DashboardFiados(

                resumo,

                maioresDevedores,

                ultimosLancamentos,

                evolucaoFiados

        );
    }

}