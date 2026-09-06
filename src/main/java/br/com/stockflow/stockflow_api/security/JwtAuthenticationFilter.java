package br.com.stockflow.stockflow_api.security;

import br.com.stockflow.stockflow_api.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import br.com.stockflow.stockflow_api.entity.Usuario;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UsuarioRepository usuarioRepository) {

        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;

    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader(
                "Authorization");

        if (authorizationHeader != null &&
                authorizationHeader.startsWith(
                        "Bearer ")) {

            String token = authorizationHeader.substring(7);

            boolean tokenValido = jwtService.validarToken(
                    token);

            if (tokenValido) {

                String email = jwtService.extrairEmail(
                        token);

                usuarioRepository
                        .findByEmail(email)
                        .ifPresent(usuario -> {

                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    usuario,
                                    null,
                                    Collections.emptyList());

                            SecurityContextHolder
                                    .getContext()
                                    .setAuthentication(
                                            authentication);

                        });

            }

        }

        filterChain.doFilter(
                request,
                response);

    }

}