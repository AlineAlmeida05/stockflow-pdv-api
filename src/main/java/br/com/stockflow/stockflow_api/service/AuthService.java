package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.dto.request.LoginRequest;
import br.com.stockflow.stockflow_api.dto.response.LoginResponse;
import br.com.stockflow.stockflow_api.dto.response.RefreshTokenResponse;
import br.com.stockflow.stockflow_api.dto.response.UsuarioAtualResponse;
import br.com.stockflow.stockflow_api.entity.Perfil;
import br.com.stockflow.stockflow_api.entity.RefreshToken;
import br.com.stockflow.stockflow_api.entity.Tenant;
import br.com.stockflow.stockflow_api.entity.Usuario;
import br.com.stockflow.stockflow_api.exception.RecursoNaoEncontradoException;
import br.com.stockflow.stockflow_api.repository.TenantRepository;
import br.com.stockflow.stockflow_api.repository.UsuarioRepository;
import br.com.stockflow.stockflow_api.security.UsuarioAutenticadoService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import br.com.stockflow.stockflow_api.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final TenantRepository tenantRepository;

    private final RefreshTokenService refreshTokenService;

    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            TenantRepository tenantRepository,
            RefreshTokenService refreshTokenService,
            UsuarioAutenticadoService usuarioAutenticadoService) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tenantRepository = tenantRepository;
        this.refreshTokenService = refreshTokenService;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    public LoginResponse login(
            LoginRequest request,
            HttpServletResponse response) {

        Usuario usuario = usuarioRepository
                .findByEmail(
                        request.email())
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Usuário ou senha inválidos."));


        boolean senhaValida = passwordEncoder.matches(
                request.senha(),
                usuario.getSenha());


        Tenant tenantLogin = tenantRepository
                .findBySlug(request.slug())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Tenant não encontrado."
                        ));

        if (
                usuario.getPerfil() != Perfil.SUPER_ADMIN
                        &&
                        !Boolean.TRUE.equals(
                                tenantLogin.getAtivo()
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Tenant inativo."
            );

        }

        if (
                usuario.getPerfil()
                        != Perfil.SUPER_ADMIN
                        &&
                        !usuario.getTenant()
                                .getSlug()
                                .equals(
                                        request.slug()
                                )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário ou senha inválidos."
            );

        }

        if (!senhaValida) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário ou senha inválidos.");

        }



        String token = jwtService.gerarToken(
                usuario);

        RefreshToken refreshToken =
                refreshTokenService.criarToken(
                        usuario);

        response.addHeader(
                "Set-Cookie",
                "refreshToken="
                        + refreshToken.getToken()
                        + "; HttpOnly; Path=/; Max-Age=604800; SameSite=Lax"
        );

        return new LoginResponse(

                usuario.getId()
                        .toString(),

                usuario.getNome(),

                usuario.getEmail(),

                usuario.getPerfil()
                        .name(),

                tenantLogin.getId()
                        .toString(),

                tenantLogin.getNome(),

                token


        );

    }

    public RefreshTokenResponse refreshToken(
            String refreshTokenValue) {

        RefreshToken refreshToken =
                refreshTokenService
                        .buscarPorToken(
                                refreshTokenValue)
                        .orElseThrow(
                                () -> new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Refresh token inválido."
                                )
                        );

        if (refreshTokenService.expirado(
                refreshToken)) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Refresh token expirado."
            );
        }

        String novoToken =
                jwtService.gerarToken(
                        refreshToken.getUsuario());

        return new RefreshTokenResponse(
                novoToken
        );
    }

    public UsuarioAtualResponse usuarioAtual() {

        Usuario usuarioLogado =
                obterUsuarioLogado();

        return new UsuarioAtualResponse(

                usuarioLogado.getId().toString(),

                usuarioLogado.getNome(),

                usuarioLogado.getEmail(),

                usuarioLogado.getPerfil().name(),

                usuarioLogado.getTenant()
                        .getId()
                        .toString(),

                usuarioLogado.getTenant()
                        .getNome()
        );
    }

    public void logout(
            String refreshTokenValue,
            HttpServletResponse response) {

        if (refreshTokenValue != null) {

            refreshTokenService
                    .buscarPorToken(
                            refreshTokenValue
                    )
                    .ifPresent(
                            refreshTokenService::remover
                    );
        }

        response.addHeader(
                "Set-Cookie",
                "refreshToken=; HttpOnly; Path=/; Max-Age=0; SameSite=Lax"
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