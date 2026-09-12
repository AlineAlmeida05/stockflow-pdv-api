package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.dto.PromocaoRequest;
import br.com.stockflow.stockflow_api.entity.Promocao;
import br.com.stockflow.stockflow_api.service.PromocaoService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/promocoes")
public class PromocaoController {

    private final PromocaoService promocaoService;

    public PromocaoController(
            PromocaoService promocaoService) {

        this.promocaoService =
                promocaoService;
    }

    @GetMapping
    public List<Promocao> listar() {

        return promocaoService.listar();
    }

    @GetMapping("/ativas")
    public List<Promocao> listarAtivas() {

        return promocaoService.listarAtivas();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Promocao criar(
            @RequestBody
            PromocaoRequest request) {

        return promocaoService
                .criarPromocao(request);
    }

    @PatchMapping("/{id}/encerrar")
    public Promocao encerrar(
            @PathVariable
            UUID id) {

        return promocaoService
                .encerrarPromocao(id);
    }

    @GetMapping("/{id}")
    public Promocao buscarPorId(
            @PathVariable
            UUID id) {

        return promocaoService
                .buscarPorId(id);
    }

    @GetMapping("/produto/{produtoId}")
    public List<Promocao> listarPorProduto(
            @PathVariable
            UUID produtoId) {

        return promocaoService
                .listarPorProduto(
                        produtoId
                );
    }
}