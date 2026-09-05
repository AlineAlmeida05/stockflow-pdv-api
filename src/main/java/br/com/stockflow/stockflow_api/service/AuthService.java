package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.dto.LoginRequest;
import br.com.stockflow.stockflow_api.dto.LoginResponse;
import br.com.stockflow.stockflow_api.entity.Usuario;
import br.com.stockflow.stockflow_api.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;

        this.passwordEncoder = passwordEncoder;

    }

    public LoginResponse login(
            LoginRequest request) {

        Usuario usuario = usuarioRepository
                .findByEmail(
                        request.email())
                .orElseThrow(
                        () -> new RuntimeException(
                                "Usuário ou senha inválidos."));

        boolean senhaValida = passwordEncoder.matches(
                request.senha(),
                usuario.getSenha());

        if (!senhaValida) {
            throw new RuntimeException("Usuário ou senha inválidos.");
        }

        return new LoginResponse(

                usuario.getId()
                        .toString(),

                usuario.getNome(),

                usuario.getEmail(),

                usuario.getPerfil()
                        .name(),

                usuario.getTenant()
                        .getId()
                        .toString(),

                usuario.getTenant()
                        .getNome()

        );

    }

}