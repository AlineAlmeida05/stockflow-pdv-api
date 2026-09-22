package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.entity.Usuario;
import br.com.stockflow.stockflow_api.exception.AcessoNegadoException;
import br.com.stockflow.stockflow_api.exception.RecursoNaoEncontradoException;
import br.com.stockflow.stockflow_api.repository.UsuarioRepository;

import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;

import br.com.stockflow.stockflow_api.entity.Perfil;
import br.com.stockflow.stockflow_api.dto.request.AlterarSenhaRequest;
import br.com.stockflow.stockflow_api.exception.RegraNegocioException;
import br.com.stockflow.stockflow_api.dto.request.UsuarioCreateRequest;
import br.com.stockflow.stockflow_api.dto.request.UsuarioUpdateRequest;
import br.com.stockflow.stockflow_api.entity.Tenant;
import br.com.stockflow.stockflow_api.repository.TenantRepository;
import br.com.stockflow.stockflow_api.dto.response.UsuarioResponse;


@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final TenantRepository tenantRepository;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            UsuarioAutenticadoService usuarioAutenticadoService,
            TenantRepository tenantRepository) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.tenantRepository = tenantRepository;

    }


    public List<UsuarioResponse> listarTodos() {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        if (
                usuarioLogado.getPerfil()
                        == Perfil.SUPER_ADMIN
        ) {

            return usuarioRepository
                    .findAll()
                    .stream()
                    .map(this::montarResponse)
                    .toList();

        }

        return usuarioRepository
                .findByTenantId(
                        usuarioLogado
                                .getTenant()
                                .getId()
                )
                .stream()
                .map(this::montarResponse)
                .toList();

    }


    public UsuarioResponse salvar(
            UsuarioCreateRequest request) {

        Tenant tenant = tenantRepository
                .findById(request.tenantId())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Tenant não encontrado."
                        ));

        Usuario usuario = new Usuario();

        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(request.senha());
        usuario.setPerfil(request.perfil());
        usuario.setTenant(tenant);

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

            throw new AcessoNegadoException(
                    "Você não pode criar usuários em outro tenant.");

        }
        if (usuarioLogado != null) {

            if (!podeGerenciarPerfil(
                    usuarioLogado.getPerfil(),
                    usuario.getPerfil())) {

                throw new AcessoNegadoException(
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

        return montarResponse(
                usuarioRepository.save(usuario)
        );

    }


    public void excluir(UUID id) {

        Usuario usuarioAlvo = usuarioRepository
                .findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado."
                        ));

        Usuario usuarioLogado = usuarioAutenticadoService
                .usuarioLogado();

        if (usuarioLogado != null
                &&
                usuarioLogado.getPerfil() != Perfil.SUPER_ADMIN
                &&
                !pertenceAoMesmoTenant(
                        usuarioLogado,
                        usuarioAlvo)) {

            throw new AcessoNegadoException(
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

                throw new AcessoNegadoException(
                        "Você não possui permissão para excluir este usuário.");

            }

        }

        usuarioRepository.deleteById(id);

    }


    public UsuarioResponse atualizar(
            UUID id,
            UsuarioUpdateRequest request) {

        Usuario usuario = usuarioRepository
                .findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado."
                        ));

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

            throw new AcessoNegadoException(
                    "Você não possui permissão para editar usuários de outro tenant."
            );

        }

        if (usuarioLogado != null) {

            if (!podeGerenciarPerfil(
                    usuarioLogado.getPerfil(),
                    usuario.getPerfil())) {

                throw new AcessoNegadoException(
                        "Você não possui permissão para editar este usuário.");

            }

        }
        usuario.setNome(
                request.nome());

        usuarioRepository
                .findByEmail(
                        request.email()
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
                request.email());

        if (request.senha() != null &&
                !request.senha().isBlank()) {

            usuario.setSenha(
                    passwordEncoder.encode(
                            request.senha()));

        }

        usuario.setAtivo(
                request.ativo());

        if (usuarioLogado != null) {

            if (!podeGerenciarPerfil(
                    usuarioLogado.getPerfil(),
                    request.perfil())) {

                throw new AcessoNegadoException(
                        "Você não possui permissão para atribuir este perfil.");

            }


            usuario.setPerfil(
                    request.perfil()
            );

            if (usuarioLogado.getPerfil() == Perfil.SUPER_ADMIN) {

                if (request.tenantId() != null) {

                    Tenant tenant = tenantRepository
                            .findById(request.tenantId())
                            .orElseThrow(() ->
                                    new RecursoNaoEncontradoException(
                                            "Tenant não encontrado."
                                    ));

                    usuario.setTenant(tenant);

                }

            }

        }

        return montarResponse(
                usuarioRepository.save(usuario)
        );

    }


    public List<UsuarioResponse> listarPorTenant(
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

            throw new AcessoNegadoException(
                    "Você não possui acesso a este tenant.");

        }

        return usuarioRepository
                .findByTenantId(
                        tenantId
                )
                .stream()
                .map(this::montarResponse)
                .toList();

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

    private UsuarioResponse montarResponse(
            Usuario usuario
    ) {

        return new UsuarioResponse(

                usuario.getId(),

                usuario.getNome(),

                usuario.getEmail(),

                usuario.getPerfil(),

                usuario.getAtivo(),

                usuario.getTenant() != null
                        ? usuario.getTenant().getId()
                        : null

        );

    }

}