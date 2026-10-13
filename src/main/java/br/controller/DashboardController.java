package br.controller;

import br.dto.response.DashboardResponse;
import br.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(
        "/api/dashboard"
)
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService
    ) {

        this.dashboardService =
                dashboardService;

    }

    @GetMapping
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE','OPERADOR_CAIXA','ESTOQUISTA')"
    )
    public DashboardResponse dashboard(

            @RequestParam(
                    defaultValue = "hoje"
            )
            String periodo

    ) {

        return dashboardService
                .obterDashboard(
                        periodo
                );

    }

}