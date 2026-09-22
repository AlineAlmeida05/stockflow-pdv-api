package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.dto.request.LoginRequest;
import br.com.stockflow.stockflow_api.dto.response.LoginResponse;
import br.com.stockflow.stockflow_api.entity.Perfil;
import br.com.stockflow.stockflow_api.entity.Tenant;
import br.com.stockflow.stockflow_api.entity.Usuario;
import br.com.stockflow.stockflow_api.exception.RecursoNaoEncontradoException;
import br.com.stockflow.stockflow_api.repository.TenantRepository;
import br.com.stockflow.stockflow_api.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import br.com.stockflow.stockflow_api.security.JwtService;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final TenantRepository tenantRepository;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            TenantRepository tenantRepository) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tenantRepository = tenantRepository;
    }

    public LoginResponse login(
            LoginRequest request) {

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

        return new LoginResponse(

                usuario.getId()
                        .toString(),

                usuario.getNome(),

                usuario.getEmail(),

                usuario.getPerfil()
                        .name(),

                tenantLogin.getId().toString(),

                tenantLogin.getNome(),

                token

        );

    }

}