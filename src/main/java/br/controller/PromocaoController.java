package br.controller;

import br.dto.response.PromocaoPainelResponse;
import br.dto.request.PromocaoRequest;
import br.entity.Promocao;
import br.service.PromocaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import br.dto.response.ProdutoPromocaoResponse;

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
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','OPERADOR_CAIXA','ESTOQUISTA')"
    )
    public List<Promocao> listar() {

        return promocaoService.listar();
    }

    @GetMapping("/ativas")
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','OPERADOR_CAIXA','ESTOQUISTA')"
    )
    public List<Promocao> listarAtivas() {

        return promocaoService.listarAtivas();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE')"
    )
    public Promocao criar(
            @Valid
            @RequestBody
            PromocaoRequest request) {

        return promocaoService
                .criarPromocao(request);
    }

    @PatchMapping("/{id}/encerrar")
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE')"
    )
    public Promocao encerrar(
            @PathVariable
            UUID id) {

        return promocaoService
                .encerrarPromocao(id);
    }

    @GetMapping("/painel")
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE')"
    )
    public PromocaoPainelResponse listarPainel() {

        return promocaoService
                .listarPainel();

    }

    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','OPERADOR_CAIXA','ESTOQUISTA')"
    )
    public Promocao buscarPorId(
            @PathVariable
            UUID id) {

        return promocaoService
                .buscarPorId(id);
    }

    @GetMapping("/produto/{produtoId}")
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','OPERADOR_CAIXA','ESTOQUISTA')"
    )
    public List<Promocao> listarPorProduto(
            @PathVariable
            UUID produtoId) {

        return promocaoService
                .listarPorProduto(
                        produtoId
                );
    }

    @GetMapping("/candidatos")
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE')"
    )
    public List<ProdutoPromocaoResponse> listarCandidatos() {

        return promocaoService
                .listarCandidatos();

    }



}