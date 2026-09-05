package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.entity.Usuario;
import br.com.stockflow.stockflow_api.service.UsuarioService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(
            UsuarioService usuarioService) {

        this.usuarioService = usuarioService;

    }

    @GetMapping
    public List<Usuario> listarTodos() {

        return usuarioService
                .listarTodos();

    }

    @PostMapping
    public Usuario salvar(
            @RequestBody Usuario usuario) {

        return usuarioService
                .salvar(usuario);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable UUID id) {

        usuarioService.excluir(id);

        return ResponseEntity
                .noContent()
                .build();

    }

    @PutMapping("/{id}")
    public Usuario atualizar(
            @PathVariable UUID id,
            @RequestBody Usuario usuario) {

        return usuarioService
                .atualizar(
                        id,
                        usuario);

    }

    @GetMapping("/tenant/{tenantId}")
    public List<Usuario> listarPorTenant(
            @PathVariable UUID tenantId) {

        return usuarioService
                .listarPorTenant(
                        tenantId);

    }

}