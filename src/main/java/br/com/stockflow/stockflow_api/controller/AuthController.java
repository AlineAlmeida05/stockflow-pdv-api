package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.dto.request.RefreshTokenRequest;
import br.com.stockflow.stockflow_api.dto.response.RefreshTokenResponse;
import br.com.stockflow.stockflow_api.dto.response.UsuarioAtualResponse;
import br.com.stockflow.stockflow_api.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import br.com.stockflow.stockflow_api.dto.request.LoginRequest;
import br.com.stockflow.stockflow_api.dto.response.LoginResponse;
import org.springframework.web.bind.annotation.CookieValue;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(
            AuthService authService) {

        this.authService = authService;

    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {

        return authService
                .login(
                        request,
                        response
                );

    }

    @PostMapping("/refresh")
    public RefreshTokenResponse refreshToken(
            @CookieValue("refreshToken")
            String refreshToken) {

        return authService.refreshToken(
                refreshToken
        );
    }

    @GetMapping("/me")
    public UsuarioAtualResponse me() {

        return authService.usuarioAtual();

    }

    @PostMapping("/logout")
    public void logout(
            @CookieValue(value = "refreshToken",
                    required = false)
            String refreshToken,
            HttpServletResponse response) {

        authService.logout(
                refreshToken,
                response
        );

    }

}