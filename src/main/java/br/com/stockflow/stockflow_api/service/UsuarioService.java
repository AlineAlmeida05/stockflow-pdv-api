package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.entity.Usuario;
import br.com.stockflow.stockflow_api.repository.UsuarioRepository;

import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;

import br.com.stockflow.stockflow_api.entity.Perfil;
import br.com.stockflow.stockflow_api.dto.AlterarSenhaRequest;
import br.com.stockflow.stockflow_api.exception.RegraNegocioException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.usuarioRepository = usuarioRepository;

        this.passwordEncoder = passwordEncoder;

        this.usuarioAutenticadoService = usuarioAutenticadoService;

    }


    public List<Usuario> listarTodos() {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();


        if (
                usuarioLogado.getPerfil()
                        == Perfil.SUPER_ADMIN
        ) {

            return usuarioRepository
                    .findAll();

        }

        return usuarioRepository
                .findByTenantId(
                        usuarioLogado
                                .getTenant()
                                .getId()
                );

    }


    public Usuario salvar(
            Usuario usuario) {

        Usuario usuarioLogado = usuarioAutenticadoService
                .usuarioLogado();

        if (usuario.getTenant() == null) {

            throw new RegraNegocioException(
                    "Tenant é obrigatório."
            );

        }

        if (usuarioLogado != null
                &&
                usuarioLogado.getPerfil() != Perfil.SUPER_ADMIN
                &&
                !usuarioLogado.getTenant()
                        .getId()
                        .equals(
                                usuario.getTenant()
                                        .getId())) {

            throw new RegraNegocioException(
                    "Você não pode criar usuários em outro tenant.");

        }
        if (usuarioLogado != null) {

            if (!podeGerenciarPerfil(
                    usuarioLogado.getPerfil(),
                    usuario.getPerfil())) {

                throw new RegraNegocioException(
                        "Você não possui permissão para criar este perfil.");

            }

        }

        usuarioRepository
                .findByEmail(
                        usuario.getEmail())
                .ifPresent(u -> {

                    throw new RegraNegocioException(
                            "Já existe um usuário com este e-mail.");

                });

        usuario.setSenha(
                passwordEncoder.encode(
                        usuario.getSenha()));

        return usuarioRepository
                .save(usuario);

    }


    public void excluir(UUID id) {

        Usuario usuarioAlvo = usuarioRepository
                .findById(id)
                .orElseThrow();

        Usuario usuarioLogado = usuarioAutenticadoService
                .usuarioLogado();

        if (usuarioLogado != null
                &&
                usuarioLogado.getPerfil() != Perfil.SUPER_ADMIN
                &&
                !pertenceAoMesmoTenant(
                        usuarioLogado,
                        usuarioAlvo)) {

            throw new RegraNegocioException(
                    "Você não possui permissão para excluir usuários de outro tenant.");

        }

        if (usuarioLogado.getId()
                .equals(usuarioAlvo.getId())) {

            throw new RegraNegocioException(
                    "Você não pode excluir seu próprio usuário.");

        }

        if (usuarioLogado != null) {

            if (!podeGerenciarPerfil(
                    usuarioLogado.getPerfil(),
                    usuarioAlvo.getPerfil())) {

                throw new RegraNegocioException(
                        "Você não possui permissão para excluir este usuário.");

            }

        }

        usuarioRepository.deleteById(id);

    }


    public Usuario atualizar(UUID id, Usuario usuarioAtualizado) {

        Usuario usuario = usuarioRepository
                .findById(id)
                .orElseThrow();

        Usuario usuarioLogado = usuarioAutenticadoService
                .usuarioLogado();

        if (
                usuarioLogado != null
                        &&
                        usuarioLogado.getPerfil() != Perfil.SUPER_ADMIN
                        &&
                        !pertenceAoMesmoTenant(
                                usuarioLogado,
                                usuario
                        )
        ) {

            throw new RegraNegocioException(
                    "Você não possui permissão para editar usuários de outro tenant."
            );

        }

        if (usuarioLogado != null) {

            if (!podeGerenciarPerfil(
                    usuarioLogado.getPerfil(),
                    usuario.getPerfil())) {

                throw new RegraNegocioException(
                        "Você não possui permissão para editar este usuário.");

            }

        }
        usuario.setNome(
                usuarioAtualizado.getNome());

        usuarioRepository
                .findByEmail(
                        usuarioAtualizado.getEmail()
                )
                .ifPresent(existente -> {

                    if (
                            !existente.getId()
                                    .equals(usuario.getId())
                    ) {

                        throw new RegraNegocioException(
                                "Já existe um usuário com este e-mail."
                        );

                    }

                });

        usuario.setEmail(
                usuarioAtualizado.getEmail());

        if (usuarioAtualizado.getSenha() != null &&
                !usuarioAtualizado.getSenha().isBlank()) {

            usuario.setSenha(
                    passwordEncoder.encode(
                            usuarioAtualizado.getSenha()));

        }

        usuario.setAtivo(
                usuarioAtualizado.getAtivo());

        if (usuarioLogado != null) {

            if (!podeGerenciarPerfil(
                    usuarioLogado.getPerfil(),
                    usuarioAtualizado.getPerfil())) {

                throw new RegraNegocioException(
                        "Você não possui permissão para atribuir este perfil.");

            }


            usuario.setPerfil(
                    usuarioAtualizado.getPerfil()
            );

            if (usuarioLogado.getPerfil() == Perfil.SUPER_ADMIN) {

                usuario.setTenant(
                        usuarioAtualizado.getTenant());

            }

        }

        return usuarioRepository
                .save(usuario);

    }


    public List<Usuario> listarPorTenant(
            UUID tenantId) {

        Usuario usuarioLogado = usuarioAutenticadoService
                .usuarioLogado();

        if (usuarioLogado != null
                &&
                usuarioLogado.getPerfil() != Perfil.SUPER_ADMIN
                &&
                !usuarioLogado.getTenant()
                        .getId()
                        .equals(tenantId)) {

            throw new RegraNegocioException(
                    "Você não possui acesso a este tenant.");

        }

        return usuarioRepository
                .findByTenantId(
                        tenantId);

    }


    private boolean pertenceAoMesmoTenant(
            Usuario usuario1,
            Usuario usuario2) {

        return usuario1.getTenant()
                .getId()
                .equals(
                        usuario2.getTenant()
                                .getId());

    }


    private boolean podeGerenciarPerfil(
            Perfil perfilLogado,
            Perfil perfilAlvo) {

        return perfilLogado.getNivel() > perfilAlvo.getNivel();

    }


    public void alterarSenha(
            AlterarSenhaRequest request) {

        Usuario usuarioLogado = usuarioAutenticadoService
                .usuarioLogado();

        if (usuarioLogado == null) {

            throw new RegraNegocioException(
                    "Usuário não autenticado.");

        }

        if (request.novaSenha().isBlank()) {

            throw new RegraNegocioException(
                    "A nova senha é obrigatória.");

        }

        if (request.novaSenha().length() < 6) {

            throw new RegraNegocioException(
                    "A senha deve possuir pelo menos 6 caracteres.");

        }

        boolean senhaCorreta = passwordEncoder.matches(
                request.senhaAtual(),
                usuarioLogado.getSenha());

        if (!senhaCorreta) {

            throw new RegraNegocioException(
                    "Senha atual inválida.");

        }

        if (!request.novaSenha()
                .equals(request.confirmarSenha())) {

            throw new RegraNegocioException(
                    "As senhas não conferem.");

        }

        usuarioLogado.setSenha(
                passwordEncoder.encode(
                        request.novaSenha()));

        usuarioRepository.save(
                usuarioLogado);

    }

}