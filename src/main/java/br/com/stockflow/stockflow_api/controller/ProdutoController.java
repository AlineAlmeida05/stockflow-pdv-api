package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.entity.Produto;
import br.com.stockflow.stockflow_api.service.ProdutoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    private final ProdutoService produtoService;

    public ProdutoController(
        ProdutoService produtoService) {

    this.produtoService = produtoService;

}

@GetMapping
public ResponseEntity<List<Produto>> listar() {

    return ResponseEntity.ok(
            produtoService.listar()
    );

}

@GetMapping("/{id}")
public ResponseEntity<Produto> buscarPorId(
        @PathVariable UUID id) {

    return ResponseEntity.ok(
            produtoService.buscarPorId(id)
    );

}

@PostMapping
public ResponseEntity<Produto> salvar(
        @RequestBody Produto produto) {

    return ResponseEntity.ok(
            produtoService.salvar(produto)
    );

}

@PutMapping("/{id}")
public ResponseEntity<Produto> atualizar(
        @PathVariable UUID id,
        @RequestBody Produto produto) {

    return ResponseEntity.ok(
            produtoService.atualizar(
                    id,
                    produto
            )
    );

}

@DeleteMapping("/{id}")
public ResponseEntity<Void> excluir(
        @PathVariable UUID id) {

    produtoService.excluir(id);

    return ResponseEntity
            .noContent()
            .build();

}


}