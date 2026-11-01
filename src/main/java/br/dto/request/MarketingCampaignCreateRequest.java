package br.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MarketingCampaignCreateRequest(

        @NotNull
        UUID produtoId,

        String tom,

        String tipoPromocao,

        String elementoVisual,

        String observacoes

) {
}