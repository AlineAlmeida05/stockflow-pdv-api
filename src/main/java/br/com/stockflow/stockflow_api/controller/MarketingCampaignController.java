package br.com.stockflow.stockflow_api.controller;

import br.com.stockflow.stockflow_api.dto.request.MarketingCampaignCreateRequest;
import br.com.stockflow.stockflow_api.dto.response.MarketingCampaignResponse;
import br.com.stockflow.stockflow_api.service.MarketingCampaignService;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/marketing/campaigns")
@RequiredArgsConstructor
public class MarketingCampaignController {

    private final MarketingCampaignService
            marketingCampaignService;

    @PostMapping
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
    public List<MarketingCampaignResponse> listar() {

        return marketingCampaignService
                .listar();

    }

}