package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.dto.ClienteRequest;
import br.com.stockflow.stockflow_api.dto.ClienteResponse;
import br.com.stockflow.stockflow_api.entity.Cliente;
import br.com.stockflow.stockflow_api.service.ClienteService;

import org.springframework.web.bind.annotation.*;
import br.com.stockflow.stockflow_api.dto.ClienteResumoResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(
            ClienteService clienteService) {

        this.clienteService = clienteService;
    }

    @PostMapping
    public Cliente salvar(
            @RequestBody ClienteRequest request) {

        return clienteService.salvar(
                request);
    }

    @PutMapping("/{id}")
    public Cliente atualizar(
            @PathVariable UUID id,
            @RequestBody ClienteRequest request) {

        return clienteService.atualizar(
                id,
                request);
    }

    @GetMapping
    public List<ClienteResponse> listar() {

        return clienteService.listar();
    }

    @DeleteMapping("/{id}")
    public void inativar(
            @PathVariable UUID id) {

        clienteService.inativar(id);
    }

    @GetMapping("/{id}/resumo")
    public ClienteResumoResponse obterResumo(
            @PathVariable UUID id) {

        return clienteService
                .obterResumo(id);
    }

}