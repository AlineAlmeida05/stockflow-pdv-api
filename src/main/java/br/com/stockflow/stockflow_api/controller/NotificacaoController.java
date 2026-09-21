package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.dto.response.MenuBadgeResponse;
import br.com.stockflow.stockflow_api.service.NotificacaoService;

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
    public List<MenuBadgeResponse> menu() {

        return notificacaoService
                .obterBadgesMenu();

    }

}