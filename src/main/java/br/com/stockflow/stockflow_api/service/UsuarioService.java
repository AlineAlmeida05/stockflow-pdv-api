package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.entity.Usuario;
import br.com.stockflow.stockflow_api.repository.UsuarioRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

        private final UsuarioRepository usuarioRepository;

        public UsuarioService(
                        UsuarioRepository usuarioRepository) {

                this.usuarioRepository = usuarioRepository;

        }

        public List<Usuario> listarTodos() {

                return usuarioRepository
                                .findAll();

        }

        public Usuario salvar(
                        Usuario usuario) {

                usuarioRepository
                                .findByEmail(
                                                usuario.getEmail())
                                .ifPresent(u -> {

                                        throw new RuntimeException(
                                                        "Já existe um usuário com este e-mail.");

                                });

                return usuarioRepository
                                .save(usuario);

        }

        public void excluir(
                        UUID id) {

                usuarioRepository
                                .deleteById(id);

        }

        public Usuario atualizar(
                        UUID id,
                        Usuario usuarioAtualizado) {

                Usuario usuario = usuarioRepository
                                .findById(id)
                                .orElseThrow();

                usuario.setNome(
                                usuarioAtualizado.getNome());

                usuario.setEmail(
                                usuarioAtualizado.getEmail());

                if (usuarioAtualizado.getSenha() != null &&
                                !usuarioAtualizado.getSenha().isBlank()) {

                        usuario.setSenha(
                                        usuarioAtualizado.getSenha());

                }

                usuario.setAtivo(
                                usuarioAtualizado.getAtivo());

                usuario.setPerfil(
                                usuarioAtualizado.getPerfil());

                usuario.setTenant(
                                usuarioAtualizado.getTenant());

                return usuarioRepository
                                .save(usuario);

        }

        public List<Usuario> listarPorTenant(
                        UUID tenantId) {

                return usuarioRepository
                                .findByTenantId(
                                                tenantId);

        }

}