package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.dto.relatorio.DashboardVendas;
import br.com.stockflow.stockflow_api.service.RelatorioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/relatorios/vendas")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping
    public ResponseEntity<DashboardVendas> relatorio(
            @RequestParam UUID inquilinoId,
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim,
            @RequestParam(required = false) String formaPagamento) {
        return ResponseEntity.ok(
                relatorioService.gerarDashboard(inquilinoId, inicio, fim, formaPagamento));
    }
}