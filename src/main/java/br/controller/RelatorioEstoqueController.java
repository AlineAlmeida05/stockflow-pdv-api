package br.controller;

import br.dto.response.relatorio.DashboardEstoque;
import br.service.RelatorioEstoqueService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorios/estoque")
public class RelatorioEstoqueController {

    private final RelatorioEstoqueService
            relatorioEstoqueService;

    public RelatorioEstoqueController(
            RelatorioEstoqueService relatorioEstoqueService) {

        this.relatorioEstoqueService = relatorioEstoqueService;
    }

    @GetMapping
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE')"
    )
    public ResponseEntity<DashboardEstoque>
    dashboard() {

        return ResponseEntity.ok(

                relatorioEstoqueService
                        .gerarDashboard()

        );
    }
}