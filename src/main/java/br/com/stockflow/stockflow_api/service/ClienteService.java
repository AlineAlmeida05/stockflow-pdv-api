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

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public ClienteService(
            ClienteRepository clienteRepository,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.clienteRepository = clienteRepository;
        this.usuarioAutenticadoService =
                usuarioAutenticadoService;
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

        Cliente cliente =
                clienteRepository
                        .findById(id)
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

        Cliente cliente =
                clienteRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Cliente não encontrado."));

        cliente.setAtivo(false);

        clienteRepository.save(
                cliente);
    }
}