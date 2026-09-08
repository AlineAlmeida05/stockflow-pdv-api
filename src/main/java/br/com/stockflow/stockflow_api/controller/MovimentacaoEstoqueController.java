package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.entity.MovimentacaoEstoque;
import br.com.stockflow.stockflow_api.service.MovimentacaoEstoqueService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import br.com.stockflow.stockflow_api.dto.MovimentacaoEstoqueRequest;
import br.com.stockflow.stockflow_api.dto.MovimentacaoEstoqueResponse;

@RestController
@RequestMapping("/api/movimentacoes-estoque")
public class MovimentacaoEstoqueController {

    private final MovimentacaoEstoqueService service;

    public MovimentacaoEstoqueController(
            MovimentacaoEstoqueService service) {

        this.service = service;

    }

    @GetMapping
    public List<MovimentacaoEstoqueResponse> listar() {

        return service.listar();

    }

    @PostMapping
    public MovimentacaoEstoque salvar(
            @RequestBody MovimentacaoEstoqueRequest request) {

        return service.salvar(
                request);

    }

}