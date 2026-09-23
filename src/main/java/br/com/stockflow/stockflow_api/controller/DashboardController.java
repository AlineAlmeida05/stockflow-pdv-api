package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.dto.response.DashboardResponse;
import br.com.stockflow.stockflow_api.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public DashboardResponse obterDashboard() {

        return dashboardService
                .obterDashboard();

    }

}