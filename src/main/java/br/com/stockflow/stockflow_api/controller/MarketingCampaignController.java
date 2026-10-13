package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.dto.request.MarketingCampaignCreateRequest;
import br.com.stockflow.stockflow_api.dto.response.MarketingCampaignResponse;
import br.com.stockflow.stockflow_api.service.MarketingCampaignService;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/marketing/campaigns")
@RequiredArgsConstructor
public class MarketingCampaignController {

    private final MarketingCampaignService
            marketingCampaignService;

    @PostMapping
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE')"
    )
    public MarketingCampaignResponse gerarCampanha(

            @Valid
            @RequestBody
            MarketingCampaignCreateRequest request

    ) {

        return marketingCampaignService
                .gerarCampanha(
                        request
                );

    }

    @GetMapping
    @PreAuthorize(
            "hasAnyRole('SUPER_ADMIN','PROPRIETARIO','SOCIO','GERENTE')"
    )
    public List<MarketingCampaignResponse> listar() {

        return marketingCampaignService
                .listar();

    }

}