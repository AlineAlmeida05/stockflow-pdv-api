package br.com.stockflow.stockflow_api.service;

import br.com.stockflow.stockflow_api.dto.LoginRequest;
import br.com.stockflow.stockflow_api.dto.LoginResponse;
import br.com.stockflow.stockflow_api.entity.Usuario;
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

        public AuthService(
                        UsuarioRepository usuarioRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService) {

                this.usuarioRepository = usuarioRepository;

                this.passwordEncoder = passwordEncoder;

                this.jwtService = jwtService;

        }

        public LoginResponse login(
                        LoginRequest request) {

                Usuario usuario = usuarioRepository
                                .findByEmailAndTenant_Slug(
                                                request.email(),
                                                request.slug())
                                .orElseThrow(
                                                () -> new ResponseStatusException(
                                                                HttpStatus.UNAUTHORIZED,
                                                                "Usuário ou senha inválidos."));

                boolean senhaValida = passwordEncoder.matches(
                                request.senha(),
                                usuario.getSenha());

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

                                usuario.getTenant()
                                                .getId()
                                                .toString(),

                                usuario.getTenant()
                                                .getNome(),

                                token

                );

        }

}