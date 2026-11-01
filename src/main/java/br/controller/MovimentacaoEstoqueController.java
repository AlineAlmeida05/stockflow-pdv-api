package br.controller;

import br.entity.MovimentacaoEstoque;
import br.service.MovimentacaoEstoqueService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import br.dto.request.MovimentacaoEstoqueRequest;
import br.dto.response.MovimentacaoEstoqueResponse;

@RestController
@RequestMapping("/api/movimentacoes-estoque")
public class MovimentacaoEstoqueController {

    private final MovimentacaoEstoqueService service;

    public MovimentacaoEstoqueController(
            MovimentacaoEstoqueService service) {

        this.service = service;

    }

    @GetMapping
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','ESTOQUISTA')"
    )
    public List<MovimentacaoEstoqueResponse> listar() {

        return service.listar();

    }

    @PostMapping
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','ESTOQUISTA')"
    )
    public MovimentacaoEstoque salvar(
            @Valid
            @RequestBody MovimentacaoEstoqueRequest request) {

        return service.salvar(request);

    }

}