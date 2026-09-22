package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.dto.request.FiadoRequest;
import br.com.stockflow.stockflow_api.dto.response.FiadoResponse;
import br.com.stockflow.stockflow_api.entity.Cliente;
import br.com.stockflow.stockflow_api.entity.Fiado;
import br.com.stockflow.stockflow_api.entity.Usuario;
import br.com.stockflow.stockflow_api.exception.RecursoNaoEncontradoException;
import br.com.stockflow.stockflow_api.repository.ClienteRepository;
import br.com.stockflow.stockflow_api.repository.FiadoRepository;
import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FiadoService {

    private final FiadoRepository fiadoRepository;

    private final ClienteRepository clienteRepository;

    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public FiadoService(
            FiadoRepository fiadoRepository,
            ClienteRepository clienteRepository,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.fiadoRepository = fiadoRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioAutenticadoService =
                usuarioAutenticadoService;
    }

    public Fiado salvar(
            FiadoRequest request) {

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

        Fiado fiado = new Fiado();

        fiado.setCliente(
                cliente);

        fiado.setVendaId(
                request.vendaId());

        fiado.setValorTotal(
                request.valorTotal());

        fiado.setDataLancamento(
                LocalDateTime.now());

        fiado.setStatus(
                "pendente");

        fiado.setObservacao(
                request.observacao());

        fiado.setTenant(
                usuarioLogado.getTenant());

        return fiadoRepository.save(
                fiado);
    }

    public List<FiadoResponse> listar() {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        if (usuarioLogado == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado");
        }

        return fiadoRepository
                .findByTenantId(
                        usuarioLogado
                                .getTenant()
                                .getId())
                .stream()
                .map(fiado ->
                        new FiadoResponse(
                                fiado.getId(),
                                fiado.getCliente().getId(),
                                fiado.getCliente().getNome(),
                                fiado.getVendaId(),
                                fiado.getValorTotal(),
                                fiado.getDataLancamento(),
                                fiado.getStatus(),
                                fiado.getObservacao()))
                .toList();
    }
}