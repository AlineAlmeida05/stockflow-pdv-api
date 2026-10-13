package br.service;

import br.entity.Usuario;
import br.exception.AcessoNegadoException;
import br.exception.RecursoNaoEncontradoException;
import br.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.UUID;
import br.security.UsuarioAutenticadoService;
import br.entity.Perfil;
import br.dto.request.AlterarSenhaRequest;
import br.exception.RegraNegocioException;
import br.dto.request.UsuarioCreateRequest;
import br.dto.request.UsuarioUpdateRequest;
import br.entity.Tenant;
import br.repository.TenantRepository;
import br.dto.response.UsuarioResponse;
import org.springframework.web.server.ResponseStatusException;


@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final TenantRepository tenantRepository;
    private final AuditoriaService auditoriaService;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            UsuarioAutenticadoService usuarioAutenticadoService,
            TenantRepository tenantRepository,
            AuditoriaService auditoriaService) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.tenantRepository = tenantRepository;
        this.auditoriaService = auditoriaService;
    }


    public List<UsuarioResponse> listarTodos() {

        Usuario usuarioLogado =
                obterUsuarioLogado();

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

        Usuario usuarioLogado =
                obterUsuarioLogado();

        if (usuario.getTenant() == null) {

            throw new RegraNegocioException(
                    "Tenant é obrigatório."
            );

        }

        if (usuarioLogado.getPerfil() != Perfil.SUPER_ADMIN
                &&
                !usuarioLogado.getTenant()
                        .getId()
                        .equals(
                                usuario.getTenant()
                                        .getId())) {

            throw new AcessoNegadoException(
                    "Você não pode criar usuários em outro tenant.");

        }
        if (!podeGerenciarPerfil(
                usuarioLogado.getPerfil(),
                usuario.getPerfil())) {

            throw new AcessoNegadoException(
                    "Você não possui permissão para criar este perfil.");

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

        Usuario usuarioSalvo =
                usuarioRepository.save(usuario);

        auditoriaService.registrar(
                usuarioLogado.getTenant().getId(),
                usuarioLogado.getId(),
                usuarioLogado.getNome(),
                "USUARIO_CRIADO",
                "Usuario",
                usuarioSalvo.getId().toString(),
                "Usuário "
                        + usuarioSalvo.getNome()
                        + " criado com perfil "
                        + usuarioSalvo.getPerfil()
        );

        return montarResponse(usuarioSalvo);

    }


    public void excluir(UUID id) {

        Usuario usuarioAlvo = usuarioRepository
                .findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado."
                        ));

        Usuario usuarioLogado =
                obterUsuarioLogado();

        if (usuarioLogado.getPerfil() != Perfil.SUPER_ADMIN
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

        if (!podeGerenciarPerfil(
                usuarioLogado.getPerfil(),
                usuarioAlvo.getPerfil())) {

            throw new AcessoNegadoException(
                    "Você não possui permissão para excluir este usuário.");

        }

        usuarioAlvo.setAtivo(false);

        usuarioRepository.save(
                usuarioAlvo
        );

        auditoriaService.registrar(
                usuarioLogado.getTenant().getId(),
                usuarioLogado.getId(),
                usuarioLogado.getNome(),
                "USUARIO_INATIVADO",
                "Usuario",
                usuarioAlvo.getId().toString(),
                "Usuário "
                        + usuarioAlvo.getNome()
                        + " inativado."
        );

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

        Usuario usuarioLogado =
                obterUsuarioLogado();

        if (usuarioLogado.getPerfil() != Perfil.SUPER_ADMIN
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

        if (!podeGerenciarPerfil(
                usuarioLogado.getPerfil(),
                usuario.getPerfil())) {

            throw new AcessoNegadoException(
                    "Você não possui permissão para editar este usuário.");

        }


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

        Usuario usuarioAtualizado =
                usuarioRepository.save(usuario);

        auditoriaService.registrar(
                usuarioLogado.getTenant().getId(),
                usuarioLogado.getId(),
                usuarioLogado.getNome(),
                "USUARIO_EDITADO",
                "Usuario",
                usuarioAtualizado.getId().toString(),
                "Usuário "
                        + usuarioAtualizado.getNome()
                        + " atualizado."
        );

        return montarResponse(usuarioAtualizado);

    }


    public List<UsuarioResponse> listarPorTenant(
            UUID tenantId) {

        Usuario usuarioLogado =
                obterUsuarioLogado();

        if (usuarioLogado.getPerfil() != Perfil.SUPER_ADMIN
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

        Usuario usuarioLogado =
                obterUsuarioLogado();

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

        auditoriaService.registrar(
                usuarioLogado.getTenant().getId(),
                usuarioLogado.getId(),
                usuarioLogado.getNome(),
                "SENHA_ALTERADA",
                "Usuario",
                usuarioLogado.getId().toString(),
                "Senha alterada pelo usuário."
        );

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

    private Usuario obterUsuarioLogado() {

        Usuario usuarioLogado =
                usuarioAutenticadoService
                        .usuarioLogado();

        if (usuarioLogado == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado."
            );

        }

        return usuarioLogado;

    }

}