package br.service;

import br.dto.request.ClienteRequest;
import br.dto.response.ClienteResponse;
import br.dto.response.ClienteResumoResponse;
import br.entity.*;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final FiadoRepository fiadoRepository;
    private final PagamentoRepository pagamentoRepository;
    private final AuditoriaService auditoriaService;

    public ClienteService(
            ClienteRepository clienteRepository,
            FiadoRepository fiadoRepository,
            PagamentoRepository pagamentoRepository,
            UsuarioAutenticadoService usuarioAutenticadoService,
            AuditoriaService auditoriaService) {

        this.clienteRepository = clienteRepository;
        this.fiadoRepository = fiadoRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.auditoriaService = auditoriaService;
    }

    public ClienteResponse salvar(
            ClienteRequest request) {

        Tenant tenantAtual =
                usuarioAutenticadoService
                        .tenantAtual();

        Usuario usuarioLogado = obterUsuarioLogado();

        Cliente cliente = new Cliente();

        cliente.setNome(request.nome());

        cliente.setTelefone(request.telefone());

        cliente.setAtivo(true);

        cliente.setDataCadastro(LocalDateTime.now());

        if (request.limiteCredito() == null) {

            cliente.setLimiteCredito(BigDecimal.valueOf(300)
            );

        } else {

            cliente.setLimiteCredito(
                    request.limiteCredito()
            );

        }

        cliente.setObservacao(request.observacao());

        cliente.setTenant(
                usuarioAutenticadoService.tenantAtual()
        );

        Cliente clienteSalvo = clienteRepository.save(cliente);

        auditoriaService.registrar(
                tenantAtual.getId(),
                tenantAtual.getNome(),
                usuarioLogado.getId(),
                usuarioLogado.getNome(),
                "CLIENTE_CRIADO",
                "Cliente",
                clienteSalvo.getId().toString(),
                "Cliente " + clienteSalvo.getNome() + " criado."
        );

        return montarResponse(clienteSalvo);
    }

    public List<ClienteResponse> listar() {

        UUID tenantId =
                usuarioAutenticadoService
                        .tenantIdAtual();

        return clienteRepository
                .findByTenantId(
                        tenantId)
                .stream()
                .map(this::montarResponse)
                .toList();
    }

    public ClienteResponse atualizar(
            UUID id,
            ClienteRequest request) {

        Tenant tenantAtual =
                usuarioAutenticadoService
                        .tenantAtual();

        Usuario usuarioLogado = obterUsuarioLogado();

        UUID tenantId =
                usuarioAutenticadoService
                        .tenantIdAtual();

        Cliente cliente =
                clienteRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId)
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Cliente não encontrado."));

        cliente.setNome(
                request.nome());

        cliente.setTelefone(
                request.telefone());

        cliente.setLimiteCredito(
                request.limiteCredito());

        cliente.setObservacao(
                request.observacao());

        Cliente clienteAtualizado = clienteRepository.save(cliente);

        auditoriaService.registrar(
                tenantAtual.getId(),
                tenantAtual.getNome(),
                usuarioLogado.getId(),
                usuarioLogado.getNome(),
                "CLIENTE_EDITADO",
                "Cliente",
                clienteAtualizado.getId().toString(),
                "Cliente " + clienteAtualizado.getNome() + " atualizado."
        );

        return montarResponse(clienteAtualizado);

    }

    public void inativar(
            UUID id) {

        Tenant tenantAtual =
                usuarioAutenticadoService
                        .tenantAtual();

        Usuario usuarioLogado = obterUsuarioLogado();

        UUID tenantId =
                usuarioAutenticadoService
                        .tenantIdAtual();

        Cliente cliente =
                clienteRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId)
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Cliente não encontrado."));

        BigDecimal totalFiado =
                fiadoRepository
                        .findByClienteIdAndTenantId(
                                cliente.getId(),
                                tenantId
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

        BigDecimal saldoDevedor =
                totalFiado.subtract(
                        totalPago
                );

        if (
                saldoDevedor.compareTo(
                        BigDecimal.ZERO
                ) > 0
        ) {

            throw new RegraNegocioException(
                    "Cliente possui débitos pendentes e não pode ser inativado."
            );
        }

        if (Boolean.FALSE.equals(
                cliente.getAtivo())) {

            throw new RegraNegocioException(
                    "Cliente já está inativo."
            );

        }

        cliente.setAtivo(false);

        clienteRepository.save(cliente);

        auditoriaService.registrar(
                tenantAtual.getId(),
                tenantAtual.getNome(),
                usuarioLogado.getId(),
                usuarioLogado.getNome(),
                "CLIENTE_INATIVADO",
                "Cliente",
                cliente.getId().toString(),
                "Cliente " + cliente.getNome() + " inativado."
        );

    }

    public ClienteResumoResponse obterResumo(
            UUID clienteId) {

        UUID tenantId =
                usuarioAutenticadoService
                        .tenantIdAtual();

        Cliente cliente =
                clienteRepository
                        .findByIdAndTenantId(
                                clienteId,
                                tenantId)
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Cliente não encontrado."));

        List<Fiado> fiados =
                fiadoRepository
                        .findByClienteIdAndTenantIdAndStatus(
                                clienteId,
                                tenantId,
                                "pendente"
                        );

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
                                tenantId);

        BigDecimal totalPago =
                pagamentos.stream()
                        .map(Pagamento::getValorPago)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add);

        BigDecimal saldoDevedor =
                totalFiado.subtract(
                        totalPago);

        int diasSemPagamento = 0;

        if (
                saldoDevedor.compareTo(
                        BigDecimal.ZERO
                ) <= 0
        ) {

            diasSemPagamento = 0;

        }

        BigDecimal creditoDisponivel =
                cliente.getLimiteCredito()
                        .subtract(
                                saldoDevedor);



        if (!pagamentos.isEmpty()) {

            LocalDateTime ultimoPagamento =
                    pagamentos.stream()
                            .map(Pagamento::getDataPagamento)
                            .max(LocalDateTime::compareTo)
                            .orElse(LocalDateTime.now());

            diasSemPagamento =
                    (int) Duration
                            .between(
                                    ultimoPagamento,
                                    LocalDateTime.now()
                            )
                            .toDays();

        } else if (!fiados.isEmpty()) {

            LocalDateTime fiadoMaisAntigo =
                    fiados.stream()
                            .map(Fiado::getDataLancamento)
                            .min(LocalDateTime::compareTo)
                            .orElse(LocalDateTime.now());

            diasSemPagamento =
                    (int) Duration
                            .between(
                                    fiadoMaisAntigo,
                                    LocalDateTime.now()
                            )
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

    private ClienteResponse montarResponse(
            Cliente cliente
    ) {

        return new ClienteResponse(

                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone(),
                cliente.getAtivo(),
                cliente.getDataCadastro(),
                cliente.getLimiteCredito(),
                cliente.getObservacao()

        );

    }

    public void reativar(
            UUID id) {

        Tenant tenantAtual =
                usuarioAutenticadoService
                        .tenantAtual();

        Usuario usuarioLogado = obterUsuarioLogado();

        UUID tenantId =
                usuarioAutenticadoService
                        .tenantIdAtual();

        Cliente cliente =
                clienteRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId)
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Cliente não encontrado."));

        if (Boolean.TRUE.equals(
                cliente.getAtivo())) {

            throw new RegraNegocioException(
                    "Cliente já está ativo."
            );

        }

        cliente.setAtivo(true);

        clienteRepository.save(cliente);

        auditoriaService.registrar(
                tenantAtual.getId(),
                tenantAtual.getNome(),
                usuarioLogado.getId(),
                usuarioLogado.getNome(),
                "CLIENTE_REATIVADO",
                "Cliente",
                cliente.getId().toString(),
                "Cliente " + cliente.getNome() + " reativado."
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

