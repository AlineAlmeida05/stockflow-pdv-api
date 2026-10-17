package br.service;

import br.dto.response.relatorio.DevedorDTO;
import br.dto.response.relatorio.FiadoResumoDTO;
import br.repository.FiadoRepository;
import br.repository.PagamentoRepository;
import br.security.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;
import br.dto.response.relatorio.DashboardFiados;
import br.dto.response.relatorio.ResumoFiados;
import br.entity.Fiado;
import br.entity.Pagamento;
import br.entity.Usuario;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class RelatorioFiadosService {

    private final FiadoRepository fiadoRepository;

    private final PagamentoRepository pagamentoRepository;

    private final UsuarioAutenticadoService
            usuarioAutenticadoService;

    public RelatorioFiadosService(

            FiadoRepository fiadoRepository,

            PagamentoRepository pagamentoRepository,

            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.fiadoRepository =
                fiadoRepository;

        this.pagamentoRepository =
                pagamentoRepository;

        this.usuarioAutenticadoService =
                usuarioAutenticadoService;
    }

    public DashboardFiados gerarDashboard() {

        Usuario usuario =
                usuarioAutenticadoService
                        .usuarioLogado();

        UUID tenantId =
                usuario.getTenant()
                        .getId();

        var fiados =
                fiadoRepository
                        .findByTenantId(
                                tenantId
                        );

        var pagamentos =
                pagamentoRepository
                        .findByTenantId(
                                tenantId
                        );

        BigDecimal totalFiado =
                fiados.stream()
                        .map(
                                Fiado::getValorTotal
                        )
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
                fiados.stream()
                        .filter(
                                fiado ->
                                        "pendente".equalsIgnoreCase(
                                                fiado.getStatus()
                                        )
                        )
                        .count();

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
                fiados.stream()

                        .filter(
                                fiado ->
                                        "pendente".equalsIgnoreCase(
                                                fiado.getStatus()
                                        )
                        )

                        .map(
                                fiado ->
                                        fiado.getCliente()
                                                .getId()
                        )

                        .distinct()

                        .count();

        var resumo =
                new ResumoFiados(

                        valorEmAberto,

                        valorRecebido,

                        pendentes,

                        pagos,

                        clientesDevedores

                );

        var maioresDevedores =

                fiados.stream()

                        .map(
                                Fiado::getCliente
                        )

                        .distinct()

                        .map(cliente -> {

                            BigDecimal totalFiadoCliente =

                                    fiadoRepository
                                            .findByClienteIdAndTenantId(
                                                    cliente.getId(),
                                                    tenantId
                                            )
                                            .stream()
                                            .map(
                                                    Fiado::getValorTotal
                                            )
                                            .reduce(
                                                    BigDecimal.ZERO,
                                                    BigDecimal::add
                                            );

                            BigDecimal totalPagoCliente =

                                    pagamentoRepository
                                            .findByClienteIdAndTenantId(
                                                    cliente.getId(),
                                                    tenantId
                                            )
                                            .stream()
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


        return new DashboardFiados(

                resumo,

                maioresDevedores,

                ultimosLancamentos

        );
    }

}