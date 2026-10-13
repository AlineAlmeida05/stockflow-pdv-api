package br.controller;

import br.dto.request.ClienteRequest;
import br.dto.response.ClienteResponse;
import br.service.ClienteService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import br.dto.response.ClienteResumoResponse;
import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(
            ClienteService clienteService) {

        this.clienteService = clienteService;
    }

    @PostMapping
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','OPERADOR_CAIXA')"
    )
    public ClienteResponse salvar(
            @Valid
            @RequestBody ClienteRequest request) {

        return clienteService.salvar(
                request);
    }

    @PutMapping("/{id}")
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','OPERADOR_CAIXA')"
    )
    public ClienteResponse atualizar(
            @PathVariable UUID id,
            @Valid
            @RequestBody ClienteRequest request) {

        return clienteService.atualizar(
                id,
                request);
    }

    @GetMapping
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','OPERADOR_CAIXA','ESTOQUISTA')"
    )
    public List<ClienteResponse> listar() {

        return clienteService.listar();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE')"
    )
    public void inativar(
            @PathVariable UUID id) {

        clienteService.inativar(id);
    }

    @GetMapping("/{id}/resumo")
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','OPERADOR_CAIXA','ESTOQUISTA')"
    )
    public ClienteResumoResponse obterResumo(
            @PathVariable UUID id) {

        return clienteService
                .obterResumo(id);
    }

    @PatchMapping("/{id}/reativar")
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE')"
    )
    public void reativar(
            @PathVariable UUID id) {

        clienteService.reativar(id);

    }

}