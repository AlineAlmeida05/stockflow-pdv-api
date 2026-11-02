package br.controller;

import br.dto.response.RefreshTokenResponse;
import br.dto.response.UsuarioAtualResponse;
import br.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import br.dto.request.LoginRequest;
import br.dto.response.LoginResponse;
import org.springframework.web.bind.annotation.CookieValue;
import br.dto.request.ForgotPasswordRequest;
import br.dto.request.ResetPasswordRequest;
import br.dto.response.TokenValidoResponse;

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

    @PostMapping("/forgot-password")
    public void forgotPassword(

            @Valid
            @RequestBody
            ForgotPasswordRequest request

    ) {

        authService
                .solicitarRecuperacaoSenha(
                        request
                );

    }

    @PostMapping("/reset-password")
    public void resetPassword(

            @Valid
            @RequestBody
            ResetPasswordRequest request

    ) {

        authService
                .redefinirSenha(
                        request
                );

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

    @GetMapping("/reset-password/validar")
    public TokenValidoResponse validarToken(

            @RequestParam String token

    ) {

        return authService
                .validarTokenRecuperacao(
                        token
                );

    }

}