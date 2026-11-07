package br.service;


import br.dto.response.IndicadoresExtratoResponse;
import br.dto.response.MovimentacaoFinanceiraResponse;
import br.repository.FiadoRepository;
import br.repository.PagamentoRepository;
import br.security.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;
import br.repository.VendaRepository;
import java.time.LocalDate;
import java.util.List;
import br.entity.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ExtratoFinanceiroService {

    private final FiadoRepository fiadoRepository;
    private final PagamentoRepository pagamentoRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final VendaRepository vendaRepository;

    public ExtratoFinanceiroService(
            FiadoRepository fiadoRepository,
            PagamentoRepository pagamentoRepository,
            VendaRepository vendaRepository,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.fiadoRepository = fiadoRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.vendaRepository = vendaRepository;
    }

    public List<MovimentacaoFinanceiraResponse>
    listarMovimentacoes() {

        UUID tenantId =
                usuarioAutenticadoService
                        .tenantIdAtual();

        List<MovimentacaoFinanceiraResponse>
                movimentacoesVenda =

                vendaRepository.findByTenantId(
                        tenantId
                        )
                        .stream()

                        .map(venda ->
                                new MovimentacaoFinanceiraResponse(

                                        "venda",

                                        venda.getClienteNome(),
                                        venda.getValorTotal(),
                                        venda.getDataVenda(),
                                        venda.getUsuario()
                                                .getNome(),
                                        venda.getFormaPagamento(),
                                        venda.getStatus()

                                )
                        )

                        .toList();

        List<MovimentacaoFinanceiraResponse>
                movimentacoesFiado =

                fiadoRepository.findByTenantId(
                        tenantId
                )
                        .stream()
                        .map(fiado ->
                                new MovimentacaoFinanceiraResponse(

                                        "fiado",

                                        fiado.getCliente()
                                                .getNome(),

                                        fiado.getValorTotal(),
                                        fiado.getDataLancamento(),
                                        null,
                                        null,
                                        fiado.getStatus()

                                ))
                        .toList();

        List<MovimentacaoFinanceiraResponse>
                movimentacoesPagamento =

                pagamentoRepository.findByTenantId(
                                tenantId
                        )
                        .stream()
                        .map(pagamento ->
                                new MovimentacaoFinanceiraResponse(

                                        "pagamento",

                                        pagamento.getCliente()
                                                .getNome(),
                                        pagamento.getValorPago(),
                                        pagamento.getDataPagamento(),
                                        pagamento.getUsuario()
                                                .getNome(),
                                        pagamento.getFormaPagamento(),
                                        "confirmado"

                                ))
                        .toList();

        return java.util.stream.Stream
                .of(

                        movimentacoesFiado,

                        movimentacoesPagamento,

                        movimentacoesVenda

                )

                .flatMap(List::stream)

                .sorted(
                        (a, b) ->
                                b.data()
                                        .compareTo(
                                                a.data()
                                        )
                )

                .toList();
    }

    public IndicadoresExtratoResponse
    obterIndicadores() {

        UUID tenantId =
                usuarioAutenticadoService
                        .tenantIdAtual();

        BigDecimal totalRecebido =
                pagamentoRepository
                        .findByTenantId(
                                tenantId
                        )
                        .stream()
                        .map(
                                pagamento ->
                                        pagamento
                                                .getValorPago()
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalFiado =
                fiadoRepository
                        .findByTenantId(
                                tenantId
                        )
                        .stream()
                        .map(
                                fiado ->
                                        fiado.getValorTotal()
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal saldoAberto =
                totalFiado.subtract(
                        totalRecebido
                );

        LocalDate hoje =
                LocalDate.now();

        BigDecimal recebimentosHoje =
                pagamentoRepository
                        .findByTenantId(
                                tenantId
                        )
                        .stream()
                        .filter(
                                pagamento ->
                                        pagamento
                                                .getDataPagamento()
                                                .toLocalDate()
                                                .equals(hoje)
                        )
                        .map(
                                pagamento ->
                                        pagamento
                                                .getValorPago()
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        Integer clientesDevedores =

                (int) fiadoRepository
                        .findByTenantId(
                                tenantId
                        )
                        .stream()
                        .map(
                                fiado ->
                                        fiado.getCliente()
                                                .getId()
                        )
                        .distinct()
                        .count();

        return new IndicadoresExtratoResponse(
                totalRecebido,
                saldoAberto,
                recebimentosHoje,
                clientesDevedores
        );

    }

}
