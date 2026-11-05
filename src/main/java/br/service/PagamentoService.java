package br.service;

import br.dto.request.PagamentoRequest;
import br.dto.response.PagamentoResponse;
import br.entity.Cliente;
import br.entity.Fiado;
import br.entity.Pagamento;
import br.entity.Usuario;
import br.exception.RecursoNaoEncontradoException;
import br.exception.RegraNegocioException;
import br.repository.ClienteRepository;
import br.repository.FiadoRepository;
import br.repository.PagamentoRepository;
import br.security.UsuarioAutenticadoService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final FiadoRepository fiadoRepository;
    private final AuditoriaService auditoriaService;

    public PagamentoService(
            PagamentoRepository pagamentoRepository,
            ClienteRepository clienteRepository,
            FiadoRepository fiadoRepository,
            UsuarioAutenticadoService usuarioAutenticadoService,
            AuditoriaService auditoriaService) {

        this.pagamentoRepository = pagamentoRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.fiadoRepository = fiadoRepository;
        this.auditoriaService = auditoriaService;
    }

    public Pagamento salvar(
            PagamentoRequest request) {

        Usuario usuarioLogado = obterUsuarioLogado();

        var tenantAtual =
                usuarioAutenticadoService
                        .tenantAtual();

        UUID tenantId =
                usuarioAutenticadoService
                        .tenantIdAtual();


        Cliente cliente =
                clienteRepository
                        .findByIdAndTenantId(
                                request.clienteId(),
                                tenantId
                        )
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Cliente não encontrado."
                                ));

        List<Fiado> fiadosPendentes =
                fiadoRepository
                        .findByClienteIdAndTenantIdAndStatus(
                                cliente.getId(),
                                tenantId,
                                "pendente"
                        );


        BigDecimal totalFiado =
                fiadoRepository
                        .findByClienteIdAndTenantIdAndStatus(
                                cliente.getId(),
                                tenantId,
                                "pendente"
                        )
                        .stream()
                        .map(Fiado::getValorTotal)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        BigDecimal totalPago =
                pagamentoRepository
                        .findByClienteIdAndTenantId(
                                cliente.getId(),
                                tenantId
                        )
                        .stream()
                        .map(Pagamento::getValorPago)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal saldoDevedor = totalFiado.subtract(totalPago);

        if (
                request.valorPago()
                        .compareTo(saldoDevedor)
                        > 0
        ) {

            throw new RegraNegocioException(
                    "Valor do pagamento superior ao saldo devedor."
            );
        }

        Pagamento pagamento = new Pagamento();

        pagamento.setCliente(cliente);

        pagamento.setValorPago(
                request
                        .valorPago());

        pagamento.setDataPagamento(
                LocalDateTime
                        .now());

        pagamento.setObservacao(
                request
                        .observacao());

        pagamento.setFormaPagamento(
                request
                        .formaPagamento());

        pagamento.setTenant(
                usuarioAutenticadoService
                        .tenantAtual()
        );

        pagamento.setUsuario(usuarioLogado);

        Pagamento pagamentoSalvo =
                pagamentoRepository.save(
                        pagamento);

        BigDecimal novoSaldo =
                saldoDevedor.subtract(
                        request.valorPago()
                );

        if (
                novoSaldo.compareTo(
                        BigDecimal.ZERO
                ) == 0
        ) {

            fiadosPendentes.forEach(
                    fiado -> fiado.setStatus(
                            "quitado"
                    )
            );

            fiadoRepository.saveAll(
                    fiadosPendentes
            );
        }

        auditoriaService.registrar(
                tenantAtual.getId(),
                tenantAtual.getNome(),
                usuarioLogado.getId(),
                usuarioLogado.getNome(),
                "PAGAMENTO_REALIZADO",
                "Pagamento",
                pagamentoSalvo.getId().toString(),
                "Pagamento de R$ "
                        + pagamentoSalvo.getValorPago()
                        + " para o cliente "
                        + cliente.getNome()
        );

        return pagamentoSalvo;
    }

    public List<PagamentoResponse> listar() {

        UUID tenantId =
                usuarioAutenticadoService
                        .tenantIdAtual();

        return pagamentoRepository
                .findByTenantId(
                        tenantId)
                .stream()
                .map(pagamento ->
                        new PagamentoResponse(
                                pagamento.getId(),
                                pagamento.getCliente().getId(),
                                pagamento.getCliente().getNome(),
                                pagamento.getValorPago(),
                                pagamento.getUsuario().getNome(),
                                pagamento.getDataPagamento(),
                                pagamento.getObservacao(),
                                pagamento.getFormaPagamento()))
                .toList();
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