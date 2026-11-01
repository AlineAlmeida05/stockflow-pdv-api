package br.controller;

import br.dto.response.MenuBadgeResponse;
import br.service.NotificacaoService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(
            NotificacaoService notificacaoService
    ) {

        this.notificacaoService =
                notificacaoService;

    }

    @GetMapping("/menu")
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','OPERADOR_CAIXA','ESTOQUISTA')"
    )
    public List<MenuBadgeResponse> menu() {

        return notificacaoService
                .obterBadgesMenu();

    }

}