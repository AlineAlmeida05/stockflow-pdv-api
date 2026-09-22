package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.dto.request.PagamentoRequest;
import br.com.stockflow.stockflow_api.dto.response.PagamentoResponse;
import br.com.stockflow.stockflow_api.entity.Cliente;
import br.com.stockflow.stockflow_api.entity.Pagamento;
import br.com.stockflow.stockflow_api.entity.Usuario;
import br.com.stockflow.stockflow_api.exception.RecursoNaoEncontradoException;
import br.com.stockflow.stockflow_api.repository.ClienteRepository;
import br.com.stockflow.stockflow_api.repository.PagamentoRepository;
import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import br.com.stockflow.stockflow_api.entity.Fiado;
import br.com.stockflow.stockflow_api.exception.RegraNegocioException;
import br.com.stockflow.stockflow_api.repository.FiadoRepository;

import java.math.BigDecimal;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final FiadoRepository fiadoRepository;

    public PagamentoService(
            PagamentoRepository pagamentoRepository,
            ClienteRepository clienteRepository,
            FiadoRepository fiadoRepository,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.pagamentoRepository = pagamentoRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.fiadoRepository = fiadoRepository;
    }

    public Pagamento salvar(
            PagamentoRequest request) {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        if (usuarioLogado == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado");
        }

        Cliente cliente =
                clienteRepository
                        .findByIdAndTenantId(
                                request.clienteId(),
                                usuarioLogado
                                        .getTenant()
                                        .getId()
                        )
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Cliente não encontrado."
                                ));

        BigDecimal totalFiado =
                fiadoRepository
                        .findByClienteIdAndTenantId(
                                cliente.getId(),
                                usuarioLogado
                                        .getTenant()
                                        .getId()
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
                                usuarioLogado
                                        .getTenant()
                                        .getId()
                        )
                        .stream()
                        .map(Pagamento::getValorPago)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal saldoDevedor =
                totalFiado.subtract(totalPago);

        if (
                request.valorPago()
                        .compareTo(saldoDevedor)
                        > 0
        ) {

            throw new RegraNegocioException(
                    "Valor do pagamento superior ao saldo devedor."
            );
        }

        Pagamento pagamento =
                new Pagamento();

        pagamento.setCliente(
                cliente);

        pagamento.setValorPago(
                request.valorPago());

        pagamento.setDataPagamento(
                LocalDateTime.now());

        pagamento.setObservacao(
                request.observacao());

        pagamento.setFormaPagamento(
                request.formaPagamento());

        pagamento.setTenant(
                usuarioLogado.getTenant());

        pagamento.setUsuario(
                usuarioLogado);

        return pagamentoRepository.save(
                pagamento);
    }

    public List<PagamentoResponse> listar() {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        if (usuarioLogado == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado");
        }

        return pagamentoRepository
                .findByTenantId(
                        usuarioLogado
                                .getTenant()
                                .getId())
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
}