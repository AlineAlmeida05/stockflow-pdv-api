package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.dto.response.ProdutoResponse;
import br.com.stockflow.stockflow_api.entity.Produto;
import br.com.stockflow.stockflow_api.service.ProdutoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;
import br.com.stockflow.stockflow_api.dto.request.ProdutoCreateRequest;
import br.com.stockflow.stockflow_api.dto.request.ProdutoUpdateRequest;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
        private final ProdutoService produtoService;

        public ProdutoController(
                        ProdutoService produtoService) {

                this.produtoService = produtoService;

        }

        @GetMapping
        @PreAuthorize(
                "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','OPERADOR_CAIXA','ESTOQUISTA')"
        )
        public ResponseEntity<List<ProdutoResponse>> listar() {

                return ResponseEntity.ok(
                                produtoService.listar());

        }

        @GetMapping("/{id}")
        @PreAuthorize(
                "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','OPERADOR_CAIXA','ESTOQUISTA')"
        )
        public ResponseEntity<ProdutoResponse> buscarPorId(
                @PathVariable UUID id) {

                return ResponseEntity.ok(
                                produtoService.buscarPorId(id));

        }

        @PostMapping
        @PreAuthorize(
                "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','ESTOQUISTA')"
        )
        ResponseEntity<ProdutoResponse> salvar(
                @Valid
                @RequestBody ProdutoCreateRequest request) {

                return ResponseEntity.ok(
                        produtoService.salvar(request));

        }

        @PutMapping("/{id}")
        @PreAuthorize(
                "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','ESTOQUISTA')"
        )
        ResponseEntity<ProdutoResponse> atualizar(
                @PathVariable UUID id,
                @Valid
                @RequestBody ProdutoUpdateRequest request) {

                return ResponseEntity.ok(
                        produtoService.atualizar(
                                id,
                                request));

        }

        @DeleteMapping("/{id}")
        @PreAuthorize(
                "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO')"
        )
        public ResponseEntity<Void> excluir(
                @PathVariable UUID id) {

                produtoService.excluir(id);

                return ResponseEntity
                                .noContent()
                                .build();

        }

        @PutMapping("/{id}/reativar")
        @PreAuthorize(
                "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO')"
        )
        public void reativar(
                @PathVariable UUID id) {

                produtoService.reativar(
                                id);

        }

}