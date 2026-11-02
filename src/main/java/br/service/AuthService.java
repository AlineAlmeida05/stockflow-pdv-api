package br.service;

import br.dto.request.LoginRequest;
import br.dto.response.LoginResponse;
import br.dto.response.RefreshTokenResponse;
import br.dto.response.UsuarioAtualResponse;
import br.entity.Perfil;
import br.entity.RefreshToken;
import br.entity.Tenant;
import br.entity.Usuario;
import br.exception.RecursoNaoEncontradoException;
import br.repository.TenantRepository;
import br.repository.UsuarioRepository;
import br.security.UsuarioAutenticadoService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import br.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import br.dto.request.ForgotPasswordRequest;
import br.dto.request.ResetPasswordRequest;
import br.entity.PasswordResetToken;
import br.dto.response.TokenValidoResponse;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TenantRepository tenantRepository;
    private final RefreshTokenService refreshTokenService;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailService emailService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            TenantRepository tenantRepository,
            RefreshTokenService refreshTokenService,
            UsuarioAutenticadoService usuarioAutenticadoService,
            PasswordResetTokenService passwordResetTokenService,
            EmailService emailService) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tenantRepository = tenantRepository;
        this.refreshTokenService = refreshTokenService;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.emailService = emailService;
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

        String token = jwtService.gerarToken(usuario);

        RefreshToken refreshToken = refreshTokenService.criarToken(
                        usuario);

        response.addHeader(
                "Set-Cookie",
                "refreshToken="
                        + refreshToken.getToken()
                        + "; HttpOnly; Path=/; Max-Age=604800; SameSite=Lax"
        );

        return new LoginResponse(

                usuario.getId().toString(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil().name(),
                tenantLogin.getId().toString(),
                tenantLogin.getNome(),

                token
        );

    }

    public RefreshTokenResponse refreshToken(
            String refreshTokenValue) {

        RefreshToken refreshToken = refreshTokenService
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

        String novoToken = jwtService.gerarToken(
                        refreshToken.getUsuario());

        return new RefreshTokenResponse(
                novoToken
        );
    }

    public UsuarioAtualResponse usuarioAtual() {

        Usuario usuarioLogado = obterUsuarioLogado();

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

        Usuario usuarioLogado = usuarioAutenticadoService
                        .usuarioLogado();

        if (usuarioLogado == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado."
            );

        }

        return usuarioLogado;

    }

    public void solicitarRecuperacaoSenha(
            ForgotPasswordRequest request) {

        Usuario usuario = usuarioRepository
                .findByEmail(
                        request.email()
                )
                .orElse(null);

        if (usuario == null) {

            return;

        }

        PasswordResetToken token =
                passwordResetTokenService
                        .criarToken(
                                usuario
                        );

        String link =
                "http://localhost:4200/reset-password"
                        + "?token="
                        + token.getToken()
                        + "&slug="
                        + request.slug();

        Tenant tenant = tenantRepository
                .findBySlug(
                        request.slug()
                )
                .orElse(null);

        String assunto =
                tenant != null
                        ? tenant.getNome() + " - Recuperação de senha"
                        : "Recuperação de senha";

        emailService.enviar(

                usuario.getEmail(),

                assunto,

                """
                Olá!
        
                Recebemos uma solicitação para redefinição de senha.
        
                Utilize o link abaixo para criar uma nova senha:
        
                %s
        
                Este link expira em 1 hora.
        
                Caso você não tenha solicitado esta alteração,
                ignore esta mensagem.
                """
                        .formatted(link)

        );

    }

    public void redefinirSenha(
            ResetPasswordRequest request) {

        PasswordResetToken token = passwordResetTokenService
                        .buscarPorToken(
                                request.token()
                        )
                        .orElseThrow(
                                () -> new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Token inválido."
                                )
                        );

        if (
                passwordResetTokenService.expirado(
                        token
                )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Token expirado."
            );

        }

        if (
                passwordResetTokenService.utilizado(
                        token
                )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Token já utilizado."
            );

        }

        Usuario usuario = token.getUsuario();

        usuario.setSenha(
                passwordEncoder.encode(
                        request.novaSenha()
                )
        );

        usuarioRepository.save(usuario);

        passwordResetTokenService
                .marcarComoUtilizado(
                        token
                );
    }

    public TokenValidoResponse validarTokenRecuperacao(
            String token) {

        return passwordResetTokenService
                .buscarPorToken(
                        token
                )
                .map(passwordResetToken ->
                        new TokenValidoResponse(
                                passwordResetTokenService
                                        .tokenValido(
                                                passwordResetToken
                                        )
                        )
                )
                .orElse(
                        new TokenValidoResponse(
                                false
                        )
                );

    }

}