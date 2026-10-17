package br.controller;

import br.dto.response.relatorio.DashboardVendas;
import br.service.RelatorioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/relatorios/vendas")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(
            RelatorioService relatorioService) {

        this.relatorioService = relatorioService;
    }

    @GetMapping
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE')"
    )
    public ResponseEntity<DashboardVendas> relatorio(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim,
            @RequestParam(required = false)
            String formaPagamento) {

        return ResponseEntity.ok(
                relatorioService.gerarDashboard(
                        inicio,
                        fim,
                        formaPagamento
                )
        );
    }
}