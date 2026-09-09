package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.dto.PagamentoRequest;
import br.com.stockflow.stockflow_api.dto.PagamentoResponse;
import br.com.stockflow.stockflow_api.entity.Cliente;
import br.com.stockflow.stockflow_api.entity.Pagamento;
import br.com.stockflow.stockflow_api.entity.Usuario;
import br.com.stockflow.stockflow_api.repository.ClienteRepository;
import br.com.stockflow.stockflow_api.repository.PagamentoRepository;
import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;

    private final ClienteRepository clienteRepository;

    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public PagamentoService(
            PagamentoRepository pagamentoRepository,
            ClienteRepository clienteRepository,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.pagamentoRepository = pagamentoRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioAutenticadoService =
                usuarioAutenticadoService;
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
                        .findById(
                                request.getClienteId())
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Cliente não encontrado."));

        Pagamento pagamento =
                new Pagamento();

        pagamento.setCliente(
                cliente);

        pagamento.setValorPago(
                request.getValorPago());

        pagamento.setDataPagamento(
                LocalDateTime.now());

        pagamento.setObservacao(
                request.getObservacao());

        pagamento.setFormaPagamento(
                request.getFormaPagamento());

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