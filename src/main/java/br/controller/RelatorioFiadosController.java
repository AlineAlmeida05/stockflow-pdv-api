package br.controller;

import br.dto.response.relatorio.DashboardFiados;
import br.service.RelatorioFiadosService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorios/fiados")
public class RelatorioFiadosController {

    private final RelatorioFiadosService
            relatorioFiadosService;

    public RelatorioFiadosController(
            RelatorioFiadosService relatorioFiadosService) {

        this.relatorioFiadosService =
                relatorioFiadosService;
    }

    @GetMapping
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE')"
    )
    public ResponseEntity<DashboardFiados>
    dashboard() {

        return ResponseEntity.ok(

                relatorioFiadosService
                        .gerarDashboard()

        );
    }
}