package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.entity.Usuario;
import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;
import br.com.stockflow.stockflow_api.service.UsuarioService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import br.com.stockflow.stockflow_api.dto.AlterarSenhaRequest;
import jakarta.validation.Valid;
import br.com.stockflow.stockflow_api.dto.UsuarioCreateRequest;
import br.com.stockflow.stockflow_api.dto.UsuarioUpdateRequest;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

        private final UsuarioService usuarioService;

        private final UsuarioAutenticadoService usuarioAutenticadoService;

        public UsuarioController(
                        UsuarioService usuarioService,
                        UsuarioAutenticadoService usuarioAutenticadoService) {

                this.usuarioService = usuarioService;
                this.usuarioAutenticadoService = usuarioAutenticadoService;

        }

        @GetMapping
        public List<Usuario> listarTodos() {
                var usuario = usuarioAutenticadoService
                                .usuarioLogado();

                System.out.println(
                                "USUARIO AUTENTICADO: "
                                                + usuario.getEmail());

                System.out.println(
                                "PERFIL: "
                                                + usuario.getPerfil());
                return usuarioService
                                .listarTodos();

        }

        @PostMapping
        public Usuario salvar(
                @Valid
                @RequestBody UsuarioCreateRequest request) {

                return usuarioService
                        .salvar(request);

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
                @Valid
                @RequestBody UsuarioUpdateRequest request) {

                return usuarioService
                        .atualizar(
                                id,
                                request);

        }


        @GetMapping("/tenant/{tenantId}")
        public List<Usuario> listarPorTenant(
                        @PathVariable UUID tenantId) {

                return usuarioService
                                .listarPorTenant(
                                                tenantId);

        }

        @PutMapping("/alterar-senha")
        public ResponseEntity<Void> alterarSenha(
                @Valid
                @RequestBody AlterarSenhaRequest request) {

                usuarioService.alterarSenha(
                        request);

                return ResponseEntity
                        .ok()
                        .build();

        }

}