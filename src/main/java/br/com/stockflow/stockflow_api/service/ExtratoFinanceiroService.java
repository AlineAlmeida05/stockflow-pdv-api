package br.com.stockflow.stockflow_api.service;


import br.com.stockflow.stockflow_api.dto.response.IndicadoresExtratoResponse;
import br.com.stockflow.stockflow_api.dto.response.MovimentacaoFinanceiraResponse;
import br.com.stockflow.stockflow_api.repository.FiadoRepository;
import br.com.stockflow.stockflow_api.repository.PagamentoRepository;
import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;
import br.com.stockflow.stockflow_api.repository.VendaRepository;
import java.time.LocalDate;
import java.util.List;
import br.com.stockflow.stockflow_api.entity.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

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

        Usuario usuarioLogado =
                obterUsuarioLogado();

        List<MovimentacaoFinanceiraResponse>
                movimentacoesVenda =

                vendaRepository
                        .findByTenantId(
                                usuarioLogado
                                        .getTenant()
                                        .getId()
                        )
                        .stream()

                        .filter(venda ->
                                !"cancelada".equalsIgnoreCase(
                                        venda.getStatus()
                                )
                        )

                        .filter(venda ->
                                !"fiado".equalsIgnoreCase(
                                        venda.getFormaPagamento()
                                )
                        )

                        .map(venda ->
                                new MovimentacaoFinanceiraResponse(

                                        "venda",

                                        venda.getClienteNome(),

                                        venda.getValorTotal(),

                                        venda.getDataVenda(),

                                        venda.getUsuario()
                                                .getNome(),

                                        venda.getFormaPagamento()

                                )
                        )

                        .toList();

        List<MovimentacaoFinanceiraResponse>
                movimentacoesFiado =

                fiadoRepository
                        .findByTenantId(
                                usuarioLogado
                                        .getTenant()
                                        .getId()
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
                                        null
                                ))
                        .toList();

        List<MovimentacaoFinanceiraResponse>
                movimentacoesPagamento =

                pagamentoRepository
                        .findByTenantId(
                                usuarioLogado
                                        .getTenant()
                                        .getId()
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
                                        pagamento.getFormaPagamento()
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

        Usuario usuarioLogado =
                obterUsuarioLogado();

        BigDecimal totalRecebido =
                pagamentoRepository
                        .findByTenantId(
                                usuarioLogado
                                        .getTenant()
                                        .getId()
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
                                usuarioLogado
                                        .getTenant()
                                        .getId()
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
                                usuarioLogado
                                        .getTenant()
                                        .getId()
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
                                usuarioLogado
                                        .getTenant()
                                        .getId()
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

    private Usuario obterUsuarioLogado() {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        if (usuarioLogado == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado."
            );

        }

        return usuarioLogado;

    }

}
