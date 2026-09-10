package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.dto.ClienteRequest;
import br.com.stockflow.stockflow_api.dto.ClienteResponse;
import br.com.stockflow.stockflow_api.entity.Cliente;
import br.com.stockflow.stockflow_api.entity.Usuario;
import br.com.stockflow.stockflow_api.repository.ClienteRepository;
import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import br.com.stockflow.stockflow_api.dto.ClienteResumoResponse;
import br.com.stockflow.stockflow_api.entity.Fiado;
import br.com.stockflow.stockflow_api.entity.Pagamento;
import br.com.stockflow.stockflow_api.repository.FiadoRepository;
import br.com.stockflow.stockflow_api.repository.PagamentoRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    private final UsuarioAutenticadoService usuarioAutenticadoService;

    private final FiadoRepository fiadoRepository;

    private final PagamentoRepository pagamentoRepository;

    public ClienteService(
            ClienteRepository clienteRepository,
            FiadoRepository fiadoRepository,
            PagamentoRepository pagamentoRepository,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.clienteRepository = clienteRepository;
        this.fiadoRepository = fiadoRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    public Cliente salvar(
            ClienteRequest request) {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        if (usuarioLogado == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado");
        }

        if (request.getNome() == null
                || request.getNome().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nome do cliente é obrigatório.");
        }
        if (request.getLimiteCredito() != null
                && request.getLimiteCredito()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Limite de crédito não pode ser negativo.");
        }
        if (request.getTelefone() == null
                || request.getTelefone().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Telefone é obrigatório.");
        }


        Cliente cliente = new Cliente();

        cliente.setNome(
                request.getNome());

        cliente.setTelefone(
                request.getTelefone());

        cliente.setAtivo(true);

        cliente.setDataCadastro(
                LocalDateTime.now());

        cliente.setLimiteCredito(
                request.getLimiteCredito() != null
                        ? request.getLimiteCredito()
                        : BigDecimal.valueOf(300));

        cliente.setObservacao(
                request.getObservacao());

        cliente.setTenant(
                usuarioLogado.getTenant());

        return clienteRepository.save(
                cliente);
    }

    public List<ClienteResponse> listar() {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        if (usuarioLogado == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado");
        }

        return clienteRepository
                .findByTenantIdAndAtivoTrue(
                        usuarioLogado
                                .getTenant()
                                .getId())
                .stream()
                .map(cliente ->
                        new ClienteResponse(
                                cliente.getId(),
                                cliente.getNome(),
                                cliente.getTelefone(),
                                cliente.getAtivo(),
                                cliente.getDataCadastro(),
                                cliente.getLimiteCredito(),
                                cliente.getObservacao()))
                .toList();
    }

    public Cliente atualizar(
            UUID id,
            ClienteRequest request) {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        if (usuarioLogado == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado");
        }
        if (request.getLimiteCredito() != null
                && request.getLimiteCredito()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Limite de crédito não pode ser negativo.");
        }
        if (request.getTelefone() == null
                || request.getTelefone().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Telefone é obrigatório.");
        }


        Cliente cliente =
                clienteRepository
                        .findByIdAndTenantId(
                                id,
                                usuarioLogado
                                        .getTenant()
                                        .getId())
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Cliente não encontrado."));

        cliente.setNome(
                request.getNome());

        cliente.setTelefone(
                request.getTelefone());

        cliente.setLimiteCredito(
                request.getLimiteCredito());

        cliente.setObservacao(
                request.getObservacao());

        return clienteRepository.save(
                cliente);
    }

    public void inativar(
            UUID id) {

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
                                id,
                                usuarioLogado
                                        .getTenant()
                                        .getId())
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Cliente não encontrado."));

        cliente.setAtivo(false);

        clienteRepository.save(
                cliente);
    }

    public ClienteResumoResponse obterResumo(
            UUID clienteId) {
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
                        .findByIdAndTenantIdAndAtivoTrue(
                                clienteId,
                                usuarioLogado
                                        .getTenant()
                                        .getId())
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Cliente não encontrado."));
        List<Fiado> fiados =
                fiadoRepository
                        .findByClienteIdAndTenantId(
                                clienteId,
                                usuarioLogado
                                        .getTenant()
                                        .getId());

        BigDecimal totalFiado =
                fiados.stream()
                        .map(Fiado::getValorTotal)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add);

        List<Pagamento> pagamentos =
                pagamentoRepository
                        .findByClienteIdAndTenantId(
                                clienteId,
                                usuarioLogado
                                        .getTenant()
                                        .getId());

        BigDecimal totalPago =
                pagamentos.stream()
                        .map(Pagamento::getValorPago)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add);

        BigDecimal saldoDevedor =
                totalFiado.subtract(
                        totalPago);

        BigDecimal creditoDisponivel =
                cliente.getLimiteCredito()
                        .subtract(
                                saldoDevedor);

        int diasSemPagamento = 0;
        if (!fiados.isEmpty()) {

            LocalDateTime fiadoMaisAntigo =
                    fiados.stream()
                            .map(Fiado::getDataLancamento)
                            .min(LocalDateTime::compareTo)
                            .orElse(LocalDateTime.now());

            diasSemPagamento =
                    (int) java.time.Duration
                            .between(
                                    fiadoMaisAntigo,
                                    LocalDateTime.now())
                            .toDays();
        }

        String status;

        if (saldoDevedor.compareTo(
                BigDecimal.ZERO) <= 0) {

            status = "EM_DIA";

        } else if (
                saldoDevedor.compareTo(
                        cliente.getLimiteCredito()) > 0) {

            status = "LIMITE_EXCEDIDO";

        } else if (diasSemPagamento >= 30) {

            status = "INADIMPLENTE";

        } else {

            status = "DEVEDOR";
        }

        return new ClienteResumoResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getLimiteCredito(),
                saldoDevedor,
                creditoDisponivel,
                status,
                diasSemPagamento);
    }
}

