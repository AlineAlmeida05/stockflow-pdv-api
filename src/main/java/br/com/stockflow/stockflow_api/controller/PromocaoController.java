package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.dto.response.PromocaoPainelResponse;
import br.com.stockflow.stockflow_api.dto.request.PromocaoRequest;
import br.com.stockflow.stockflow_api.entity.Promocao;
import br.com.stockflow.stockflow_api.service.PromocaoService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import br.com.stockflow.stockflow_api.dto.response.ProdutoPromocaoResponse;

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
            @Valid
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

    @GetMapping("/painel")
    public PromocaoPainelResponse
    listarPainel() {

        return promocaoService
                .listarPainel();

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

    @GetMapping("/candidatos")
    public List<ProdutoPromocaoResponse>
    listarCandidatos() {

        return promocaoService
                .listarCandidatos();

    }



}